package com.chairpick.ecommerce.model.payment.factory;

import com.chairpick.ecommerce.model.Coupon;
import com.chairpick.ecommerce.model.payment.factory.interfaces.PaymentStrategyFactory;
import com.chairpick.ecommerce.model.payment.strategy.CouponsPayment;
import com.chairpick.ecommerce.model.payment.strategy.PaymentStrategy;

import java.util.List;
import java.util.Map;

public class CouponPaymentFactory implements PaymentStrategyFactory {

    public PaymentStrategy createPayment(List<Object> paymentMethods) {
        List<Coupon> coupons = paymentMethods.stream()
                .filter(Coupon.class::isInstance)
                .map(Coupon.class::cast)
                .toList();
        return CouponsPayment
                .builder()
                .couponList(coupons)
                .build();
    }

    @Override
    public PaymentStrategy createPayment(List<Object> paymentMethods, Map<Long, Double> paymentValues) {
        List<Coupon> coupons = paymentMethods.stream()
                .filter(Coupon.class::isInstance)
                .map(Coupon.class::cast)
                .toList();
        return CouponsPayment
                .builder()
                .couponList(coupons)
                .build();
    }
}
