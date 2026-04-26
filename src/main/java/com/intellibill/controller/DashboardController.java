package com.intellibill.controller;

import com.intellibill.service.ServiceRegistry;
import com.intellibill.ui.AppNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {
    private static final DateTimeFormatter SYNC_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label consumerCountLabel;

    @FXML
    private Label billCountLabel;

    @FXML
    private Label paymentCountLabel;
    @FXML
    private Label persistenceModeLabel;
    @FXML
    private Label lastSyncLabel;
    @FXML
    private Label dbStatusBadge;
    @FXML
    private Label currentUserBadge;
    @FXML
    private ToggleButton themeToggle;

    @FXML
    public void initialize() {
        ServiceRegistry.initialize();
        AppNavigator.setDashboardRoot(rootPane);
        currentUserBadge.setText("User: Admin Operator");
        refreshStats();
        AppNavigator.showInCenter("/com/intellibill/ui/reports.fxml");
    }

    @FXML
    private void onAddConsumer() {
        AppNavigator.showInCenter("/com/intellibill/ui/add-consumer.fxml");
    }

    @FXML
    private void onGenerateBill() {
        AppNavigator.showInCenter("/com/intellibill/ui/generate-bill.fxml");
    }

    @FXML
    private void onViewReports() {
        AppNavigator.showInCenter("/com/intellibill/ui/reports.fxml");
    }

    @FXML
    private void onRefreshStats() {
        refreshStats();
    }

    @FXML
    private void onToggleTheme() {
        applyTheme();
    }

    private void refreshStats() {
        consumerCountLabel.setText("Consumers: " + ServiceRegistry.consumerService().getAllConsumers().size());
        billCountLabel.setText("Bills: " + ServiceRegistry.billService().getAllBills().size());
        paymentCountLabel.setText("Payments: " + ServiceRegistry.paymentService().getAllPayments().size());
        boolean mysqlLive = ServiceRegistry.ensureDatabaseMode();
        persistenceModeLabel.setText(mysqlLive ? "Persistence: MySQL Live" : "Persistence: File Fallback");
        dbStatusBadge.setText(mysqlLive ? "DB: Connected" : "DB: Fallback");
        dbStatusBadge.getStyleClass().removeAll("badge-ok", "badge-warn");
        dbStatusBadge.getStyleClass().add(mysqlLive ? "badge-ok" : "badge-warn");
        lastSyncLabel.setText("Last Sync: " + LocalDateTime.now().format(SYNC_FORMAT));
        applyTheme();
    }

    private void applyTheme() {
        if (rootPane.getScene() == null) {
            return;
        }
        rootPane.getStyleClass().remove("dark-theme");
        if (themeToggle.isSelected()) {
            rootPane.getStyleClass().add("dark-theme");
            themeToggle.setText("Dark");
        } else {
            themeToggle.setText("Light");
        }
    }
}
