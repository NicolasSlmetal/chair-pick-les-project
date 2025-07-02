package com.chairpick.ecommerce.io.output;

import com.chairpick.ecommerce.model.Coupon;
import lombok.Getter;

import java.util.List;

@Getter
public class CouponPaymentDTO extends PaymentDTO{

    private final List<Coupon> coupons;

    public CouponPaymentDTO(Long orderId, List<Coupon> coupons) {
        super(orderId, PaymentType.COUPON);
        this.setTotalValue(coupons.stream().map(Coupon::getValue).reduce(0.0, Double::sum));
        this.coupons = coupons;
    }
}
