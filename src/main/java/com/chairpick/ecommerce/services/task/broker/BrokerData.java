package com.chairpick.ecommerce.services.task.broker;

import lombok.Getter;

@Getter
public abstract class BrokerData {

    private final Destination destination;

    protected BrokerData(Destination destination) {
        this.destination = destination;
    }
}
