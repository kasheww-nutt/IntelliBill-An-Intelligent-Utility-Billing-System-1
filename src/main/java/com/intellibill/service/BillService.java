package com.intellibill.service;

import com.intellibill.database.BillDAO;
import com.intellibill.exception.ConsumerNotFoundException;
import com.intellibill.exception.InvalidReadingException;
import com.intellibill.model.Bill;
import com.intellibill.model.ElectricityService;
import com.intellibill.model.InternetService;
import com.intellibill.model.UtilityService;
import com.intellibill.model.WaterService;
import com.intellibill.strategy.ElectricityBillingStrategy;
import com.intellibill.strategy.InternetBillingStrategy;
import com.intellibill.strategy.WaterBillingStrategy;
import com.intellibill.util.IdGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillService {
    private static final double LATE_FEE_PERCENTAGE = 0.02;

    private final InMemoryStore store = InMemoryStore.getInstance();
    private final ConsumerService consumerService;
    private final BillDAO billDAO = new BillDAO();
    private final FileService fileService = new FileService();
    private final Map<Integer, UtilityService> serviceCatalog = new HashMap<>();

    public BillService(ConsumerService consumerService) {
        this.consumerService = consumerService;
        serviceCatalog.put(1, new ElectricityService(1, new ElectricityBillingStrategy()));
        serviceCatalog.put(2, new WaterService(2, new WaterBillingStrategy()));
        serviceCatalog.put(3, new InternetService(3, new InternetBillingStrategy()));
    }

    public Bill generateBill(int consumerId, int serviceId, double previousReading, double currentReading)
            throws Exception {
        if (previousReading < 0 || currentReading < 0) {
            throw new InvalidReadingException("Meter readings cannot be negative.");
        }
        if (currentReading < previousReading) {
            throw new InvalidReadingException("Current reading cannot be less than previous reading.");
        }

        consumerService.getConsumerById(consumerId);
        UtilityService utilityService = getServiceById(serviceId);

        double units = currentReading - previousReading;
        double amount = utilityService.getBillingStrategy().calculateBill(units);

        Bill bill = new Bill();
        bill.setBillId(IdGenerator.nextBillId());
        bill.setConsumerId(consumerId);
        bill.setServiceId(serviceId);
        bill.setPreviousReading(previousReading);
        bill.setCurrentReading(currentReading);
        bill.setUnitsConsumed(units);
        bill.setAmount(amount);
        bill.setPenalty(0);
        bill.setPaidAmount(0);
        bill.setBillDate(LocalDate.now());
        bill.setDueDate(LocalDate.now().plusDays(15));
        bill.setStatus("UNPAID");

        store.getBills().add(bill);
        store.getBillMap().put(bill.getBillId(), bill);
        persistBill(bill);
        return bill;
    }

    public void addBill(Bill bill) {
        store.getBills().add(bill);
        store.getBillMap().put(bill.getBillId(), bill);
        IdGenerator.syncBillCounter(bill.getBillId());
    }

    public UtilityService getServiceById(int serviceId) throws InvalidReadingException {
        UtilityService service = serviceCatalog.get(serviceId);
        if (service == null) {
            throw new InvalidReadingException("Invalid service ID. Use 1=Electricity, 2=Water, 3=Internet.");
        }
        return service;
    }

    public List<Bill> getAllBills() {
        applyLatePenalties();
        return new ArrayList<>(store.getBills());
    }

    public Bill getBillById(int billId) {
        return store.getBillMap().get(billId);
    }

    public void deleteBill(int billId) throws Exception {
        Bill bill = store.getBillMap().get(billId);
        if (bill == null) {
            throw new InvalidReadingException("Bill not found with ID: " + billId);
        }
        store.getBills().remove(bill);
        store.getBillMap().remove(billId);
        store.getPayments().removeIf(p -> p.getBillId() == billId);
        store.getPaymentMap().entrySet().removeIf(entry -> entry.getValue().getBillId() == billId);

        if (ServiceRegistry.ensureDatabaseMode()) {
            billDAO.deleteById(billId);
        } else {
            fileService.saveAll();
        }
    }

    public List<Bill> getBillsByConsumer(int consumerId) {
        applyLatePenalties();
        List<Bill> result = new ArrayList<>();
        for (Bill bill : store.getBills()) {
            if (bill.getConsumerId() == consumerId) {
                result.add(bill);
            }
        }
        return result;
    }

    public String compareConsumption(int consumerId, int serviceId) {
        List<Bill> consumerBills = new ArrayList<>();
        for (Bill bill : store.getBills()) {
            if (bill.getConsumerId() == consumerId && bill.getServiceId() == serviceId) {
                consumerBills.add(bill);
            }
        }
        if (consumerBills.size() < 2) {
            return "Not enough bill history to compare consumption.";
        }

        Bill latest = consumerBills.get(consumerBills.size() - 1);
        Bill previous = consumerBills.get(consumerBills.size() - 2);
        double diff = latest.getUnitsConsumed() - previous.getUnitsConsumed();

        if (diff > 0) {
            return "Consumption increased by " + diff + " units compared to previous bill.";
        }
        if (diff < 0) {
            return "Consumption decreased by " + Math.abs(diff) + " units compared to previous bill.";
        }
        return "Consumption is unchanged from previous bill.";
    }

    public void applyLatePenalties() {
        LocalDate today = LocalDate.now();
        for (Bill bill : store.getBills()) {
            if (!"PAID".equalsIgnoreCase(bill.getStatus()) && today.isAfter(bill.getDueDate())) {
                double expectedPenalty = bill.getAmount() * LATE_FEE_PERCENTAGE;
                bill.setPenalty(Math.max(bill.getPenalty(), expectedPenalty));
                if (bill.getBalance() > 0) {
                    bill.setStatus("OVERDUE");
                }
            }
        }
    }

    public Map<Integer, UtilityService> getServiceCatalog() {
        return serviceCatalog;
    }

    public void persistBill(Bill bill) throws Exception {
        if (ServiceRegistry.ensureDatabaseMode()) {
            billDAO.upsert(bill);
        } else {
            fileService.saveAll();
        }
    }
}
