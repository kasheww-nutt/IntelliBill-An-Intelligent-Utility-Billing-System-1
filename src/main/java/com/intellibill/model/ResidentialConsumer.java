package com.intellibill.model;

public class ResidentialConsumer extends Consumer {
    public ResidentialConsumer() {
        this.consumerType = ConsumerType.RESIDENTIAL;
    }

    public ResidentialConsumer(int consumerId, String name, String address) {
        super(consumerId, name, address, ConsumerType.RESIDENTIAL);
    }

    @Override
    public String getCategoryDescription() {
        return "Residential Consumer";
    }
}
