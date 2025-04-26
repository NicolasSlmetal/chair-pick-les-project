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

    final Map<Predicate<Object>, PaymentStrategyFactory> factories = new HashMap<>();

    public PaymentStrategyFactoryRegistry() {
        Predicate<Object> isAllCreditCard = o -> o instanceof CreditCard;
        Predicate<Object> isAllCoupon = o -> o instanceof Coupon;
        Predicate<Object> isAllCreditCardAndCoupon = o -> o instanceof CreditCard || o instanceof Coupon;
        factories.put(isAllCoupon, new CouponPaymentFactory());
        factories.put(isAllCreditCard, new CreditCardPaymentFactory());
        factories.put(isAllCreditCardAndCoupon, new CreditCardAndCouponPaymentFactory());
    }


    public PaymentStrategy createPayment(List<Object> paymentMethods, Map<Long, Double> paymentValues) {
        for (Map.Entry<Predicate<Object>, PaymentStrategyFactory> entry : factories.entrySet()) {
            if (paymentMethods.stream().allMatch(entry.getKey())) {
                return entry.getValue().createPayment(paymentMethods, paymentValues);
            }
        }
        throw new IllegalArgumentException("No suitable payment strategy found for the provided payment methods.");
    }
}
