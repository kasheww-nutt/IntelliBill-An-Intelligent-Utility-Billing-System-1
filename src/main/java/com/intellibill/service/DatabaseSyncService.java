package com.intellibill.service;

import com.intellibill.database.BillDAO;
import com.intellibill.database.ConsumerDAO;
import com.intellibill.database.PaymentDAO;
import com.intellibill.database.ServiceDAO;
import com.intellibill.model.Bill;
import com.intellibill.model.Consumer;
import com.intellibill.model.Payment;
import com.intellibill.util.IdGenerator;

import java.util.List;

public class DatabaseSyncService {
    private final InMemoryStore store = InMemoryStore.getInstance();

    private final ConsumerDAO consumerDAO = new ConsumerDAO();
    private final BillDAO billDAO = new BillDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();

    public void seedServices() throws Exception {
        serviceDAO.seedDefaultServices(ServiceRegistry.billService().getServiceCatalog());
    }

    public void exportAllToDatabase() throws Exception {
        seedServices();

        for (Consumer consumer : store.getConsumers()) {
            consumerDAO.upsert(consumer);
        }
        for (Bill bill : store.getBills()) {
            billDAO.upsert(bill);
        }
        for (Payment payment : store.getPayments()) {
            paymentDAO.upsert(payment);
        }
    }

    public void importAllFromDatabase() throws Exception {
        List<Consumer> consumers = consumerDAO.findAll();
        List<Bill> bills = billDAO.findAll();
        List<Payment> payments = paymentDAO.findAll();

        store.replaceConsumers(consumers);
        store.replaceBills(bills);
        store.replacePayments(payments);

        IdGenerator.syncConsumerCounter(consumers.stream().mapToInt(Consumer::getConsumerId).max().orElse(1000));
        IdGenerator.syncBillCounter(bills.stream().mapToInt(Bill::getBillId).max().orElse(5000));
        IdGenerator.syncPaymentCounter(payments.stream().mapToInt(Payment::getPaymentId).max().orElse(9000));
    }
}
