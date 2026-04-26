package com.intellibill.strategy;

public class WaterBillingStrategy implements BillingStrategy {
    @Override
    public double calculateBill(double unitsConsumed) {
        double amount = 20; // maintenance charge
        double remaining = unitsConsumed;

        if (remaining > 0) {
            double slab = Math.min(remaining, 50);
            amount += slab * 2.0;
            remaining -= slab;
        }
        if (remaining > 0) {
            double slab = Math.min(remaining, 100);
            amount += slab * 3.5;
            remaining -= slab;
        }
        if (remaining > 0) {
            amount += remaining * 5.0;
        }
        return amount;
    }

    @Override
    public String getSlabDescription() {
        return "0-50: Rs 2/unit, 51-150: Rs 3.5/unit, 150+: Rs 5/unit, fixed Rs 20";
    }
}
