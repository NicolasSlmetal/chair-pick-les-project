package com.chairpick.ecommerce.services.task.consumer;

import com.chairpick.ecommerce.services.task.RabbitMqAckConfirmation;
import com.chairpick.ecommerce.services.task.Task;
import com.chairpick.ecommerce.services.task.TaskType;
import com.chairpick.ecommerce.services.task.broker.BrokerData;
import com.chairpick.ecommerce.services.task.broker.RabbitMqData;
import com.chairpick.ecommerce.services.task.broker.RabbitMqDestination;

import com.chairpick.ecommerce.services.task.handler.TaskHandler;
import com.chairpick.ecommerce.services.task.handler.TaskHandlerRegistry;
import com.chairpick.ecommerce.utils.task.TaskTypeParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;


public class RabbitMqConsumer implements TaskConsumer {

    private final TaskHandlerRegistry taskHandlerRegistry;

    public RabbitMqConsumer(TaskHandlerRegistry taskHandlerRegistry) {
        this.taskHandlerRegistry = taskHandlerRegistry;
    }

    @Override
    public void consume(BrokerData data) {
        RabbitMqData rabbitMqData = convertToRabbitMqData(data);
        Channel channel = rabbitMqData.getChannel();
        RabbitMqDestination destination = (RabbitMqDestination) rabbitMqData.getDestination();

        try {
            channel.basicConsume(destination.getQueueName(), false, (consumerTag, message) -> {

                String messageBody = new String(message.getBody());

                Task<?> task = deserializeTask(messageBody);
                TaskHandler handler = taskHandlerRegistry.getHandler(task.getType());
                if (handler == null) {
                    System.err.println("No handler found for task type: " + task.getType());
                    channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
                    return;
                }
                RabbitMqAckConfirmation ackConfirmation = new RabbitMqAckConfirmation(channel, message);

                handler.handle(task, ackConfirmation);

            }, consumerTag -> {

            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to consume message from RabbitMQ", e);
        }

    }

    private Task<?> deserializeTask(String messageBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        TaskType taskType = TaskTypeParser.parse(messageBody);
        objectMapper.registerModule(new JavaTimeModule());
        if (taskType == null) {
            throw new IllegalArgumentException("Invalid task type in message body");
        }
        try {

            return objectMapper.readValue(messageBody, Task.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize task", e);
        }
    }

    private RabbitMqData convertToRabbitMqData(BrokerData data) {
        if (data instanceof RabbitMqData rabbitMqData) return rabbitMqData;
        throw new IllegalArgumentException("Invalid data type. Expected RabbitMqData.");
    }
}
