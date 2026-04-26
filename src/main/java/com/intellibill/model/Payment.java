package com.intellibill.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Payment implements Serializable {
    private int paymentId;
    private int billId;
    private double amountPaid;
    private LocalDate paymentDate;
    private String mode;

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", billId=" + billId +
                ", amountPaid=" + amountPaid +
                ", paymentDate=" + paymentDate +
                ", mode='" + mode + '\'' +
                '}';
    }
}
