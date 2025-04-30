package com.chairpick.ecommerce.model.payment.factory;

import com.chairpick.ecommerce.model.Coupon;
import com.chairpick.ecommerce.model.CreditCard;
import com.chairpick.ecommerce.model.payment.factory.interfaces.PaymentStrategyFactory;
import com.chairpick.ecommerce.model.payment.strategy.PaymentStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Component
public class PaymentStrategyFactoryRegistry {

    final Map<String, PaymentStrategyFactory> factories = new HashMap<>();

    public PaymentStrategyFactoryRegistry() {

        factories.put("COUPON", new CouponPaymentFactory());
        factories.put("CREDIT_CARD", new CreditCardPaymentFactory());
        factories.put("CREDIT_CARD_AND_COUPON", new CreditCardAndCouponPaymentFactory());
    }


    public PaymentStrategy createPayment(List<Object> paymentMethods, Map<Long, Double> paymentValues) {
        Predicate<Object> isAllCreditCard = o -> o instanceof CreditCard;
        Predicate<Object> isAllCoupon = o -> o instanceof Coupon;
        Predicate<Object> isAllCreditCardAndCoupon = o -> o instanceof CreditCard || o instanceof Coupon;

        if (paymentMethods.stream().allMatch(isAllCreditCard)) {
            return factories.get("CREDIT_CARD").createPayment(paymentMethods, paymentValues);
        }

        if (paymentMethods.stream().allMatch(isAllCoupon)) {
            return factories.get("COUPON").createPayment(paymentMethods, paymentValues);
        }
        return factories.get("CREDIT_CARD_AND_COUPON").createPayment(paymentMethods, paymentValues);
    }
}
