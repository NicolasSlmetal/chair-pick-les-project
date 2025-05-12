package com.chairpick.ecommerce.services.task.handler;

import com.chairpick.ecommerce.services.task.TaskType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TaskHandlerRegistry {

    private final Map<TaskType, TaskHandler> handlers = new HashMap<>();

    public TaskHandlerRegistry(CartCheckTaskHandler cartCheckTaskHandler,
                               CartExpirationNotificationTaskHandler cartExpirationNotificationTaskHandler) {
        handlers.put(TaskType.CHECK_CART, cartCheckTaskHandler);
        handlers.put(TaskType.CART_EXPIRATION_NOTIFICATION, cartExpirationNotificationTaskHandler);
    }

    public TaskHandler getHandler(TaskType type) {
        return handlers.get(type);
    }
}
