package com.intellibill.model;

import com.intellibill.strategy.BillingStrategy;

public class InternetService extends UtilityService {
    public InternetService(int serviceId, BillingStrategy billingStrategy) {
        super(serviceId, ServiceType.INTERNET, "Internet", billingStrategy);
    }
}
