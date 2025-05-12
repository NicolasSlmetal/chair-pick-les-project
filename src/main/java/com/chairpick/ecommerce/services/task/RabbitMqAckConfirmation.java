package com.chairpick.ecommerce.services.task;

import com.chairpick.ecommerce.services.task.interfaces.TaskConfirmation;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;



public class RabbitMqAckConfirmation implements TaskConfirmation {

    private final Channel channel;
    private final Delivery deliveryTag;

    public RabbitMqAckConfirmation(Channel channel, Delivery deliveryTag) {
        this.channel = channel;
        this.deliveryTag = deliveryTag;
    }

    @Override
    public void confirm() {
        try {
            channel.basicAck(deliveryTag.getEnvelope().getDeliveryTag(), false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to acknowledge message", e);
        }
    }

    @Override
    public void reject(boolean retry) {
        try {
            channel.basicNack(deliveryTag.getEnvelope().getDeliveryTag(), false, retry);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reject message", e);
        }
    }
}
