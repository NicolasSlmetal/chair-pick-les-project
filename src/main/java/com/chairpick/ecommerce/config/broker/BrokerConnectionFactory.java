package com.chairpick.ecommerce.config.broker;

public interface BrokerConnectionFactory {

    Object createConnection(String url, String username, String password);
}
