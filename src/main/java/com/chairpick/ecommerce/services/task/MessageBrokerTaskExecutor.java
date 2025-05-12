package com.chairpick.ecommerce.services.task;

import com.chairpick.ecommerce.services.task.broker.Destination;
import com.chairpick.ecommerce.services.task.interfaces.TaskDestinationResolver;
import com.chairpick.ecommerce.services.task.interfaces.TaskExecutor;

public abstract class MessageBrokerTaskExecutor implements TaskExecutor {

    protected TaskDestinationResolver resolver;

    public MessageBrokerTaskExecutor(TaskDestinationResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public final <T> void execute(Task<T> task) {
        Destination destination = resolver.resolveDestination(task);
        publish(task, destination);
    }


    protected abstract <T> void publish(Task<T> task, Destination destination);
}
