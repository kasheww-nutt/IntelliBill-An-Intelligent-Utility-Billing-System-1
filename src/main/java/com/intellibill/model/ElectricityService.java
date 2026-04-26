package com.intellibill.model;

import com.intellibill.strategy.BillingStrategy;

public class ElectricityService extends UtilityService {
    public ElectricityService(int serviceId, BillingStrategy billingStrategy) {
        super(serviceId, ServiceType.ELECTRICITY, "Electricity", billingStrategy);
    }
}
