package com.chairpick.ecommerce.services.task.broker;

import com.chairpick.ecommerce.services.task.Task;
import com.chairpick.ecommerce.services.task.interfaces.TaskDestinationResolver;
import com.chairpick.ecommerce.services.task.TaskType;

import java.util.HashMap;
import java.util.Map;

public class RabbitMqDestinationResolver implements TaskDestinationResolver {

    Map<TaskType, String> taskTypeQueueMap = new HashMap<>();
    Map<TaskType, String> taskTypeExchangeMap = new HashMap<>();

    public RabbitMqDestinationResolver() {
        taskTypeQueueMap.put(TaskType.CHECK_CART, "cart-check-queue");
        taskTypeQueueMap.put(TaskType.CHECK_STOCK, "stock-check-queue");
        taskTypeQueueMap.put(TaskType.SEND_EMAIL, "email-queue");
        taskTypeQueueMap.put(TaskType.SEND_SMS, "sms-queue");
        taskTypeQueueMap.put(TaskType.CART_EXPIRATION_NOTIFICATION, "notification-queue");
        taskTypeExchangeMap.put(TaskType.CHECK_CART, "cart-check-exchange");
        taskTypeExchangeMap.put(TaskType.CHECK_STOCK, "stock-check-exchange");
        taskTypeExchangeMap.put(TaskType.SEND_EMAIL, "email-exchange");
        taskTypeExchangeMap.put(TaskType.SEND_SMS, "sms-exchange");
        taskTypeExchangeMap.put(TaskType.CART_EXPIRATION_NOTIFICATION, "notification-exchange");
    }

    @Override
    public <T> Destination resolveDestination(Task<T> task) {
        TaskType taskType = task.getType();
        String queueName = taskTypeQueueMap.get(taskType);
        String exchangeName = taskTypeExchangeMap.get(taskType);

        return new RabbitMqDestination(queueName, exchangeName);
    }
}
