package com.chairpick.ecommerce.services.task.handler;

import com.chairpick.ecommerce.model.Cart;
import com.chairpick.ecommerce.model.Item;
import com.chairpick.ecommerce.model.enums.CartItemStatus;
import com.chairpick.ecommerce.repositories.CartRepository;
import com.chairpick.ecommerce.services.task.CartCheckTask;
import com.chairpick.ecommerce.services.task.CartExpirationNotificationTask;
import com.chairpick.ecommerce.services.task.Task;
import com.chairpick.ecommerce.services.task.TaskType;
import com.chairpick.ecommerce.services.task.interfaces.TaskConfirmation;
import com.chairpick.ecommerce.services.task.interfaces.TaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        confirmation.confirm();
        CartCheckTask cartCheckTask = convertToCartCheckTask(task);

        Cart cart = cartCheckTask.getInfo();
        LocalDateTime endTime = cart.getEntryDate().plusMinutes(8);
        long remainingTime = ChronoUnit.MINUTES.between(cart.getEntryDate(), endTime);

        executorService.schedule(() -> {
            List<Cart> carts = cartRepository.findByCustomer(cart.getCustomer());
            Cart latestCart = carts.stream()
                    .filter(c -> c.getItem().getId().equals(cart.getItem().getId()))
                    .max(Comparator.comparing(Cart::getEntryDate))
                    .orElse(null);
            if (latestCart == null) {
                return;
            }

            if (!latestCart.getId().equals(cart.getId())) {
                return;
            }



        }, remainingTime - 5, TimeUnit.MINUTES);

        executorService.schedule(() -> {
            List<Cart> carts = cartRepository.findByCustomer(cart.getCustomer());
            Cart latestCart = carts.stream()
                    .filter(c -> c.getItem().getId().equals(cart.getItem().getId()))
                    .max(Comparator.comparing(Cart::getEntryDate))
                    .orElse(null);
            if (latestCart == null) {
                return;
            }

            if (!latestCart.getId().equals(cart.getId())) {
                return;
            }

            carts.forEach(expiredCart -> {
                expiredCart.setStatus(CartItemStatus.EXPIRED);
                Item item = expiredCart.getItem();
                item.setReservedAmount(item.getReservedAmount() - expiredCart.getAmount());
            });

            cartRepository.batchUpdateCarts(carts);

            taskExecutor.execute(new CartExpirationNotificationTask(cart));
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
