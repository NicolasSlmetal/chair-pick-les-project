package com.chairpick.ecommerce.services.task;

import com.chairpick.ecommerce.model.Cart;

import java.util.UUID;

public class CartCheckTask extends Task<Cart> {

    public CartCheckTask() {

    }

    public CartCheckTask(Cart info) {
        super(info, TaskType.CHECK_CART);
    }


    @Override
    public String toString() {
        return "CartCheckTask{" +
                "info=" + info +
                '}';
    }
}
