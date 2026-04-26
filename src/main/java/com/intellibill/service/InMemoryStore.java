package com.intellibill.service;

import com.intellibill.model.Bill;
import com.intellibill.model.Consumer;
import com.intellibill.model.Payment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryStore {
    private static final InMemoryStore INSTANCE = new InMemoryStore();

    private final List<Consumer> consumers = new ArrayList<>();
    private final Map<Integer, Consumer> consumerMap = new HashMap<>();

    private final List<Bill> bills = new ArrayList<>();
    private final Map<Integer, Bill> billMap = new HashMap<>();

    private final List<Payment> payments = new ArrayList<>();
    private final Map<Integer, Payment> paymentMap = new HashMap<>();

    private InMemoryStore() {
    }

    public static InMemoryStore getInstance() {
        return INSTANCE;
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }

    public Map<Integer, Consumer> getConsumerMap() {
        return consumerMap;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public Map<Integer, Bill> getBillMap() {
        return billMap;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public Map<Integer, Payment> getPaymentMap() {
        return paymentMap;
    }

    public void replaceConsumers(List<Consumer> newConsumers) {
        consumers.clear();
        consumerMap.clear();
        for (Consumer consumer : newConsumers) {
            consumers.add(consumer);
            consumerMap.put(consumer.getConsumerId(), consumer);
        }
    }

    public void replaceBills(List<Bill> newBills) {
        bills.clear();
        billMap.clear();
        for (Bill bill : newBills) {
            bills.add(bill);
            billMap.put(bill.getBillId(), bill);
        }
    }

    public void replacePayments(List<Payment> newPayments) {
        payments.clear();
        paymentMap.clear();
        for (Payment payment : newPayments) {
            payments.add(payment);
            paymentMap.put(payment.getPaymentId(), payment);
        }
    }
}
