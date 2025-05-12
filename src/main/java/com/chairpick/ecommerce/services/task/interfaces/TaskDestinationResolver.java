package com.chairpick.ecommerce.services.task.interfaces;

import com.chairpick.ecommerce.services.task.Task;
import com.chairpick.ecommerce.services.task.broker.Destination;

public interface TaskDestinationResolver {


    <T> Destination resolveDestination(Task<T> task);
}
