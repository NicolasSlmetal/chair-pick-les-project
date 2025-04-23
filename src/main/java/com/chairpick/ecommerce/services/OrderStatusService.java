package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.DomainValidationException;
import com.chairpick.ecommerce.model.Order;
import com.chairpick.ecommerce.model.OrderItem;
import com.chairpick.ecommerce.model.enums.OrderStatus;
import com.chairpick.ecommerce.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderStatusService {

    private final OrderRepository orderRepository;

    public OrderStatusService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void changeOrderStatus(Order order, OrderStatus newStatus) {
        OrderStatus beforeStatus = order.getStatus();
        if (isNotOrderStatusValid(beforeStatus, newStatus)) {
            throw new DomainValidationException("INVALID_ORDER_STATUS");
        }

        if (newStatus.equals(OrderStatus.APPROVED) ||
                newStatus.equals(OrderStatus.REPROVED) ||
                newStatus.equals(OrderStatus.PENDING)) {
            List<OrderItem> items = orderRepository.findAllOrderItems(order);
            order.setItems(items);
            order.getItems()
                    .forEach(orderItem -> orderItem.setStatus(newStatus));
        }

        order.setStatus(newStatus);
        order.setUpdatedDate(LocalDate.now());
    }

    public void changeOrderItemStatus(OrderItem orderItem, OrderStatus newStatus) {
        if (isNotOrderStatusValid(orderItem.getStatus(), newStatus)) {
            throw new DomainValidationException("INVALID_ORDER_STATUS");
        }

        orderItem.setStatus(newStatus);
    }

    public boolean isNotOrderStatusValid(OrderStatus oldStatus, OrderStatus newStatus) {
        return !switch (oldStatus) {
            case PENDING -> newStatus == OrderStatus.APPROVED || newStatus == OrderStatus.REPROVED;
            case APPROVED -> newStatus == OrderStatus.DELIVERING;
            case REPROVED -> newStatus == OrderStatus.PENDING;
            case DELIVERING -> newStatus == OrderStatus.DELIVERED;
            case DELIVERED -> newStatus == OrderStatus.SWAP_REQUEST;
            case SWAP_REQUEST -> newStatus == OrderStatus.IN_SWAP;
            case IN_SWAP -> newStatus == OrderStatus.SWAPPED;
            default -> false;
        };
    }
}
