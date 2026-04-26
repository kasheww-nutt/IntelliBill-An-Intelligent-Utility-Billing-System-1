package com.intellibill.service;

import com.intellibill.database.PaymentDAO;
import com.intellibill.model.Bill;
import com.intellibill.model.Payment;
import com.intellibill.util.IdGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentService {
    private final InMemoryStore store = InMemoryStore.getInstance();
    private final BillService billService;
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final FileService fileService = new FileService();

    public PaymentService(BillService billService) {
        this.billService = billService;
    }

    public Payment recordPayment(int billId, double amount, String mode) throws Exception {
        Bill bill = billService.getBillById(billId);
        if (bill == null) {
            throw new IllegalArgumentException("Bill not found with ID: " + billId);
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than 0.");
        }

        Payment payment = new Payment();
        payment.setPaymentId(IdGenerator.nextPaymentId());
        payment.setBillId(billId);
        payment.setAmountPaid(amount);
        payment.setPaymentDate(LocalDate.now());
        payment.setMode(mode);

        store.getPayments().add(payment);
        store.getPaymentMap().put(payment.getPaymentId(), payment);

        bill.setPaidAmount(bill.getPaidAmount() + amount);
        if (bill.getBalance() <= 0) {
            bill.setStatus("PAID");
        } else if (bill.getPaidAmount() > 0) {
            bill.setStatus("PARTIAL");
        }
        persistPayment(payment);
        billService.persistBill(bill);
        return payment;
    }

    public void addPayment(Payment payment) {
        store.getPayments().add(payment);
        store.getPaymentMap().put(payment.getPaymentId(), payment);
        IdGenerator.syncPaymentCounter(payment.getPaymentId());
    }

    public List<Payment> getAllPayments() {
        return new ArrayList<>(store.getPayments());
    }

    private void persistPayment(Payment payment) throws Exception {
        if (ServiceRegistry.ensureDatabaseMode()) {
            paymentDAO.upsert(payment);
        } else {
            fileService.saveAll();
        }
    }
}
