package com.chairpick.ecommerce.services.task.consumer;

import com.chairpick.ecommerce.services.task.handler.TaskHandlerRegistry;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ConsumerRegistry {

    Map<ConsumerType, TaskConsumer> consumers = new HashMap<>();

    public ConsumerRegistry(TaskHandlerRegistry taskHandlerRegistry) {
        consumers.put(ConsumerType.RABBIT_MQ, new RabbitMqConsumer(taskHandlerRegistry));

    }

    public TaskConsumer getConsumer(ConsumerType type) {
        return consumers.get(type);
    }
}
