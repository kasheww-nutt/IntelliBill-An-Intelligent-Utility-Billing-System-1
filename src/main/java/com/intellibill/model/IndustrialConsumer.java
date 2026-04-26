package com.intellibill.model;

public class IndustrialConsumer extends Consumer {
    public IndustrialConsumer() {
        this.consumerType = ConsumerType.INDUSTRIAL;
    }

    public IndustrialConsumer(int consumerId, String name, String address) {
        super(consumerId, name, address, ConsumerType.INDUSTRIAL);
    }

    @Override
    public String getCategoryDescription() {
        return "Industrial Consumer";
    }
}
