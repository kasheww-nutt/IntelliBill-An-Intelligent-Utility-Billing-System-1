package com.intellibill.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Bill implements Serializable {
    private int billId;
    private int consumerId;
    private int serviceId;
    private double previousReading;
    private double currentReading;
    private double unitsConsumed;
    private double amount;
    private double penalty;
    private double paidAmount;
    private LocalDate billDate;
    private LocalDate dueDate;
    private String status;

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(int consumerId) {
        this.consumerId = consumerId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public double getPreviousReading() {
        return previousReading;
    }

    public void setPreviousReading(double previousReading) {
        this.previousReading = previousReading;
    }

    public double getCurrentReading() {
        return currentReading;
    }

    public void setCurrentReading(double currentReading) {
        this.currentReading = currentReading;
    }

    public double getUnitsConsumed() {
        return unitsConsumed;
    }

    public void setUnitsConsumed(double unitsConsumed) {
        this.unitsConsumed = unitsConsumed;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getPenalty() {
        return penalty;
    }

    public void setPenalty(double penalty) {
        this.penalty = penalty;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalPayable() {
        return amount + penalty;
    }

    public double getBalance() {
        return getTotalPayable() - paidAmount;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId=" + billId +
                ", consumerId=" + consumerId +
                ", serviceId=" + serviceId +
                ", unitsConsumed=" + unitsConsumed +
                ", amount=" + amount +
                ", penalty=" + penalty +
                ", paidAmount=" + paidAmount +
                ", dueDate=" + dueDate +
                ", status='" + status + '\'' +
                '}';
    }
}
