package com.chairpick.ecommerce.config.broker;

import java.util.Map;

public class DefaultRabbitMqBindings {

    public static Map<String, String> getDefaultBindings() {
        return Map.of(
                "cart-check-exchange", "cart-check-queue",
                "stock-check-exchange", "stock-check-queue",
                "email-exchange", "email-queue",
                "notification-exchange", "notification-queue"
        );
    }
}
