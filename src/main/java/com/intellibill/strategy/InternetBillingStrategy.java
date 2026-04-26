package com.intellibill.strategy;

public class InternetBillingStrategy implements BillingStrategy {
    @Override
    public double calculateBill(double unitsConsumed) {
        if (unitsConsumed <= 100) {
            return 499;
        }
        if (unitsConsumed <= 300) {
            return 799;
        }
        return 799 + ((unitsConsumed - 300) * 2.0);
    }

    @Override
    public String getSlabDescription() {
        return "<=100GB: Rs 499, 101-300GB: Rs 799, >300GB: Rs 799 + Rs 2/GB";
    }
}
