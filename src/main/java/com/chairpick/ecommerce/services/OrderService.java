package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.DomainValidationException;
import com.chairpick.ecommerce.exceptions.EntityNotFoundException;
import com.chairpick.ecommerce.io.input.CreditCartPaymentInput;
import com.chairpick.ecommerce.io.input.OrderInput;
import com.chairpick.ecommerce.io.output.FreightValueDTO;
import com.chairpick.ecommerce.model.*;
import com.chairpick.ecommerce.model.enums.OrderStatus;
import com.chairpick.ecommerce.model.payment.strategy.CouponsPayment;
import com.chairpick.ecommerce.model.payment.strategy.CreditCardsAndCouponsPayment;
import com.chairpick.ecommerce.model.payment.strategy.CreditCardsPayment;
import com.chairpick.ecommerce.model.payment.strategy.PaymentStrategy;
import com.chairpick.ecommerce.repositories.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final CreditCardRepository creditCardRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final FreightCalculatorService freightService;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository, AddressRepository addressRepository, ChairRepository chairRepository, CreditCardRepository creditCardRepository, CartRepository cartRepository, FreightCalculatorService freightService) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.creditCardRepository = creditCardRepository;
        this.cartRepository = cartRepository;
        this.freightService = freightService;
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
                .map(couponId -> (Coupon) Coupon.builder().build()).toList();

        List<Object> paymentMethods = new ArrayList<>();
        paymentMethods.addAll(creditCards);
        paymentMethods.addAll(coupons);

        if (paymentMethods.isEmpty()) {
            throw new DomainValidationException("INVALID_PAYMENT_METHODS");
        }
        Map<Long, Double> creditCardValues = orderInput.creditCards().stream().collect(Collectors.toMap(CreditCartPaymentInput::id, CreditCartPaymentInput::value));

        PaymentStrategy payment = getPaymentStrategy(paymentMethods, creditCardValues);

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

        LocalDate now = LocalDate.now();
        Order order = Order.builder()
                .totalAmount(totalAmount)
                .createdDate(now)
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
                .map(item -> {
                    item.setAmount(item.getAmount() - itemAmountMap.get(item.getId()));
                    item.setReservedAmount(item.getReservedAmount() - itemAmountMap.get(item.getId()));
                    return (OrderItem) OrderItem.builder()
                            .amount(itemAmountMap.get(item.getId()))
                            .freightValue(freightMap.get(item.getId()))
                            .order(order)
                            .status(OrderStatus.PENDING)
                            .value(item.getChair().getSellPrice() * itemAmountMap.get(item.getId()))
                            .item(item)
                            .build();
                }).toList();

        order.setItems(orderItems);
        order.validate();

        return orderRepository.saveOrderAndUpdateStock(order, cartList);
    }


    private PaymentStrategy getPaymentStrategy(List<Object> paymentMethods, Map<Long, Double> paymentValues) {
        Predicate<Object> isCreditCard = object -> object instanceof CreditCard card;
        Predicate<Object> isCoupon = object -> object instanceof Coupon coupon;
        Predicate<Object> isCreditCardAndCoupon = object -> object instanceof CreditCard card || object instanceof Coupon coupon;

        if (paymentMethods.stream().allMatch(isCreditCard)) {
            Map<CreditCard, Double> creditCardPayments = paymentMethods
                    .stream()
                    .filter(isCreditCard)
                    .map(object -> (CreditCard) object)
                    .collect(Collectors.toMap(creditCard -> creditCard, creditCard -> paymentValues.get(creditCard.getId())));

            return CreditCardsPayment
                    .builder()
                    .creditCardPayments(creditCardPayments)
                    .build();
        }

        if (paymentMethods.stream().allMatch(isCoupon)) {
            List<Coupon> coupons = paymentMethods
                    .stream()
                    .filter(isCoupon)
                    .map(object -> (Coupon) object)
                    .toList();

            return CouponsPayment
                    .builder()
                    .couponList(coupons)
                    .build();
        }

        if (!paymentMethods.stream().allMatch(isCreditCardAndCoupon)) {
            throw new DomainValidationException("INVALID_PAYMENT_METHODS");
        }

        Map<CreditCard, Double> creditCardPayments = paymentMethods
                .stream()
                .filter(isCreditCard)
                .map(object -> (CreditCard) object)
                .collect(Collectors.toMap(creditCard -> creditCard, creditCard -> paymentValues.get(creditCard.getId())));
        List<Coupon> coupons = paymentMethods
                .stream()
                .filter(isCoupon)
                .map(object -> (Coupon) object)
                .toList();

        return CreditCardsAndCouponsPayment
                .builder()
                .creditCardPayments(creditCardPayments)
                .coupons(coupons)
                .build();
    }

    public List<Order> findAllByCustomer(Long customerId, Map<String, String> parameters) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        return orderRepository.findAllByCustomer(customer, parameters);
    }
}
