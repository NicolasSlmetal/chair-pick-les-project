package com.chairpick.ecommerce.services.task.handler;

import com.chairpick.ecommerce.model.Cart;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.Item;
import com.chairpick.ecommerce.model.enums.CartItemStatus;
import com.chairpick.ecommerce.repositories.CartRepository;
import com.chairpick.ecommerce.services.task.*;
import com.chairpick.ecommerce.services.task.interfaces.TaskConfirmation;
import com.chairpick.ecommerce.services.task.interfaces.TaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CartCheckTaskHandler implements TaskHandler {

    private final CartRepository cartRepository;
    private final TaskExecutor taskExecutor;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    public CartCheckTaskHandler(CartRepository cartRepository, TaskExecutor taskExecutor) {
        this.cartRepository = cartRepository;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public <T> void handle(Task<T> task, TaskConfirmation confirmation) {

        CartCheckTask cartCheckTask = convertToCartCheckTask(task);

        Cart cart = cartCheckTask.getInfo();

        LocalDateTime endTime = cart.getEntryDate().plusMinutes(30);
        long remainingTime = ChronoUnit.MINUTES.between(cart.getEntryDate(), endTime);

        executorService.schedule(() -> {

            List<Cart> carts = cartRepository.findByCustomer(cart.getCustomer());


            if (carts.isEmpty()) {
                confirmation.confirm();
                return;
            }

            Cart latestCart = carts.stream()
                    .max(Comparator.comparing(Cart::getEntryDate))
                    .orElse(null);

            if (!latestCart.getId().equals(cart.getId())) {
                confirmation.confirm();
                return;
            }

            taskExecutor.execute(new CartExpirationAdviceTask(cart));

        }, remainingTime - 5, TimeUnit.MINUTES);

        executorService.schedule(() -> {
            List<Cart> carts = cartRepository.findByCustomer(cart.getCustomer());
            if (carts.isEmpty()) {
                confirmation.confirm();
                return;
            }

            Cart latestCart = carts.stream()
                    .max(Comparator.comparing(Cart::getEntryDate))
                    .orElse(null);

            if (!latestCart.getId().equals(cart.getId())) {
                confirmation.confirm();
                return;
            }

            carts.stream()
                .peek(customerCart -> customerCart.setStatus(CartItemStatus.EXPIRED))
                .forEach(customerCart -> {
                    Item item = customerCart.getItem();
                    item.setReservedAmount(item.getReservedAmount() - customerCart.getAmount());
                });

            List<Cart> updatedCarts = cartRepository.batchUpdateCarts(carts);

            taskExecutor.execute(new CartExpirationNotificationTask(updatedCarts));
            confirmation.confirm();
        }, remainingTime, TimeUnit.MINUTES);
    }

    private <T> CartCheckTask convertToCartCheckTask(Task<T> task) {
        if (task.getType().equals(TaskType.CHECK_CART)) {
            return new CartCheckTask((Cart) task.getInfo());
        } else {
            throw new IllegalArgumentException("Invalid task type");
        }
    }
}
