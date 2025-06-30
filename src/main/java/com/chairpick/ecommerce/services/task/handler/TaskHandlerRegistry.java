package com.chairpick.ecommerce.services.task.handler;

import com.chairpick.ecommerce.services.task.TaskType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TaskHandlerRegistry {

    private final Map<TaskType, TaskHandler> handlers = new HashMap<>();

    public TaskHandlerRegistry(CartCheckTaskHandler cartCheckTaskHandler,
                               CartExpirationNotificationTaskHandler cartExpirationNotificationTaskHandler,
                               CartExpirationAdviceTaskHandler cartExpirationAdviceTaskHandler,
                               SendMailTaskHandler sendMailTaskHandler){
        handlers.put(TaskType.CHECK_CART, cartCheckTaskHandler);
        handlers.put(TaskType.CART_EXPIRATION_NOTIFICATION, cartExpirationNotificationTaskHandler);
        handlers.put(TaskType.CART_EXPIRATION_ADVICE, cartExpirationAdviceTaskHandler);
        handlers.put(TaskType.SEND_EMAIL, sendMailTaskHandler);
    }

    public TaskHandler getHandler(TaskType type) {
        return handlers.get(type);
    }
}
