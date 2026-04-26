package com.intellibill.strategy;

public class ElectricityBillingStrategy implements BillingStrategy {
    @Override
    public double calculateBill(double unitsConsumed) {
        double amount = 50; // fixed meter charge
        double remaining = unitsConsumed;

        if (remaining > 0) {
            double slab = Math.min(remaining, 100);
            amount += slab * 5.0;
            remaining -= slab;
        }
        if (remaining > 0) {
            double slab = Math.min(remaining, 200);
            amount += slab * 7.0;
            remaining -= slab;
        }
        if (remaining > 0) {
            amount += remaining * 10.0;
        }
        return amount;
    }

    @Override
    public String getSlabDescription() {
        return "0-100: Rs 5/unit, 101-300: Rs 7/unit, 300+: Rs 10/unit, fixed Rs 50";
    }
}
