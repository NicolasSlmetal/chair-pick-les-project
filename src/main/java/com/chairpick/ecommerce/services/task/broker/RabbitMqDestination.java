package com.chairpick.ecommerce.services.task.broker;

import lombok.Getter;

@Getter
public class RabbitMqDestination extends Destination {

    private final String queueName;
    private final String exchangeName;

    public RabbitMqDestination(String queueName, String exchangeName) {
        this.queueName = queueName;
        this.exchangeName = exchangeName;
    }
}
