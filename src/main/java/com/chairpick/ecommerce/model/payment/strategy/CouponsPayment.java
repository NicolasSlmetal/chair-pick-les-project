package com.chairpick.ecommerce.model.payment.strategy;

import com.chairpick.ecommerce.model.Coupon;
import com.chairpick.ecommerce.model.DomainEntity;
import com.chairpick.ecommerce.model.Order;
import com.chairpick.ecommerce.utils.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@SuperBuilder
public class CouponsPayment extends DomainEntity implements PaymentStrategy {

    private Order order;
    private List<Coupon> couponList;

    @Override
    public void validate() {

    }

    @Override
    public List<ErrorCode> validatePayment(double orderTotalValue) {
        couponList.forEach((coupon) -> {
            if (coupon.getValue() > orderTotalValue) {
                getErrors().add(ErrorCode.INVALID_PAYMENT_VALUE_FOR_COUPON);
            }
        });

        double totalValue = couponList.stream()
                .mapToDouble(Coupon::getValue)
                .sum();

        if (totalValue > orderTotalValue) {
            getErrors().add(ErrorCode.REQUIRE_GENERATE_SWAP_COUPON);
        }

        if (totalValue < orderTotalValue) {
            getErrors().add(ErrorCode.INVALID_PAYMENT_VALUE_FOR_COUPON);
        }

        return getErrors();
    }
}
