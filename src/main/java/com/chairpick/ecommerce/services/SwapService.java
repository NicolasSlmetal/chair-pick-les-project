package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.DomainValidationException;
import com.chairpick.ecommerce.exceptions.EntityNotFoundException;
import com.chairpick.ecommerce.io.input.OrderStatusInput;
import com.chairpick.ecommerce.io.input.SwapInput;
import com.chairpick.ecommerce.model.*;
import com.chairpick.ecommerce.model.enums.CouponType;
import com.chairpick.ecommerce.model.enums.OrderStatus;
import com.chairpick.ecommerce.repositories.ItemRepository;
import com.chairpick.ecommerce.repositories.OrderRepository;
import com.chairpick.ecommerce.repositories.SwapRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class SwapService {

    private final SwapRepository swapRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderStatusService orderStatusService;

    public SwapService(SwapRepository swapRepository, OrderRepository orderRepository, ItemRepository itemRepository, OrderStatusService orderStatusService) {
        this.swapRepository = swapRepository;
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.orderStatusService = orderStatusService;
    }

    public Swap createSwapRequest(Long orderId, SwapInput swapInput) {
        System.out.println(swapInput);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        OrderItem orderItem = orderRepository.findOrderItemById(swapInput.orderItemId())
                .orElseThrow(() -> new EntityNotFoundException("Order item not found"));
        System.out.println(orderId + " - " + orderItem.getOrder().getId());
        if (!Objects.equals(orderItem.getOrder().getId(), orderId)) {;
            throw new EntityNotFoundException("Order item not found");
        }

        orderItem.setOrder(order);
        orderStatusService.changeOrderItemStatus(orderItem, OrderStatus.SWAP_REQUEST);

        Swap swap = Swap
                .builder()
                .orderItem(orderItem)
                .value(orderItem.getValue())
                .status(OrderStatus.SWAP_REQUEST)
                .amount(swapInput.amount())
                .build();

        swap.validate();
        return swapRepository.save(swap);
    }

    public List<Swap> findAllSwapsByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return swapRepository.findAllByOrder(order);
    }

    public Swap updateSwapStatus(Long orderId, Long swapId, OrderStatusInput input) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        Swap swap = swapRepository.findById(swapId)
                .orElseThrow(() -> new EntityNotFoundException("Swap not found"));

        if (!swap.getOrderItem().getOrder().getId().equals(order.getId())) {
            throw new EntityNotFoundException("Swap not found");
        }

        String[] orderStatus = Arrays.stream(OrderStatus
                        .values())
                .map(OrderStatus::name)
                .sorted()
                .toArray(String[]::new);

        int statusIndex = Arrays.binarySearch(orderStatus, input.status());

        if (statusIndex < 0 || statusIndex >= orderStatus.length) {
            throw new DomainValidationException("INVALID_ORDER_STATUS");
        }

        OrderStatus newStatus = OrderStatus.valueOf(input.status());

        orderStatusService.changeOrderItemStatus(swap.getOrderItem(), newStatus);
        swap.setStatus(newStatus);

        if (newStatus.equals(OrderStatus.SWAPPED)) {
            Item item = itemRepository.findItemById(swap.getOrderItem().getItem().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Item not found"));
            item.setAmount(item.getAmount() + swap.getAmount());

            swap.getOrderItem().setItem(item);

            Coupon coupon = Coupon
                    .builder()
                    .customer(Customer
                            .builder()
                            .id(order.getCustomer().getId())
                            .build())
                    .type(CouponType.SWAP)
                    .value(swap.getValue() * swap.getAmount())
                    .build();
            return swapRepository.confirmSwap(swap, coupon);
        }

        return swapRepository.updateStatus(swap);
    }
}
