package com.chairpick.ecommerce.services.task;

import com.chairpick.ecommerce.model.Cart;

import java.util.List;

public class CartExpirationNotificationTask extends Task<List<Cart>> {

    public CartExpirationNotificationTask() {
        super();
    }

    public CartExpirationNotificationTask(List<Cart> info) {
        super(info, TaskType.CART_EXPIRATION_NOTIFICATION);
    }
}
