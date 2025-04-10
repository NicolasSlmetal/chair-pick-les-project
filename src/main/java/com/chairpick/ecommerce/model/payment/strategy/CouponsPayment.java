package com.chairpick.ecommerce.model.payment.strategy;

import com.chairpick.ecommerce.model.Coupon;
import com.chairpick.ecommerce.model.Order;
import com.chairpick.ecommerce.utils.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class CouponsPayment implements PaymentStrategy {

    private List<Coupon> couponList;

    @Override
    public List<ErrorCode> validatePayment(double orderTotalValue) {
        List<ErrorCode> errors = new ArrayList<>();
        if (couponList == null || couponList.isEmpty()) {
            errors.add(ErrorCode.COUPON_REQUIRED);
            return errors;
        }

        double totalValue = couponList.stream()
                .mapToDouble(Coupon::getValue)
                .sum();

        if (totalValue > orderTotalValue) {
            errors.add(ErrorCode.REQUIRE_GENERATE_SWAP_COUPON);
        }

        if (totalValue < orderTotalValue) {
            errors.add(ErrorCode.INVALID_PAYMENT_VALUE_FOR_COUPON);
        }

        return errors;
    }
}
