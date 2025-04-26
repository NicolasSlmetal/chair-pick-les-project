package com.chairpick.ecommerce.model;

import com.chairpick.ecommerce.model.enums.OrderStatus;
import com.chairpick.ecommerce.utils.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class OrderItem extends DomainEntity {

    private Long id;
    private Item item;
    private int amount;
    private double value;
    private double freightValue;
    private OrderStatus status;
    private Order order;
    private Swap swap;

    @Override
    public void validate() {
        if (item == null) {
            getErrors().add(ErrorCode.ITEM_REQUIRED);
        }

        if (amount <= 0) {
            getErrors().add(ErrorCode.AMOUNT_REQUIRED);
        }

        if (value <= 0) {
            getErrors().add(ErrorCode.VALUE_REQUIRED);
        }

        if (freightValue < 0) {
            getErrors().add(ErrorCode.FREIGHT_REQUIRED);
        }

        if (status == null) {
            getErrors().add(ErrorCode.ORDER_STATUS_REQUIRED);
        }

        if (order == null) {
            getErrors().add(ErrorCode.ORDER_REQUIRED);
        }

        verifyIfHasErrors();

        item.validate();
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", item=" + item +
                ", amount=" + amount +
                ", value=" + value +
                ", freightValue=" + freightValue +
                ", status=" + status +
                ", order=" + order +
                '}';
    }
}
