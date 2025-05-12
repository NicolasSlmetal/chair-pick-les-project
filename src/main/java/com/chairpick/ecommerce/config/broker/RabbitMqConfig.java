package com.chairpick.ecommerce.config.broker;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.impl.AMQBasicProperties;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;

import java.util.HashMap;
import java.util.Map;

public class RabbitMqConfig {

    public final Map<String, String> bindingsToRoutingKeys = new HashMap<>();
    public final Map<String, AMQBasicProperties> bindingsToProperties = new HashMap<>();

    public RabbitMqConfig() {
        bindingsToRoutingKeys.put("cart-check-exchange-cart-check-queue", "cart-check-routing-key");
        bindingsToRoutingKeys.put("stock-check-exchange-stock-check-queue", "stock-check-routing-key");
        bindingsToRoutingKeys.put("email-exchange-email-queue", "email-routing-key");
        bindingsToRoutingKeys.put("notification-exchange-notification-queue", "notification-routing-key");

    }

    public  String getRoutingKey(String exchange, String queue) {
        return bindingsToRoutingKeys.get(exchange + "-" + queue);
    }


}
