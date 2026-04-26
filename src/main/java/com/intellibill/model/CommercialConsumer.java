package com.intellibill.model;

public class CommercialConsumer extends Consumer {
    public CommercialConsumer() {
        this.consumerType = ConsumerType.COMMERCIAL;
    }

    public CommercialConsumer(int consumerId, String name, String address) {
        super(consumerId, name, address, ConsumerType.COMMERCIAL);
    }

    @Override
    public String getCategoryDescription() {
        return "Commercial Consumer";
    }
}
