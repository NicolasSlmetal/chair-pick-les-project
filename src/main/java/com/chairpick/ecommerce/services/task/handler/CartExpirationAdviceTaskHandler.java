package com.chairpick.ecommerce.services.task.handler;

import com.chairpick.ecommerce.model.Cart;
import com.chairpick.ecommerce.services.task.CartExpirationAdviceTask;
import com.chairpick.ecommerce.services.task.Task;
import com.chairpick.ecommerce.services.task.interfaces.TaskConfirmation;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class CartExpirationAdviceTaskHandler implements TaskHandler {

    private final SimpMessagingTemplate messagingTemplate;

    public CartExpirationAdviceTaskHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    @Override
    public <T> void handle(Task<T> task, TaskConfirmation confirmation) {
        CartExpirationAdviceTask cartExpirationAdviceTask = convertToCartExpirationAdviceTask(task);
        Cart cart = cartExpirationAdviceTask.getInfo();
        String message = "Os itens no carrinho expirarão em 5 minutos";
        messagingTemplate.convertAndSendToUser(cart.getCustomer().getUser().getEmail(), "/notifications/cart-expiration-advice", message);
        confirmation.confirm();
    }

    private <T> CartExpirationAdviceTask convertToCartExpirationAdviceTask(Task<T> task) {
        if (task instanceof CartExpirationAdviceTask cartExpirationAdviceTask) return cartExpirationAdviceTask;

        throw new RuntimeException("Invalid task provided");
    }
}
