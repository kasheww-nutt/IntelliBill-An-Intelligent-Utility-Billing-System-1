package com.intellibill.model;

import java.io.Serializable;

public abstract class Consumer implements Serializable {
    protected int consumerId;
    protected String name;
    protected String address;
    protected ConsumerType consumerType;

    protected Consumer() {
    }

    protected Consumer(int consumerId, String name, String address, ConsumerType consumerType) {
        this.consumerId = consumerId;
        this.name = name;
        this.address = address;
        this.consumerType = consumerType;
    }

    public int getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(int consumerId) {
        this.consumerId = consumerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ConsumerType getConsumerType() {
        return consumerType;
    }

    public void setConsumerType(ConsumerType consumerType) {
        this.consumerType = consumerType;
    }

    public abstract String getCategoryDescription();

    @Override
    public String toString() {
        return "Consumer{" +
                "consumerId=" + consumerId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", consumerType=" + consumerType +
                '}';
    }
}
