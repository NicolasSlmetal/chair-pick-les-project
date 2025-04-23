package com.chairpick.ecommerce.model.payment.strategy;

import com.chairpick.ecommerce.model.CreditCard;
import com.chairpick.ecommerce.utils.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class CreditCardsPayment implements PaymentStrategy {

    public static final double MIN_VALUE_FOR_MANY_CARDS = 10.00;
    private Map<CreditCard, Double> creditCardPayments;

    @Override
    public List<ErrorCode> validatePayment(double orderTotalValue) {
        List<ErrorCode> errors = new ArrayList<>();

        if (creditCardPayments == null || creditCardPayments.isEmpty()) {
            errors.add(ErrorCode.CREDIT_CARD_REQUIRED);
            return errors;
        }

        double minValue = creditCardPayments.size() > 1 ? MIN_VALUE_FOR_MANY_CARDS : 0.00;

        for (CreditCard creditCard : creditCardPayments.keySet()) {
            if (creditCard == null) {
                errors.add(ErrorCode.CREDIT_CARD_REQUIRED);
                break;
            }
            double value = creditCardPayments.get(creditCard);
            if (value <= minValue) {
                errors.add(ErrorCode.INVALID_PAYMENT_VALUE_FOR_CREDIT_CARD);
                break;
            }
            if (value > orderTotalValue) {
                errors.add(ErrorCode.INVALID_PAYMENT_VALUE_FOR_CREDIT_CARD);
                break;
            }
        }

        double totalValue = creditCardPayments.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        if (totalValue != orderTotalValue) {
            errors.add(ErrorCode.INVALID_PAYMENT_VALUE_FOR_CREDIT_CARD);
        }

        return errors;
    }

    @Override
    public double getTotalValue() {
        return creditCardPayments
                .values()
                .stream()
                .reduce(0.0, Double::sum);
    }
}


