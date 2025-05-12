package com.chairpick.ecommerce.services.task.interfaces;

import com.chairpick.ecommerce.services.task.Task;

public interface TaskExecutor {

    <T> void execute(Task<T> task);
}
