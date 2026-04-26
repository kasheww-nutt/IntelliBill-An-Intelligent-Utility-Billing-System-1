package com.intellibill.util;

import java.util.concurrent.atomic.AtomicInteger;

public final class IdGenerator {
    private static final AtomicInteger CONSUMER_COUNTER = new AtomicInteger(1000);
    private static final AtomicInteger BILL_COUNTER = new AtomicInteger(5000);
    private static final AtomicInteger PAYMENT_COUNTER = new AtomicInteger(9000);

    private IdGenerator() {
    }

    public static int nextConsumerId() {
        return CONSUMER_COUNTER.incrementAndGet();
    }

    public static int nextBillId() {
        return BILL_COUNTER.incrementAndGet();
    }

    public static int nextPaymentId() {
        return PAYMENT_COUNTER.incrementAndGet();
    }

    public static void syncConsumerCounter(int maxId) {
        CONSUMER_COUNTER.set(Math.max(CONSUMER_COUNTER.get(), maxId));
    }

    public static void syncBillCounter(int maxId) {
        BILL_COUNTER.set(Math.max(BILL_COUNTER.get(), maxId));
    }

    public static void syncPaymentCounter(int maxId) {
        PAYMENT_COUNTER.set(Math.max(PAYMENT_COUNTER.get(), maxId));
    }
}
