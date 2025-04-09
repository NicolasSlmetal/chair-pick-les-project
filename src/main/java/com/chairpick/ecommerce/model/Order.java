package com.chairpick.ecommerce.model;

import com.chairpick.ecommerce.model.enums.OrderStatus;
import com.chairpick.ecommerce.model.payment.strategy.PaymentStrategy;
import com.chairpick.ecommerce.utils.ErrorCode;
import com.google.common.base.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@SuperBuilder
public class Order extends DomainEntity {

    private Customer customer;

    private List<OrderItem> items;

    private int totalAmount;

    private double totalValue;

    private OrderStatus status;

    private Address deliveryAddress;

    private Address billingAddress;

    private PaymentStrategy payment;

    private LocalDate createdDate;

    private LocalDate updatedDate;


    @Override
    public void validate() {
        if (customer == null) {
            getErrors().add(ErrorCode.CUSTOMER_REQUIRED);
        }

        if (status == null) {
            getErrors().add(ErrorCode.ORDER_STATUS_REQUIRED);
        }

        if (!validateCreatedDate()) {
            getErrors().add(ErrorCode.INVALID_CREATED_DATE);
        }

        if (!validateUpdatedDate()) {
            getErrors().add(ErrorCode.INVALID_UPDATED_DATE);
        }

        if (deliveryAddress == null) {
            getErrors().add(ErrorCode.DELIVERY_ADDRESS_REQUIRED);
        }
        if (billingAddress == null) {
            getErrors().add(ErrorCode.BILLING_ADDRESS_REQUIRED);
        }

        if (payment == null) {
            getErrors().add(ErrorCode.PAYMENT_REQUIRED);
        }

        if (items == null || items.isEmpty()) {
            getErrors().add(ErrorCode.ITEM_REQUIRED);
        }

        if (totalAmount <= 0) {
            getErrors().add(ErrorCode.AMOUNT_REQUIRED);
        }

        if (totalValue <= 0) {
            getErrors().add(ErrorCode.VALUE_REQUIRED);
        }

        List<ErrorCode> errorsFromPayment = Optional.of(payment
                .validatePayment(totalValue)
                .stream()
                .filter(error -> error != ErrorCode.REQUIRE_GENERATE_SWAP_COUPON)
                .toList()).or(List.of());

        getErrors().addAll(errorsFromPayment);

        verifyIfHasErrors();
        items.forEach(OrderItem::validate);

    }

    private boolean validateUpdatedDate() {
        if (updatedDate == null) {
            return false;
        }

        return !updatedDate.isAfter(LocalDate.now());
    }

    private boolean requiresSwapCoupon() {
        return payment.validatePayment(totalValue).contains(ErrorCode.REQUIRE_GENERATE_SWAP_COUPON);
    }

    private boolean validateCreatedDate() {
        if (createdDate == null) {
            return false;
        }

        return !createdDate.isAfter(LocalDate.now());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return getTotalAmount() == order.getTotalAmount() && Double.compare(getTotalValue(), order.getTotalValue()) == 0 && Objects.equals(getCustomer(), order.getCustomer()) && Objects.equals(getDeliveryAddress(), order.getDeliveryAddress()) && Objects.equals(getBillingAddress(), order.getBillingAddress()) && Objects.equals(getCreatedDate(), order.getCreatedDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCustomer(), getTotalAmount(), getTotalValue(), getDeliveryAddress(), getBillingAddress(), getCreatedDate());
    }

    @Override
    public String toString() {
        return "Order{" +
                "customer=" + customer +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", totalValue=" + totalValue +
                ", billingAddress=" + billingAddress +
                ", deliveryAddress=" + deliveryAddress +
                ", payment=" + payment +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
}
