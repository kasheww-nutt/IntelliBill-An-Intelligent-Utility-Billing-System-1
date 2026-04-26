package com.intellibill.model;

import com.intellibill.strategy.BillingStrategy;

public class WaterService extends UtilityService {
    public WaterService(int serviceId, BillingStrategy billingStrategy) {
        super(serviceId, ServiceType.WATER, "Water", billingStrategy);
    }
}
