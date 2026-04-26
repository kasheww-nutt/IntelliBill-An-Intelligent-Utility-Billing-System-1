package com.intellibill.strategy;

public interface BillingStrategy {
    double calculateBill(double unitsConsumed);
    String getSlabDescription();
}
