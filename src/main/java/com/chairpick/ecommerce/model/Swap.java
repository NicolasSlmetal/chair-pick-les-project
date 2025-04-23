package com.chairpick.ecommerce.model;

import com.chairpick.ecommerce.model.enums.OrderStatus;
import com.chairpick.ecommerce.utils.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class Swap extends DomainEntity {

    private OrderItem orderItem;
    private int amount;
    private double value;
    private OrderStatus status;

    @Override
    public void validate() {
        if (orderItem == null) {
            getErrors().add(ErrorCode.ITEM_REQUIRED);
        }

        if (amount <= 0) {
            getErrors().add(ErrorCode.AMOUNT_REQUIRED);
        }

        if (amount > orderItem.getAmount()) {
            getErrors().add(ErrorCode.AMOUNT_EXCEEDS_LIMIT);
        }

        if (value <= 0) {
            getErrors().add(ErrorCode.VALUE_REQUIRED);
        }

        if (status == null) {
            getErrors().add(ErrorCode.SWAP_STATUS_REQUIRED);
        }

        verifyIfHasErrors();
    }
}
