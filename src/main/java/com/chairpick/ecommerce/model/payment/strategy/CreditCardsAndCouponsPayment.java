package com.chairpick.ecommerce.model.payment.strategy;

import com.chairpick.ecommerce.model.Coupon;
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
public class CreditCardsAndCouponsPayment extends DomainEntity implements PaymentStrategy {

    private Order order;
    private Map<CreditCard, Double> creditCardPayments;
    private List<Coupon> coupons;


    @Override
    public List<ErrorCode> validatePayment(double orderTotalValue) {
        creditCardPayments.forEach(((creditCard, value) -> {
            if (value < 0.00) {
                getErrors().add(ErrorCode.INVALID_PAYMENT_VALUE_FOR_CREDIT_CARD);
            }

            if (value > orderTotalValue) {
                getErrors().add(ErrorCode.INVALID_PAYMENT_VALUE_FOR_CREDIT_CARD);
            }
        }));

        double totalValueCreditCards = creditCardPayments.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        double totalValueCoupons = coupons.stream()
                .mapToDouble(Coupon::getValue)
                .sum();

        if (totalValueCreditCards + totalValueCoupons > orderTotalValue) {
            getErrors().add(ErrorCode.INVALID_PAYMENT_VALUE_FOR_COUPON);
        }

        return getErrors();

    }

    @Override
    public void validate() {
        verifyIfHasErrors();
    }
}
