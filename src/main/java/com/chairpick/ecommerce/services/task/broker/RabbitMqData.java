package com.chairpick.ecommerce.services.task.broker;

import com.rabbitmq.client.Channel;
import lombok.Getter;

@Getter
public class RabbitMqData extends BrokerData {

    private final Channel channel;

    public RabbitMqData(RabbitMqDestination rabbitMqDestination, Channel channel) {
        super(rabbitMqDestination);
        this.channel = channel;
    }
}
