package com.chairpick.ecommerce.config;

import com.chairpick.ecommerce.config.broker.DefaultRabbitMqBindings;
import com.chairpick.ecommerce.config.broker.RabbitMqConnectionFactory;
import com.chairpick.ecommerce.config.broker.RabbitMqConfig;
import com.chairpick.ecommerce.services.task.broker.RabbitMqData;
import com.chairpick.ecommerce.services.task.broker.RabbitMqDestination;
import com.chairpick.ecommerce.services.task.consumer.ConsumerRegistry;
import com.chairpick.ecommerce.services.task.consumer.ConsumerType;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMqInitializer {

    private final Connection connection;
    private Channel channel;

    private final ConsumerRegistry consumerRegistry;

    public RabbitMqInitializer(ConsumerRegistry consumerRegistry, Connection connection) {
        this.consumerRegistry = consumerRegistry;
        this.connection = connection;
    }

    private final Map<String, String> exchangeQueueBindings = DefaultRabbitMqBindings.getDefaultBindings();

    @PostConstruct
    public void setupRabbitMq() {
        try {
            channel = connection.createChannel();
            for (Map.Entry<String, String> entry : exchangeQueueBindings.entrySet()) {
                String exchangeName = entry.getKey();
                String queueName = entry.getValue();

                RabbitMqConfig rabbitMqConfig = new RabbitMqConfig();
                channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT, true);
                channel.queueDeclare(queueName, true, false, false, null);
                channel.queueBind(queueName, exchangeName, rabbitMqConfig.getRoutingKey(entry.getKey(), entry.getValue()));
                channel.basicQos(1);

                RabbitMqDestination destination = new RabbitMqDestination(queueName, exchangeName);
                RabbitMqData rabbitMqData = new RabbitMqData(destination, channel);
                consumerRegistry.getConsumer(ConsumerType.RABBIT_MQ).consume(rabbitMqData);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to create RabbitMQ connection", e);
        }
    }

    @PreDestroy
    public void closeRabbitMq() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to close RabbitMQ connection", e);
        }
    }


}
