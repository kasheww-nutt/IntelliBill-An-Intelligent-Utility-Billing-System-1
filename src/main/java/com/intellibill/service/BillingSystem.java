package com.intellibill.service;

import com.intellibill.exception.ConsumerNotFoundException;
import com.intellibill.exception.InvalidReadingException;
import com.intellibill.model.Bill;
import com.intellibill.model.Consumer;
import com.intellibill.model.ConsumerType;
import com.intellibill.model.Payment;
import com.intellibill.model.UtilityService;
import com.intellibill.util.InputUtil;

public class BillingSystem {
    private final ConsumerService consumerService = ServiceRegistry.consumerService();
    private final BillService billService = ServiceRegistry.billService();
    private final PaymentService paymentService = ServiceRegistry.paymentService();
    private final FileService fileService = ServiceRegistry.fileService();

    public void startConsole() {
        ServiceRegistry.initialize();
        boolean running = true;
        while (running) {
            printMenu();
            int choice = InputUtil.readInt("Choose option: ");
            try {
                switch (choice) {
                    case 1 -> addConsumer();
                    case 2 -> updateConsumer();
                    case 3 -> listConsumers();
                    case 4 -> generateBill();
                    case 5 -> listBills();
                    case 6 -> makePayment();
                    case 7 -> compareConsumption();
                    case 8 -> deleteConsumer();
                    case 9 -> deleteBill();
                    case 10 -> saveToFiles();
                    case 11 -> loadFromFiles();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid menu option.");
                }
            } catch (ConsumerNotFoundException | InvalidReadingException ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("Unexpected error: " + ex.getMessage());
            }
        }
        System.out.println("Exiting IntelliBill Console.");
    }

    private void printMenu() {
        System.out.println("\n=== IntelliBill Console ===");
        System.out.println("1. Add Consumer");
        System.out.println("2. Update Consumer");
        System.out.println("3. View All Consumers");
        System.out.println("4. Generate Bill");
        System.out.println("5. View All Bills");
        System.out.println("6. Record Payment");
        System.out.println("7. Compare Consumption");
        System.out.println("8. Delete Consumer");
        System.out.println("9. Delete Bill");
        System.out.println("10. Save Data to Files (Fallback)");
        System.out.println("11. Load Data from Files (Fallback)");
        System.out.println("0. Exit");
    }

    private void addConsumer() throws Exception {
        String name = InputUtil.readNonEmptyString("Name: ");
        String address = InputUtil.readNonEmptyString("Address: ");
        int typeChoice = InputUtil.readInt("Type (1=Residential, 2=Commercial, 3=Industrial): ");

        ConsumerType type = switch (typeChoice) {
            case 1 -> ConsumerType.RESIDENTIAL;
            case 2 -> ConsumerType.COMMERCIAL;
            case 3 -> ConsumerType.INDUSTRIAL;
            default -> throw new IllegalArgumentException("Invalid consumer type.");
        };

        Consumer consumer = consumerService.addConsumer(name, address, type);
        System.out.println("Consumer added with ID: " + consumer.getConsumerId());
    }

    private void updateConsumer() throws Exception {
        int consumerId = InputUtil.readInt("Consumer ID: ");
        String name = InputUtil.readNonEmptyString("New Name: ");
        String address = InputUtil.readNonEmptyString("New Address: ");

        consumerService.updateConsumer(consumerId, name, address);
        System.out.println("Consumer updated successfully.");
    }

    private void listConsumers() {
        System.out.println("\n--- Consumers ---");
        for (Consumer consumer : consumerService.getAllConsumers()) {
            System.out.println(consumer);
        }
    }

    private void generateBill() throws Exception {
        int consumerId = InputUtil.readInt("Consumer ID: ");
        int serviceId = InputUtil.readInt("Service (1=Electricity,2=Water,3=Internet): ");
        double previousReading = InputUtil.readDouble("Previous Reading: ");
        double currentReading = InputUtil.readDouble("Current Reading: ");

        Bill bill = billService.generateBill(consumerId, serviceId, previousReading, currentReading);
        UtilityService service = billService.getServiceById(serviceId);

        System.out.println("Bill generated: ID=" + bill.getBillId());
        System.out.println("Service: " + service.getServiceName());
        System.out.println("Units: " + bill.getUnitsConsumed());
        System.out.println("Amount: Rs " + bill.getAmount());
        System.out.println("Due Date: " + bill.getDueDate());
    }

    private void listBills() {
        System.out.println("\n--- Bills ---");
        for (Bill bill : billService.getAllBills()) {
            System.out.println(bill);
        }
    }

    private void makePayment() throws Exception {
        int billId = InputUtil.readInt("Bill ID: ");
        double amount = InputUtil.readDouble("Amount Paid: ");
        String mode = InputUtil.readNonEmptyString("Mode (UPI/Card/Cash/etc): ");

        Payment payment = paymentService.recordPayment(billId, amount, mode);
        System.out.println("Payment recorded with ID: " + payment.getPaymentId());
    }

    private void compareConsumption() {
        int consumerId = InputUtil.readInt("Consumer ID: ");
        int serviceId = InputUtil.readInt("Service ID (1/2/3): ");
        String message = billService.compareConsumption(consumerId, serviceId);
        System.out.println(message);
    }

    private void deleteConsumer() throws Exception {
        int consumerId = InputUtil.readInt("Consumer ID to delete: ");
        consumerService.deleteConsumer(consumerId);
        System.out.println("Consumer deleted.");
    }

    private void deleteBill() throws Exception {
        int billId = InputUtil.readInt("Bill ID to delete: ");
        billService.deleteBill(billId);
        System.out.println("Bill deleted.");
    }

    private void saveToFiles() throws Exception {
        fileService.saveAll();
        System.out.println("Data saved to files successfully.");
    }

    private void loadFromFiles() throws Exception {
        fileService.loadAll();
        System.out.println("Data loaded from files successfully.");
    }
}
