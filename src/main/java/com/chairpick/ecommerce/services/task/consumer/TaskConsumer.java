package com.chairpick.ecommerce.services.task.consumer;

import com.chairpick.ecommerce.services.task.broker.BrokerData;

public interface TaskConsumer {

    void consume(BrokerData data);
}
