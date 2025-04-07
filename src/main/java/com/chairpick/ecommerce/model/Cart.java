package com.chairpick.ecommerce.model;

import com.chairpick.ecommerce.model.enums.CartItemStatus;
import com.chairpick.ecommerce.utils.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Getter
@Setter
public class Cart extends DomainEntity {

    private Customer customer;
    private Item item;
    private int amount;
    private int limit;
    private LocalDateTime entryDate;
    private double price;
    private CartItemStatus status;


    @Override
    public void validate() {
        if (customer == null) {
            getErrors().add(ErrorCode.CUSTOMER_REQUIRED);
        }

        if (amount <= 0) {
            getErrors().add(ErrorCode.AMOUNT_REQUIRED);
        }

        if (!validateItem()) {
            getErrors().add(ErrorCode.ITEM_REQUIRED);
        }

        if (status == null) {
            getErrors().add(ErrorCode.CART_STATUS_REQUIRED);
        }

        if (!validateEntryDateTime()) {
            getErrors().add(ErrorCode.INVALID_ENTRY_DATE);
        }

        if (limit <= 0) {
            getErrors().add(ErrorCode.INVALID_LIMIT);
        }

        if (amount > limit) {
            getErrors().add(ErrorCode.AMOUNT_EXCEEDS_LIMIT);
        }

        verifyIfHasErrors();


    }

    private boolean validateItem() {
        return item != null;
    }

    private boolean validateEntryDateTime() {
        if (entryDate == null) {
            return false;
        }

        return !entryDate.isAfter(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Cart{" +
                "customer=" + customer +
                ", item=" + item +
                ", amount=" + amount +
                ", entryDate=" + entryDate +
                ", price=" + price +
                ", status=" + status +
                '}';
    }
}
