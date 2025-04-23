package com.chairpick.ecommerce.model;

import com.chairpick.ecommerce.model.enums.CouponType;
import com.chairpick.ecommerce.utils.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class Coupon extends DomainEntity {

    private Customer customer;
    private double value;
    private CouponType type;

    @Override
    public void validate() {
        if (customer == null) {
            getErrors().add(ErrorCode.CUSTOMER_REQUIRED);
        }

        if (value <= 0.00) {
            getErrors().add(ErrorCode.INVALID_COUPON_VALUE);
        }

        if (type == null) {
            getErrors().add(ErrorCode.INVALID_COUPON_TYPE);
        }

        verifyIfHasErrors();
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "customer=" + customer +
                ", value=" + value +
                ", type=" + type +
                '}';
    }
}
