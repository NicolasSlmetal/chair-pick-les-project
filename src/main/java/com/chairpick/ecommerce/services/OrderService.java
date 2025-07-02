package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.DomainValidationException;
import com.chairpick.ecommerce.exceptions.EntityNotFoundException;
import com.chairpick.ecommerce.io.input.CreditCardPaymentInput;
import com.chairpick.ecommerce.io.input.OrderInput;
import com.chairpick.ecommerce.io.input.OrderStatusInput;
import com.chairpick.ecommerce.io.output.FreightValueDTO;
import com.chairpick.ecommerce.io.output.PaymentDTO;
import com.chairpick.ecommerce.model.*;
import com.chairpick.ecommerce.model.enums.CouponType;
import com.chairpick.ecommerce.model.enums.OrderStatus;
import com.chairpick.ecommerce.model.payment.factory.PaymentStrategyFactoryRegistry;
import com.chairpick.ecommerce.model.payment.factory.interfaces.PaymentStrategyFactory;
import com.chairpick.ecommerce.model.payment.strategy.CouponsPayment;
import com.chairpick.ecommerce.model.payment.strategy.CreditCardsAndCouponsPayment;
import com.chairpick.ecommerce.model.payment.strategy.CreditCardsPayment;
import com.chairpick.ecommerce.model.payment.strategy.PaymentStrategy;
import com.chairpick.ecommerce.projections.OrderReportByChairs;
import com.chairpick.ecommerce.repositories.*;
import com.chairpick.ecommerce.utils.pagination.PageInfo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final CreditCardRepository creditCardRepository;
    private final CouponRepository couponRepository;
    private final PaymentStrategyFactoryRegistry paymentStrategyFactory;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final FreightCalculatorService freightService;
    private final OrderStatusService orderStatusService;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository, AddressRepository addressRepository, ChairRepository chairRepository, CreditCardRepository creditCardRepository, CouponRepository couponRepository, PaymentStrategyFactoryRegistry paymentStrategyFactory, CartRepository cartRepository, FreightCalculatorService freightService, OrderStatusService orderStatusService) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.creditCardRepository = creditCardRepository;
        this.couponRepository = couponRepository;
        this.paymentStrategyFactory = paymentStrategyFactory;
        this.cartRepository = cartRepository;
        this.freightService = freightService;
        this.orderStatusService = orderStatusService;
    }

    public Order createOrder(Long customerId, OrderInput orderInput) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        Address billingAddress = addressRepository.findById(orderInput.billingAddressId())
                .orElseThrow(() -> new EntityNotFoundException("Billing address not found"));

        Address deliveryAddress = billingAddress;

        if (!billingAddress.getId().equals(orderInput.deliveryAddressId())) {
            deliveryAddress = addressRepository.findById(orderInput.deliveryAddressId())
                    .orElseThrow(() -> new EntityNotFoundException("Delivery address not found"));
        }

        List<CreditCard> creditCards = orderInput
                .creditCards()
                .stream()
                .map(creditCardId -> creditCardRepository.findById(creditCardId.id())
                        .orElseThrow(() -> new EntityNotFoundException("Credit card not found")))
                .toList();

        List<Coupon> coupons = orderInput
                .coupons()
                .stream()
                .map(couponId -> couponRepository.findCouponById(couponId)
                        .orElseThrow(() -> new EntityNotFoundException("Coupon with id " + couponId + " not found"))).toList();

        List<Object> paymentMethods = new ArrayList<>();
        paymentMethods.addAll(creditCards);
        paymentMethods.addAll(coupons);

        if (paymentMethods.isEmpty()) {
            throw new DomainValidationException("INVALID_PAYMENT_METHODS");
        }

        Map<Long, Double> creditCardValues = orderInput
                .creditCards()
                .stream()
                .collect(Collectors.toMap(CreditCardPaymentInput::id, CreditCardPaymentInput::value));

        PaymentStrategy payment = paymentStrategyFactory.createPayment(paymentMethods, creditCardValues);

        List<Cart> cartList = cartRepository.findByCustomer(customer);
        if (cartList.isEmpty()) {
            throw new EntityNotFoundException("No items in cart");
        }

        double totalValue = 0;
        int totalAmount = 0;
        for (Cart cart : cartList) {
            totalAmount += cart.getAmount();
            totalValue += cart.getItem().getChair().getSellPrice() * cart.getAmount();
        }

        double freightValue = 0;
        Map<Long, Double> freightMap = new HashMap<>();
        for (Cart cart : cartList) {
            FreightValueDTO freight = freightService.calculateFreight(cart.getItem().getChair(), deliveryAddress);
            freightValue += freight.value();
            freightMap.put(cart.getItem().getId(), freight.value());
        }
        totalValue += freightValue;
        totalValue = Math.round(totalValue * 100.0) / 100.0;

        Map<Long, Integer> itemAmountMap = cartList
                .stream()
                .collect(Collectors.toMap(cart -> cart.getItem().getId(), Cart::getAmount));

        LocalDateTime now = LocalDateTime.now();
        Order order = Order.builder()
                .totalAmount(totalAmount)
                .createdDate(now.toLocalDate())
                .updatedDate(now)
                .totalValue(totalValue)
                .status(OrderStatus.PENDING)
                .customer(customer)
                .payment(payment)
                .billingAddress(billingAddress)
                .deliveryAddress(deliveryAddress)
                .build();
        List<OrderItem> orderItems = cartList
                .stream()
                .map(Cart::getItem)
                .map(item ->
                    (OrderItem) OrderItem.builder()
                            .amount(itemAmountMap.get(item.getId()))
                            .freightValue(freightMap.get(item.getId()))
                            .order(order)
                            .status(OrderStatus.PENDING)
                            .value(item.getChair().getSellPrice())
                            .item(item)
                            .build()
                ).toList();

        order.setItems(orderItems);
        order.validate();

        return orderRepository.saveOrder(order, cartList);
    }

    private Order updateStockWhenApproved(Order order) {
        order.getItems()
                .forEach(item -> {
                    Item itemToUpdate = item.getItem();
                    itemToUpdate.setAmount(itemToUpdate.getAmount() - item.getAmount());
                    itemToUpdate.setReservedAmount(itemToUpdate.getReservedAmount() - item.getAmount());
                    item.validate();
                });
        PaymentDTO paymentDTO = orderRepository.findPaymentByOrder(order)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));


        double difference = calculateDifferencePrecisely(order, paymentDTO);

        if (difference > 0) {
            Coupon coupon = Coupon.builder()
                    .value(difference)
                    .type(CouponType.SWAP)
                    .customer(order.getCustomer())
                    .build();
            coupon.validate();
            return orderRepository.updateApprovedOrder(order, coupon);
        }
        return orderRepository.updateApprovedOrder(order);
    }

    private static double calculateDifferencePrecisely(Order order, PaymentDTO paymentDTO) {
        BigDecimal orderTotalValue = BigDecimal.valueOf(order.getTotalValue());
        BigDecimal paymentTotalValue = BigDecimal.valueOf(paymentDTO.getTotalValue());
        BigDecimal difference = paymentTotalValue.subtract(orderTotalValue).setScale(2, RoundingMode.HALF_DOWN);
        return difference.doubleValue();
    }

    public List<Order> findAllOrders(Map<String, String> parameters) {
        return orderRepository.findAllOrders(parameters);
    }

    public List<OrderReportByChairs> findAllReportsByChair(LocalDate startDate, LocalDate endDate) {
        LocalDate now = LocalDate.now();

        if (startDate == null || startDate.isAfter(now)) {
            startDate = now.minusMonths(1);
        }

        if (endDate == null || endDate.isAfter(now)) {
            endDate = now;
        }

        return orderRepository.findAllOrderReportsByChair(startDate, endDate);

    }

    public PageInfo<Order> findAllByCustomer(Long customerId, Map<String, String> parameters) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        if (!parameters.containsKey("page")) {
            parameters.put("page", "1");
        }

        if (!parameters.containsKey("limit")) {
            parameters.put("limit", "1");
        }

        return orderRepository.findAllByCustomer(customer, parameters);
    }

    public List<OrderItem> findOrderItems(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return orderRepository.findAllOrderItems(order);
    }

    public PaymentDTO findOrderPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        return orderRepository
                .findPaymentByOrder(order)
                .stream().peek(dto -> {
                    dto.setStatus(order.getStatus());
                    dto.setTotalValue(order.getTotalValue());
                })
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    public Order updateOrderStatus(Long orderId, OrderStatusInput status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        String[] orderStatus = Arrays.stream(OrderStatus
                .values())
                .map(OrderStatus::name)
                .sorted()
                .toArray(String[]::new);

        int statusIndex = Arrays.binarySearch(orderStatus, status.status());

        if (statusIndex < 0 || statusIndex >= orderStatus.length) {
            throw new DomainValidationException("INVALID_ORDER_STATUS");
        }
        OrderStatus newStatus = OrderStatus.valueOf(status.status());
        orderStatusService.changeOrderStatus(order, newStatus);

        if (newStatus.equals(OrderStatus.APPROVED)) {
            return updateStockWhenApproved(order);
        }

        return orderRepository.updateOrderStatus(order);
    }

    public OrderItem updateOrderItemStatus(Long orderItemId, OrderStatusInput status) {
        OrderItem orderItem = orderRepository.findOrderItemById(orderItemId)
                .orElseThrow(() -> new EntityNotFoundException("Order item not found"));

        String[] orderStatus = Arrays.stream(OrderStatus
                .values())
                .map(OrderStatus::name)
                .sorted()
                .toArray(String[]::new);

        int statusIndex = Arrays.binarySearch(orderStatus, status.status());

        if (statusIndex < 0 || statusIndex >= orderStatus.length) {
            throw new DomainValidationException("INVALID_ORDER_STATUS");
        }

        OrderStatus newStatus = OrderStatus.valueOf(status.status());
        orderStatusService.changeOrderItemStatus(orderItem, newStatus);
        return orderRepository.updateOrderItemStatus(orderItem);
    }

    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        List<OrderItem> orderItems = orderRepository.findAllOrderItems(order);
        orderItems.forEach(orderItem -> {
            Item item = orderItem.getItem();
            item.setReservedAmount(item.getReservedAmount() - orderItem.getAmount());
        });
        order.setItems(orderItems);

        if (!order.getStatus().equals(OrderStatus.REPROVED)) {
            throw new DomainValidationException("INVALID_ORDER_STATUS");
        }
        orderRepository.deleteOrder(order);
    }

}
