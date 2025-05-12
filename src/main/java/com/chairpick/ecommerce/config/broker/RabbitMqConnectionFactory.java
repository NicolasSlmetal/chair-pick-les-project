package com.chairpick.ecommerce.config.broker;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqConnectionFactory implements BrokerConnectionFactory {

    @Override
    public Connection createConnection(String url, String username, String password) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(url);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setPort(5672);
        try {
            return factory.newConnection();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create RabbitMQ connection", e);
        }
    }
}
