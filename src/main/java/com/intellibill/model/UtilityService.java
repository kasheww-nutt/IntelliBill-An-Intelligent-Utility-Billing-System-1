package com.intellibill.model;

import com.intellibill.strategy.BillingStrategy;

public abstract class UtilityService {
    protected int serviceId;
    protected ServiceType serviceType;
    protected String serviceName;
    protected BillingStrategy billingStrategy;

    protected UtilityService(int serviceId, ServiceType serviceType, String serviceName, BillingStrategy billingStrategy) {
        this.serviceId = serviceId;
        this.serviceType = serviceType;
        this.serviceName = serviceName;
        this.billingStrategy = billingStrategy;
    }

    public int getServiceId() { return serviceId; }
    public ServiceType getServiceType() { return serviceType; }
    public String getServiceName() { return serviceName; }
    public BillingStrategy getBillingStrategy() { return billingStrategy; }
}
