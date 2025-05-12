package com.chairpick.ecommerce.services.task.interfaces;

public interface TaskConfirmation {

    void confirm();
    void reject(boolean retry);
}
