package com.chairpick.ecommerce.services.task.handler;

import com.chairpick.ecommerce.services.task.Task;
import com.chairpick.ecommerce.services.task.interfaces.TaskConfirmation;

public interface TaskHandler {

    <T> void handle(Task<T> task, TaskConfirmation confirmation);
}
