package com.intellibill.controller;

import com.intellibill.model.Consumer;
import com.intellibill.model.ConsumerType;
import com.intellibill.service.ServiceRegistry;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddConsumerController {
    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private ComboBox<ConsumerType> typeCombo;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList(ConsumerType.values()));
        typeCombo.getSelectionModel().select(ConsumerType.RESIDENTIAL);
    }

    @FXML
    private void onSaveConsumer() {
        try {
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            String address = addressField.getText() == null ? "" : addressField.getText().trim();
            ConsumerType type = typeCombo.getValue();

            if (name.isEmpty() || address.isEmpty() || type == null) {
                statusLabel.setText("Please fill all fields.");
                return;
            }

            Consumer consumer = ServiceRegistry.consumerService().addConsumer(name, address, type);
            statusLabel.setText("Consumer added with ID: " + consumer.getConsumerId());
            nameField.clear();
            addressField.clear();
            typeCombo.getSelectionModel().select(ConsumerType.RESIDENTIAL);
        } catch (Exception ex) {
            statusLabel.setText("Save failed: " + ex.getMessage());
        }
    }
}
