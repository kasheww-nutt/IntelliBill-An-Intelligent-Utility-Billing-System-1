package com.intellibill.controller;

import com.intellibill.model.Bill;
import com.intellibill.model.Consumer;
import com.intellibill.model.Payment;
import com.intellibill.service.ServiceRegistry;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReportsController {
    private static final DateTimeFormatter MONTH_LABEL = DateTimeFormatter.ofPattern("MMM yy");

    @FXML
    private javafx.scene.layout.VBox rootPane;
    @FXML
    private TextField updateConsumerIdField;
    @FXML
    private TextField updateNameField;
    @FXML
    private TextField updateAddressField;
    @FXML
    private TextField paymentBillIdField;
    @FXML
    private TextField paymentAmountField;
    @FXML
    private TextField paymentModeField;
    @FXML
    private TextField deleteConsumerIdField;
    @FXML
    private TextField deleteBillIdField;
    @FXML
    private Label actionStatusLabel;
    @FXML
    private Label totalConsumersLabel;
    @FXML
    private Label totalBillsLabel;
    @FXML
    private Label totalPaymentsLabel;
    @FXML
    private Label totalRevenueLabel;
    @FXML
    private TableView<Consumer> consumersTable;
    @FXML
    private TableColumn<Consumer, Integer> colConsumerId;
    @FXML
    private TableColumn<Consumer, String> colConsumerName;
    @FXML
    private TableColumn<Consumer, String> colConsumerAddress;
    @FXML
    private TableColumn<Consumer, String> colConsumerType;
    @FXML
    private TableView<Bill> billsTable;
    @FXML
    private TableColumn<Bill, Integer> colBillId;
    @FXML
    private TableColumn<Bill, Integer> colBillConsumerId;
    @FXML
    private TableColumn<Bill, Integer> colBillServiceId;
    @FXML
    private TableColumn<Bill, Number> colBillUnits;
    @FXML
    private TableColumn<Bill, String> colBillTotal;
    @FXML
    private TableColumn<Bill, String> colBillBalance;
    @FXML
    private TableColumn<Bill, String> colBillStatus;
    @FXML
    private TableView<Payment> paymentsTable;
    @FXML
    private TableColumn<Payment, Integer> colPaymentId;
    @FXML
    private TableColumn<Payment, Integer> colPaymentBillId;
    @FXML
    private TableColumn<Payment, Number> colPaymentAmount;
    @FXML
    private TableColumn<Payment, String> colPaymentDate;
    @FXML
    private TableColumn<Payment, String> colPaymentMode;
    @FXML
    private TextField searchConsumersField;
    @FXML
    private ComboBox<String> filterConsumerTypeCombo;
    @FXML
    private TextField searchBillsField;
    @FXML
    private ComboBox<String> filterBillStatusCombo;
    @FXML
    private TextField searchPaymentsField;
    @FXML
    private ComboBox<String> filterPaymentModeCombo;
    @FXML
    private TabPane dataTabPane;
    @FXML
    private BarChart<String, Number> summaryChart;
    @FXML
    private PieChart serviceRevenuePie;
    @FXML
    private LineChart<String, Number> monthlyConsumptionLine;
    @FXML
    private PieChart billStatusPie;
    @FXML
    private BarChart<String, Number> topUsageBar;
    @FXML
    private LineChart<String, Number> penaltyTrendLine;
    @FXML
    private ProgressBar collectionGauge;
    @FXML
    private Label collectionGaugeLabel;
    @FXML
    private GridPane paymentHeatmapGrid;
    @FXML
    private PieChart consumerTypePie;
    @FXML
    private BarChart<String, Number> billHistogramBar;
    @FXML
    private LineChart<String, Number> forecastLine;

    private final ObservableList<Consumer> consumerData = FXCollections.observableArrayList();
    private final ObservableList<Bill> billData = FXCollections.observableArrayList();
    private final ObservableList<Payment> paymentData = FXCollections.observableArrayList();

    private FilteredList<Consumer> consumerFiltered;
    private FilteredList<Bill> billFiltered;
    private FilteredList<Payment> paymentFiltered;

    @FXML
    public void initialize() {
        configureTables();
        configureFilters();
        configureEmptyStates();
        wireTableSelectionActions();
        wireTabAnimation();
        refreshReport();
    }

    @FXML
    private void onRefreshReport() {
        refreshReport();
        toast("Live data refreshed");
    }

    @FXML
    private void onUpdateConsumer() {
        try {
            int consumerId = Integer.parseInt(updateConsumerIdField.getText().trim());
            String name = updateNameField.getText().trim();
            String address = updateAddressField.getText().trim();
            ServiceRegistry.consumerService().updateConsumer(consumerId, name, address);
            setStatus("Consumer updated successfully.");
            clearUpdateFields();
            refreshReport();
        } catch (Exception ex) {
            setStatus("Update failed: " + ex.getMessage());
        }
    }

    @FXML
    private void onMakePayment() {
        try {
            int billId = Integer.parseInt(paymentBillIdField.getText().trim());
            double amount = Double.parseDouble(paymentAmountField.getText().trim());
            String mode = paymentModeField.getText().trim();
            ServiceRegistry.paymentService().recordPayment(billId, amount, mode);
            setStatus("Payment recorded.");
            clearPaymentFields();
            refreshReport();
        } catch (Exception ex) {
            setStatus("Payment failed: " + ex.getMessage());
        }
    }

    @FXML
    private void onDeleteConsumer() {
        try {
            int consumerId = Integer.parseInt(deleteConsumerIdField.getText().trim());
            if (!confirm("Delete Consumer", "Delete consumer " + consumerId + " and linked bills/payments?")) {
                return;
            }
            ServiceRegistry.consumerService().deleteConsumer(consumerId);
            setStatus("Consumer deleted.");
            deleteConsumerIdField.clear();
            refreshReport();
        } catch (Exception ex) {
            setStatus("Delete consumer failed: " + ex.getMessage());
        }
    }

    @FXML
    private void onDeleteBill() {
        try {
            int billId = Integer.parseInt(deleteBillIdField.getText().trim());
            if (!confirm("Delete Bill", "Delete bill " + billId + " and linked payments?")) {
                return;
            }
            ServiceRegistry.billService().deleteBill(billId);
            setStatus("Bill deleted.");
            deleteBillIdField.clear();
            refreshReport();
        } catch (Exception ex) {
            setStatus("Delete bill failed: " + ex.getMessage());
        }
    }

    @FXML
    private void onExportConsumers() {
        exportTable("consumers-export.csv", List.of("Consumer ID", "Name", "Address", "Type"),
                consumerFiltered.stream()
                        .map(c -> List.of(
                                String.valueOf(c.getConsumerId()),
                                c.getName(),
                                c.getAddress(),
                                c.getConsumerType().name()))
                        .collect(Collectors.toList()));
    }

    @FXML
    private void onExportBills() {
        exportTable("bills-export.csv", List.of("Bill ID", "Consumer ID", "Service ID", "Units", "Total", "Balance", "Status"),
                billFiltered.stream()
                        .map(b -> List.of(
                                String.valueOf(b.getBillId()),
                                String.valueOf(b.getConsumerId()),
                                String.valueOf(b.getServiceId()),
                                String.valueOf(b.getUnitsConsumed()),
                                formatCurrency(b.getTotalPayable()),
                                formatCurrency(b.getBalance()),
                                b.getStatus()))
                        .collect(Collectors.toList()));
    }

    @FXML
    private void onExportPayments() {
        exportTable("payments-export.csv", List.of("Payment ID", "Bill ID", "Amount", "Date", "Mode"),
                paymentFiltered.stream()
                        .map(p -> List.of(
                                String.valueOf(p.getPaymentId()),
                                String.valueOf(p.getBillId()),
                                String.valueOf(p.getAmountPaid()),
                                p.getPaymentDate() == null ? "" : p.getPaymentDate().toString(),
                                p.getMode()))
                        .collect(Collectors.toList()));
    }

    private void refreshReport() {
        List<Consumer> consumers = ServiceRegistry.consumerService().getAllConsumers();
        List<Bill> bills = ServiceRegistry.billService().getAllBills();
        List<Payment> payments = ServiceRegistry.paymentService().getAllPayments();

        consumerData.setAll(consumers);
        billData.setAll(bills);
        paymentData.setAll(payments);

        updateFilterChoices(consumers, bills, payments);
        runAllFilters();

        totalConsumersLabel.setText(String.valueOf(consumers.size()));
        totalBillsLabel.setText(String.valueOf(bills.size()));
        totalPaymentsLabel.setText(String.valueOf(payments.size()));
        double revenue = payments.stream().mapToDouble(Payment::getAmountPaid).sum();
        totalRevenueLabel.setText(formatCurrency(revenue));

        refreshMonthlyBillsVsPaymentsChart(bills, payments);
        refreshServiceRevenuePie(bills);
        refreshMonthlyConsumptionLine(bills);
        refreshBillStatusPie(bills);
        refreshTopUsageBar(bills);
        refreshPenaltyTrendLine(bills);
        refreshCollectionGauge(bills, payments);
        refreshPaymentHeatmap(payments);
        refreshConsumerTypePie(consumers);
        refreshBillHistogram(bills);
        refreshForecastLine(bills);
    }

    private void configureTables() {
        colConsumerId.setCellValueFactory(new PropertyValueFactory<>("consumerId"));
        colConsumerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colConsumerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colConsumerType.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getConsumerType().name()));

        colBillId.setCellValueFactory(new PropertyValueFactory<>("billId"));
        colBillConsumerId.setCellValueFactory(new PropertyValueFactory<>("consumerId"));
        colBillServiceId.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        colBillUnits.setCellValueFactory(new PropertyValueFactory<>("unitsConsumed"));
        colBillTotal.setCellValueFactory(cell -> new SimpleStringProperty(formatCurrency(cell.getValue().getTotalPayable())));
        colBillBalance.setCellValueFactory(cell -> new SimpleStringProperty(formatCurrency(cell.getValue().getBalance())));
        colBillStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colBillStatus.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                Label chip = new Label(item.toUpperCase(Locale.ROOT));
                chip.getStyleClass().add("status-chip");
                String normalized = item.toUpperCase(Locale.ROOT);
                if (normalized.contains("PAID")) {
                    chip.getStyleClass().add("status-paid");
                } else if (normalized.contains("OVERDUE")) {
                    chip.getStyleClass().add("status-overdue");
                } else {
                    chip.getStyleClass().add("status-partial");
                }
                setGraphic(chip);
                setText(null);
            }
        });

        colPaymentId.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        colPaymentBillId.setCellValueFactory(new PropertyValueFactory<>("billId"));
        colPaymentAmount.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        colPaymentDate.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getPaymentDate() == null ? "-" : cell.getValue().getPaymentDate().toString()));
        colPaymentMode.setCellValueFactory(new PropertyValueFactory<>("mode"));

        consumerFiltered = new FilteredList<>(consumerData, c -> true);
        billFiltered = new FilteredList<>(billData, b -> true);
        paymentFiltered = new FilteredList<>(paymentData, p -> true);

        SortedList<Consumer> consumerSorted = new SortedList<>(consumerFiltered);
        consumerSorted.comparatorProperty().bind(consumersTable.comparatorProperty());
        consumersTable.setItems(consumerSorted);

        SortedList<Bill> billSorted = new SortedList<>(billFiltered);
        billSorted.comparatorProperty().bind(billsTable.comparatorProperty());
        billsTable.setItems(billSorted);

        SortedList<Payment> paymentSorted = new SortedList<>(paymentFiltered);
        paymentSorted.comparatorProperty().bind(paymentsTable.comparatorProperty());
        paymentsTable.setItems(paymentSorted);
    }

    private void configureFilters() {
        filterConsumerTypeCombo.setItems(FXCollections.observableArrayList("All"));
        filterBillStatusCombo.setItems(FXCollections.observableArrayList("All"));
        filterPaymentModeCombo.setItems(FXCollections.observableArrayList("All"));
        filterConsumerTypeCombo.getSelectionModel().selectFirst();
        filterBillStatusCombo.getSelectionModel().selectFirst();
        filterPaymentModeCombo.getSelectionModel().selectFirst();

        searchConsumersField.textProperty().addListener((obs, oldVal, newVal) -> applyConsumerFilter());
        searchBillsField.textProperty().addListener((obs, oldVal, newVal) -> applyBillFilter());
        searchPaymentsField.textProperty().addListener((obs, oldVal, newVal) -> applyPaymentFilter());
        filterConsumerTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyConsumerFilter());
        filterBillStatusCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyBillFilter());
        filterPaymentModeCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyPaymentFilter());
    }

    private void configureEmptyStates() {
        consumersTable.setPlaceholder(new Label("No consumers found for current filter."));
        billsTable.setPlaceholder(new Label("No bills found for current filter."));
        paymentsTable.setPlaceholder(new Label("No payments found for current filter."));
    }

    private void wireTableSelectionActions() {
        consumersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                updateConsumerIdField.setText(String.valueOf(selected.getConsumerId()));
                updateNameField.setText(selected.getName());
                updateAddressField.setText(selected.getAddress());
                deleteConsumerIdField.setText(String.valueOf(selected.getConsumerId()));
            }
        });

        billsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                paymentBillIdField.setText(String.valueOf(selected.getBillId()));
                deleteBillIdField.setText(String.valueOf(selected.getBillId()));
            }
        });
    }

    private void wireTabAnimation() {
        dataTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && newTab.getContent() != null) {
                Node content = newTab.getContent();
                content.setOpacity(0);
                FadeTransition ft = new FadeTransition(Duration.millis(190), content);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();
            }
        });
    }

    private void updateFilterChoices(List<Consumer> consumers, List<Bill> bills, List<Payment> payments) {
        preserveComboChoices(filterConsumerTypeCombo, consumers.stream()
                .map(c -> c.getConsumerType().name())
                .collect(Collectors.toCollection(TreeSet::new)));
        preserveComboChoices(filterBillStatusCombo, bills.stream()
                .map(Bill::getStatus)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toCollection(TreeSet::new)));
        preserveComboChoices(filterPaymentModeCombo, payments.stream()
                .map(Payment::getMode)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toCollection(TreeSet::new)));
    }

    private void preserveComboChoices(ComboBox<String> comboBox, Set<String> values) {
        String selected = comboBox.getValue();
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("All");
        items.addAll(values);
        comboBox.setItems(items);
        if (selected != null && items.contains(selected)) {
            comboBox.setValue(selected);
        } else {
            comboBox.getSelectionModel().selectFirst();
        }
    }

    private void runAllFilters() {
        applyConsumerFilter();
        applyBillFilter();
        applyPaymentFilter();
    }

    private void applyConsumerFilter() {
        String q = safeLower(searchConsumersField.getText());
        String type = filterConsumerTypeCombo.getValue();
        consumerFiltered.setPredicate(c -> {
            boolean queryMatch = q.isBlank()
                    || String.valueOf(c.getConsumerId()).contains(q)
                    || safeLower(c.getName()).contains(q)
                    || safeLower(c.getAddress()).contains(q);
            boolean typeMatch = type == null || "All".equals(type) || c.getConsumerType().name().equals(type);
            return queryMatch && typeMatch;
        });
    }

    private void applyBillFilter() {
        String q = safeLower(searchBillsField.getText());
        String status = filterBillStatusCombo.getValue();
        billFiltered.setPredicate(b -> {
            boolean queryMatch = q.isBlank()
                    || String.valueOf(b.getBillId()).contains(q)
                    || String.valueOf(b.getConsumerId()).contains(q)
                    || String.valueOf(b.getServiceId()).contains(q);
            boolean statusMatch = status == null || "All".equals(status) || status.equalsIgnoreCase(b.getStatus());
            return queryMatch && statusMatch;
        });
    }

    private void applyPaymentFilter() {
        String q = safeLower(searchPaymentsField.getText());
        String mode = filterPaymentModeCombo.getValue();
        paymentFiltered.setPredicate(p -> {
            boolean queryMatch = q.isBlank()
                    || String.valueOf(p.getPaymentId()).contains(q)
                    || String.valueOf(p.getBillId()).contains(q)
                    || safeLower(p.getMode()).contains(q);
            boolean modeMatch = mode == null || "All".equals(mode) || mode.equalsIgnoreCase(p.getMode());
            return queryMatch && modeMatch;
        });
    }

    private void refreshMonthlyBillsVsPaymentsChart(List<Bill> bills, List<Payment> payments) {
        List<YearMonth> months = lastSixMonths();
        XYChart.Series<String, Number> billSeries = new XYChart.Series<>();
        billSeries.setName("Bills");
        XYChart.Series<String, Number> paymentSeries = new XYChart.Series<>();
        paymentSeries.setName("Payments");

        for (YearMonth month : months) {
            double billAmount = bills.stream()
                    .filter(b -> b.getBillDate() != null && YearMonth.from(b.getBillDate()).equals(month))
                    .mapToDouble(Bill::getTotalPayable)
                    .sum();
            double paymentAmount = payments.stream()
                    .filter(p -> p.getPaymentDate() != null && YearMonth.from(p.getPaymentDate()).equals(month))
                    .mapToDouble(Payment::getAmountPaid)
                    .sum();
            String label = month.format(MONTH_LABEL);
            billSeries.getData().add(new XYChart.Data<>(label, billAmount));
            paymentSeries.getData().add(new XYChart.Data<>(label, paymentAmount));
        }
        summaryChart.getData().setAll(billSeries, paymentSeries);
    }

    private void refreshServiceRevenuePie(List<Bill> bills) {
        Map<Integer, Double> revenue = bills.stream().collect(Collectors.groupingBy(
                Bill::getServiceId, Collectors.summingDouble(Bill::getTotalPayable)));
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
                new PieChart.Data("Electricity", revenue.getOrDefault(1, 0.0)),
                new PieChart.Data("Water", revenue.getOrDefault(2, 0.0)),
                new PieChart.Data("Internet", revenue.getOrDefault(3, 0.0))
        );
        serviceRevenuePie.setData(data);
    }

    private void refreshMonthlyConsumptionLine(List<Bill> bills) {
        List<YearMonth> months = lastSixMonths();
        monthlyConsumptionLine.getData().clear();
        monthlyConsumptionLine.getData().add(buildServiceSeries("Electricity", 1, months, bills, Bill::getUnitsConsumed));
        monthlyConsumptionLine.getData().add(buildServiceSeries("Water", 2, months, bills, Bill::getUnitsConsumed));
        monthlyConsumptionLine.getData().add(buildServiceSeries("Internet", 3, months, bills, Bill::getUnitsConsumed));
    }

    private XYChart.Series<String, Number> buildServiceSeries(String name, int serviceId, List<YearMonth> months,
                                                               List<Bill> bills, Function<Bill, Double> mapper) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (YearMonth month : months) {
            double value = bills.stream()
                    .filter(b -> b.getServiceId() == serviceId)
                    .filter(b -> b.getBillDate() != null && YearMonth.from(b.getBillDate()).equals(month))
                    .mapToDouble(mapper::apply)
                    .sum();
            series.getData().add(new XYChart.Data<>(month.format(MONTH_LABEL), value));
        }
        return series;
    }

    private void refreshBillStatusPie(List<Bill> bills) {
        Map<String, Long> byStatus = bills.stream()
                .collect(Collectors.groupingBy(b -> normalizeStatus(b.getStatus()), Collectors.counting()));
        billStatusPie.setData(FXCollections.observableArrayList(
                new PieChart.Data("PAID", byStatus.getOrDefault("PAID", 0L)),
                new PieChart.Data("PARTIAL", byStatus.getOrDefault("PARTIAL", 0L)),
                new PieChart.Data("OVERDUE", byStatus.getOrDefault("OVERDUE", 0L)),
                new PieChart.Data("UNPAID", byStatus.getOrDefault("UNPAID", 0L))
        ));
    }

    private void refreshTopUsageBar(List<Bill> bills) {
        Map<Integer, Double> usageByConsumer = bills.stream().collect(Collectors.groupingBy(
                Bill::getConsumerId, Collectors.summingDouble(Bill::getUnitsConsumed)));
        List<Map.Entry<Integer, Double>> top = usageByConsumer.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .toList();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Units");
        for (Map.Entry<Integer, Double> entry : top) {
            series.getData().add(new XYChart.Data<>("C-" + entry.getKey(), entry.getValue()));
        }
        topUsageBar.getData().setAll(series);
    }

    private void refreshPenaltyTrendLine(List<Bill> bills) {
        List<YearMonth> months = lastSixMonths();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Penalty");
        for (YearMonth month : months) {
            double penalty = bills.stream()
                    .filter(b -> b.getBillDate() != null && YearMonth.from(b.getBillDate()).equals(month))
                    .mapToDouble(Bill::getPenalty)
                    .sum();
            series.getData().add(new XYChart.Data<>(month.format(MONTH_LABEL), penalty));
        }
        penaltyTrendLine.getData().setAll(series);
    }

    private void refreshCollectionGauge(List<Bill> bills, List<Payment> payments) {
        double totalBilled = bills.stream().mapToDouble(Bill::getTotalPayable).sum();
        double totalPaid = payments.stream().mapToDouble(Payment::getAmountPaid).sum();
        double ratio = totalBilled <= 0 ? 0 : Math.min(1.0, totalPaid / totalBilled);
        collectionGauge.setProgress(ratio);
        collectionGaugeLabel.setText(String.format(Locale.US, "Collection Efficiency: %.1f%%", ratio * 100));
    }

    private void refreshPaymentHeatmap(List<Payment> payments) {
        paymentHeatmapGrid.getChildren().clear();
        paymentHeatmapGrid.setHgap(4);
        paymentHeatmapGrid.setVgap(4);

        Map<LocalDate, Double> amountByDate = payments.stream()
                .filter(p -> p.getPaymentDate() != null)
                .collect(Collectors.groupingBy(Payment::getPaymentDate, Collectors.summingDouble(Payment::getAmountPaid)));

        LocalDate start = LocalDate.now().minusDays(27);
        double max = amountByDate.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);

        for (int i = 0; i < 28; i++) {
            LocalDate day = start.plusDays(i);
            double value = amountByDate.getOrDefault(day, 0.0);
            double intensity = max <= 0 ? 0 : value / max;
            Rectangle cell = new Rectangle(20, 20);
            cell.setArcHeight(4);
            cell.setArcWidth(4);
            cell.setFill(Color.rgb(58, 128, 246, 0.15 + 0.75 * intensity));
            cell.setStroke(Color.rgb(180, 198, 220, 0.4));
            Label tip = new Label(day + " : Rs " + String.format(Locale.US, "%.2f", value));
            tip.getStyleClass().add("heatmap-tip");
            TooltipUtil.install(cell, tip.getText());
            paymentHeatmapGrid.add(cell, i % 7, i / 7);
            GridPane.setMargin(cell, new Insets(1));
        }
    }

    private void refreshConsumerTypePie(List<Consumer> consumers) {
        Map<String, Long> byType = consumers.stream()
                .collect(Collectors.groupingBy(c -> c.getConsumerType().name(), Collectors.counting()));
        consumerTypePie.setData(FXCollections.observableArrayList(
                new PieChart.Data("Residential", byType.getOrDefault("RESIDENTIAL", 0L)),
                new PieChart.Data("Commercial", byType.getOrDefault("COMMERCIAL", 0L)),
                new PieChart.Data("Industrial", byType.getOrDefault("INDUSTRIAL", 0L))
        ));
    }

    private void refreshBillHistogram(List<Bill> bills) {
        int[] bins = new int[5];
        for (Bill bill : bills) {
            double value = bill.getTotalPayable();
            if (value < 500) bins[0]++;
            else if (value < 1000) bins[1]++;
            else if (value < 2000) bins[2]++;
            else if (value < 5000) bins[3]++;
            else bins[4]++;
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Bill Count");
        series.getData().add(new XYChart.Data<>("0-499", bins[0]));
        series.getData().add(new XYChart.Data<>("500-999", bins[1]));
        series.getData().add(new XYChart.Data<>("1000-1999", bins[2]));
        series.getData().add(new XYChart.Data<>("2000-4999", bins[3]));
        series.getData().add(new XYChart.Data<>("5000+", bins[4]));
        billHistogramBar.getData().setAll(series);
    }

    private void refreshForecastLine(List<Bill> bills) {
        List<YearMonth> months = lastSixMonths();
        XYChart.Series<String, Number> actual = new XYChart.Series<>();
        actual.setName("Actual Units");
        List<Double> values = new ArrayList<>();
        for (YearMonth month : months) {
            double units = bills.stream()
                    .filter(b -> b.getBillDate() != null && YearMonth.from(b.getBillDate()).equals(month))
                    .mapToDouble(Bill::getUnitsConsumed)
                    .sum();
            values.add(units);
            actual.getData().add(new XYChart.Data<>(month.format(MONTH_LABEL), units));
        }

        double avg = values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        XYChart.Series<String, Number> forecast = new XYChart.Series<>();
        forecast.setName("Forecast Units");
        forecast.getData().add(new XYChart.Data<>(months.get(months.size() - 1).format(MONTH_LABEL), values.get(values.size() - 1)));
        YearMonth nextMonth = months.get(months.size() - 1).plusMonths(1);
        forecast.getData().add(new XYChart.Data<>(nextMonth.format(MONTH_LABEL), avg));
        forecastLine.getData().setAll(actual, forecast);
    }

    private List<YearMonth> lastSixMonths() {
        YearMonth current = YearMonth.from(LocalDate.now());
        List<YearMonth> months = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            months.add(current.minusMonths(i));
        }
        return months;
    }

    private void exportTable(String fileName, List<String> headers, List<List<String>> rows) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export CSV");
        chooser.setInitialFileName(fileName);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        File target = chooser.showSaveDialog(rootPane.getScene().getWindow());
        if (target == null) {
            return;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(target.toPath())) {
            writer.write(toCsvRow(headers));
            writer.newLine();
            for (List<String> row : rows) {
                writer.write(toCsvRow(row));
                writer.newLine();
            }
            setStatus("Exported: " + target.getName());
        } catch (IOException ex) {
            setStatus("Export failed: " + ex.getMessage());
        }
    }

    private String toCsvRow(List<String> values) {
        return values.stream().map(this::csvEscape).collect(Collectors.joining(","));
    }

    private String csvEscape(String input) {
        String safe = input == null ? "" : input;
        if (safe.contains(",") || safe.contains("\"") || safe.contains("\n")) {
            return "\"" + safe.replace("\"", "\"\"") + "\"";
        }
        return safe;
    }

    private boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void setStatus(String message) {
        actionStatusLabel.setText(message);
        toast(message);
    }

    private void toast(String message) {
        if (rootPane.getScene() == null || rootPane.getScene().getWindow() == null) {
            return;
        }
        Label label = new Label(message);
        label.getStyleClass().add("toast-label");
        StackPane container = new StackPane(label);
        container.getStyleClass().add("toast-pane");
        Popup popup = new Popup();
        popup.getContent().add(container);

        Stage stage = (Stage) rootPane.getScene().getWindow();
        popup.show(stage);
        popup.setX(stage.getX() + stage.getWidth() - container.prefWidth(-1) - 40);
        popup.setY(stage.getY() + stage.getHeight() - 100);

        PauseTransition delay = new PauseTransition(Duration.seconds(1.6));
        delay.setOnFinished(e -> {
            FadeTransition fade = new FadeTransition(Duration.millis(260), container);
            fade.setFromValue(1);
            fade.setToValue(0);
            fade.setOnFinished(done -> popup.hide());
            fade.play();
        });
        delay.play();
    }

    private void clearUpdateFields() {
        updateConsumerIdField.clear();
        updateNameField.clear();
        updateAddressField.clear();
    }

    private void clearPaymentFields() {
        paymentBillIdField.clear();
        paymentAmountField.clear();
        paymentModeField.clear();
    }

    private String formatCurrency(double value) {
        return "Rs " + String.format(Locale.US, "%.2f", value);
    }

    private String safeLower(String input) {
        return input == null ? "" : input.toLowerCase(Locale.ROOT).trim();
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "UNPAID";
        }
        return status.toUpperCase(Locale.ROOT);
    }

    private static final class TooltipUtil {
        private TooltipUtil() {}

        static void install(Node node, String text) {
            javafx.scene.control.Tooltip.install(node, new javafx.scene.control.Tooltip(text));
        }
    }
}
