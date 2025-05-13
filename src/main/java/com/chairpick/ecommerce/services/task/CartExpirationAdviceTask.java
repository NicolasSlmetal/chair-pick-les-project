package com.chairpick.ecommerce.services.task;

import com.chairpick.ecommerce.model.Cart;


public class CartExpirationAdviceTask extends Task<Cart> {

    public CartExpirationAdviceTask() {

    }

    public CartExpirationAdviceTask(Cart info) {
        super(info, TaskType.CART_EXPIRATION_ADVICE);
    }
}
