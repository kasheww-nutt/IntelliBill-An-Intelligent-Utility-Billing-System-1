package com.intellibill.controller;

import com.intellibill.model.Bill;
import com.intellibill.service.ServiceRegistry;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class GenerateBillController {
    @FXML
    private TextField consumerIdField;

    @FXML
    private ComboBox<String> serviceCombo;

    @FXML
    private TextField previousReadingField;

    @FXML
    private TextField currentReadingField;

    @FXML
    private Label resultLabel;

    @FXML
    public void initialize() {
        serviceCombo.getItems().addAll("1 - Electricity", "2 - Water", "3 - Internet");
        serviceCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void onGenerateBill() {
        try {
            int consumerId = Integer.parseInt(consumerIdField.getText().trim());
            int serviceId = Integer.parseInt(serviceCombo.getValue().substring(0, 1));
            double previous = Double.parseDouble(previousReadingField.getText().trim());
            double current = Double.parseDouble(currentReadingField.getText().trim());

            Bill bill = ServiceRegistry.billService().generateBill(consumerId, serviceId, previous, current);
            resultLabel.setText("Bill ID " + bill.getBillId() + " | Amount: Rs " + bill.getAmount() + " | Due: " + bill.getDueDate());
        } catch (Exception ex) {
            resultLabel.setText("Error: " + ex.getMessage());
        }
    }
}
