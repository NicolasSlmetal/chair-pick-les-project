package com.chairpick.ecommerce.services.task.handler;

import com.chairpick.ecommerce.model.Cart;
import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.model.enums.CartItemStatus;
import com.chairpick.ecommerce.repositories.CartRepository;
import com.chairpick.ecommerce.services.task.CartExpirationNotificationTask;
import com.chairpick.ecommerce.services.task.Task;
import com.chairpick.ecommerce.services.task.TaskType;
import com.chairpick.ecommerce.services.task.interfaces.TaskConfirmation;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class CartExpirationNotificationTaskHandler implements TaskHandler {

    private final SimpMessagingTemplate messagingTemplate;

    public CartExpirationNotificationTaskHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public <T> void handle(Task<T> task, TaskConfirmation confirmation) {
        confirmation.confirm();
        CartExpirationNotificationTask cartExpirationNotificationTask = convertToCartExpirationNotificationTask(task);
        Cart cart = cartExpirationNotificationTask.getInfo();

        messagingTemplate.convertAndSendToUser(cart.getCustomer().getUser().getEmail(), "/notifications/cart-expiration", cartExpirationNotificationTask.getInfo());
    }

    private <T> CartExpirationNotificationTask convertToCartExpirationNotificationTask(Task<T> task) {
        if (task.getType().equals(TaskType.CART_EXPIRATION_NOTIFICATION)) {
            return new CartExpirationNotificationTask((Cart) task.getInfo());
        } else {
            throw new IllegalArgumentException("Invalid task type");
        }
    }
}
