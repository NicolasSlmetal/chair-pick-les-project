package com.chairpick.ecommerce.services.task.broker;

import com.chairpick.ecommerce.config.broker.RabbitMqConfig;
import com.chairpick.ecommerce.services.task.MessageBrokerTaskExecutor;
import com.chairpick.ecommerce.services.task.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
@Primary
public class RabbitMqTaskExecutor extends MessageBrokerTaskExecutor {

    private final Connection connection;
    public RabbitMqTaskExecutor(Connection connection) {
        super(new RabbitMqDestinationResolver());
        this.connection = connection;
    }

    @Override
    protected <T> void publish(Task<T> task, Destination destination) {
        RabbitMqDestination rabbitMqDestination = convertDestinationToRabbitMq(destination);

        try  {
            Channel channel = connection.createChannel();

            String serializedTask = serializeTask(task);

            RabbitMqConfig rabbitMqConfig = new RabbitMqConfig();
            int DURABLE = 2;
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                    .deliveryMode(DURABLE)
                    .build();
            String routingKey = rabbitMqConfig.getRoutingKey(rabbitMqDestination.getExchangeName(), rabbitMqDestination.getQueueName());
            channel.basicPublish(rabbitMqDestination.getExchangeName(), routingKey, properties, serializedTask.getBytes());
            System.out.println("Published message with routing key \"" + routingKey + "\"");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> String serializeTask(Task<T> task) throws JsonProcessingException {
        ObjectMapper mapper = JsonMapper.builder()
                .build();

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        return mapper.writeValueAsString(task);
    }

    private RabbitMqDestination convertDestinationToRabbitMq(Destination destination) {
        if (destination instanceof RabbitMqDestination rabbitMqDestination) return rabbitMqDestination;
        throw new IllegalArgumentException("Invalid destination type: " + destination.getClass().getName());
    }
}

