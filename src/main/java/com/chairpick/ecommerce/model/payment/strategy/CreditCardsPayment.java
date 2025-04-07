package com.chairpick.ecommerce.model.payment.strategy;

import com.chairpick.ecommerce.model.CreditCard;
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
public class CreditCardsPayment extends DomainEntity implements PaymentStrategy {

    private Map<CreditCard, Double> creditCardPayments;

    @Override
    public List<ErrorCode> validatePayment(double orderTotalValue) {
        double minValue = creditCardPayments.size() > 1 ? 10.00 : 0.00;

        creditCardPayments.forEach((creditCardPayments, value) -> {
            if (value < minValue) {
                getErrors().add(ErrorCode.INVALID_PAYMENT_VALUE_FOR_CREDIT_CARD);
            }

            if (value > orderTotalValue) {
                getErrors().add(ErrorCode.INVALID_PAYMENT_VALUE_FOR_CREDIT_CARD);
            }
        });

        double totalValue = creditCardPayments.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        if (totalValue != orderTotalValue) {
            getErrors().add(ErrorCode.INVALID_PAYMENT_VALUE_FOR_CREDIT_CARD);
        }

        return getErrors();
    }

    @Override
    public void validate() {
        verifyIfHasErrors();
    }
}


