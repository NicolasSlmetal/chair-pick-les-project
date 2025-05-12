package com.chairpick.ecommerce.config.broker;

import com.rabbitmq.client.Connection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqManager {

    @Value("${rabbitmq.host}")
    private String url;

    @Value("${rabbitmq.username}")
    private String username;

    @Value("${rabbitmq.password}")
    private String password;

    @Bean
    Connection getConnection() {
        RabbitMqConnectionFactory rabbitConn = new RabbitMqConnectionFactory();
        return rabbitConn.createConnection(url, username, password);
    }
}
