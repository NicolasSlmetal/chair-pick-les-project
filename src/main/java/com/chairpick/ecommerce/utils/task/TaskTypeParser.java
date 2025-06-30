package com.chairpick.ecommerce.utils.task;

import com.chairpick.ecommerce.services.task.TaskType;

public class TaskTypeParser {

    public static TaskType parse(String serializedTask) {
        if (serializedTask == null || serializedTask.isEmpty()) {
            return null;
        }

        String[] parts = serializedTask.toUpperCase().replaceAll("\"", "").split(":");
        for (String part: parts) {
            if (part.startsWith("CHECK_CART")) {
                return TaskType.CHECK_CART;
            } else if (part.startsWith("CHECK_STOCK")) {
                return TaskType.CHECK_STOCK;
            } else if (part.startsWith("SEND_EMAIL")) {
                return TaskType.SEND_EMAIL;
            } else if (part.startsWith("SEND_SMS")) {
                return TaskType.SEND_SMS;
            } else if (part.startsWith("CART_EXPIRATION_NOTIFICATION")) {
                return TaskType.CART_EXPIRATION_NOTIFICATION;
            } else if (part.startsWith("CART_CHECK")) {
                return TaskType.CHECK_CART;
            } else if (part.startsWith("CART_EXPIRATION_ADVICE")) {
                return TaskType.CART_EXPIRATION_ADVICE;
            }
        }
        return null;
    }
}
