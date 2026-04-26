package com.intellibill.service;

import com.intellibill.model.Bill;
import com.intellibill.model.Consumer;
import com.intellibill.model.Payment;
import com.intellibill.util.IdGenerator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileService {
    private static final Path DATA_DIR = Path.of("data");
    private static final Path CONSUMER_FILE = DATA_DIR.resolve("consumers.dat");
    private static final Path BILL_FILE = DATA_DIR.resolve("bills.dat");
    private static final Path PAYMENT_FILE = DATA_DIR.resolve("payments.dat");

    private final InMemoryStore store = InMemoryStore.getInstance();

    public void saveAll() throws IOException {
        Files.createDirectories(DATA_DIR);
        saveList(CONSUMER_FILE, store.getConsumers());
        saveList(BILL_FILE, store.getBills());
        saveList(PAYMENT_FILE, store.getPayments());
    }

    @SuppressWarnings("unchecked")
    public void loadAll() throws IOException, ClassNotFoundException {
        List<Consumer> consumers = Files.exists(CONSUMER_FILE)
                ? (List<Consumer>) readList(CONSUMER_FILE)
                : new ArrayList<>();
        List<Bill> bills = Files.exists(BILL_FILE)
                ? (List<Bill>) readList(BILL_FILE)
                : new ArrayList<>();
        List<Payment> payments = Files.exists(PAYMENT_FILE)
                ? (List<Payment>) readList(PAYMENT_FILE)
                : new ArrayList<>();

        store.replaceConsumers(consumers);
        store.replaceBills(bills);
        store.replacePayments(payments);

        int maxConsumer = consumers.stream().mapToInt(Consumer::getConsumerId).max().orElse(1000);
        int maxBill = bills.stream().mapToInt(Bill::getBillId).max().orElse(5000);
        int maxPayment = payments.stream().mapToInt(Payment::getPaymentId).max().orElse(9000);

        IdGenerator.syncConsumerCounter(maxConsumer);
        IdGenerator.syncBillCounter(maxBill);
        IdGenerator.syncPaymentCounter(maxPayment);
    }

    private void saveList(Path filePath, List<?> data) throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            outputStream.writeObject(new ArrayList<>(data));
        }
    }

    private Object readList(Path filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return inputStream.readObject();
        }
    }
}
