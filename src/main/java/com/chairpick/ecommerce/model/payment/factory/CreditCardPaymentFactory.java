package com.chairpick.ecommerce.model.payment.factory;

import com.chairpick.ecommerce.model.CreditCard;
import com.chairpick.ecommerce.model.payment.factory.interfaces.PaymentStrategyFactory;
import com.chairpick.ecommerce.model.payment.strategy.CreditCardsPayment;
import com.chairpick.ecommerce.model.payment.strategy.PaymentStrategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CreditCardPaymentFactory implements PaymentStrategyFactory {
    @Override
    public PaymentStrategy createPayment(List<Object> paymentMethods, Map<Long, Double> paymentValues) {
        Map<CreditCard, Double> creditCards = paymentMethods.stream()
                .filter(CreditCard.class::isInstance)
                .map(CreditCard.class::cast)
                .collect(Collectors
                        .toMap(c -> c, c -> paymentValues.get(c.getId())));

        return CreditCardsPayment
                .builder()
                .creditCardPayments(creditCards)
                .build();
    }
}
