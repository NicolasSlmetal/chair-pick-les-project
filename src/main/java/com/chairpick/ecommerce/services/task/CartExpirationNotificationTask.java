package com.chairpick.ecommerce.services.task;

import com.chairpick.ecommerce.model.Cart;

public class CartExpirationNotificationTask extends Task<Cart> {

    public CartExpirationNotificationTask() {
        super();
    }

    public CartExpirationNotificationTask(Cart cart) {
        super(cart, TaskType.CART_EXPIRATION_NOTIFICATION);
    }
}
