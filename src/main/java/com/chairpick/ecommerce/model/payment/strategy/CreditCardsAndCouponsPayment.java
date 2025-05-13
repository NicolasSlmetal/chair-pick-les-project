package com.chairpick.ecommerce.model.payment.strategy;

import com.chairpick.ecommerce.model.Coupon;
import com.chairpick.ecommerce.model.CreditCard;
import com.chairpick.ecommerce.model.Order;
import com.chairpick.ecommerce.utils.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class CreditCardsAndCouponsPayment implements PaymentStrategy {

    private Map<CreditCard, Double> creditCardPayments;
    private List<Coupon> coupons;

    @Override
    public List<ErrorCode> validatePayment(double orderTotalValue) {
        List<ErrorCode> errors = new ArrayList<>();

        if (creditCardPayments == null || creditCardPayments.isEmpty()) {
            errors.add(ErrorCode.CREDIT_CARD_REQUIRED);
            return errors;
        }

        if (coupons == null || coupons.isEmpty()) {
            errors.add(ErrorCode.COUPON_REQUIRED);
            return errors;
        }

        for (CreditCard creditCard : creditCardPayments.keySet()) {
            if (creditCard == null) {
                errors.add(ErrorCode.CREDIT_CARD_REQUIRED);
                break;
            }
            double value = creditCardPayments.get(creditCard);
            if (value <= 0.00) {
                errors.add(ErrorCode.INVALID_PAYMENT_VALUE_FOR_CREDIT_CARD);
                break;
            }
            if (value > orderTotalValue) {
                errors.add(ErrorCode.INVALID_PAYMENT_VALUE_FOR_CREDIT_CARD);
                break;
            }
        }

        double totalValueCreditCards = creditCardPayments.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        double totalValueCoupons = coupons.stream()
                .mapToDouble(Coupon::getValue)
                .sum();

        BigDecimal totalValueCouponDecimal = new BigDecimal(totalValueCoupons);
        BigDecimal totalValueCreditCard = new BigDecimal(totalValueCreditCards);
        BigDecimal total = new BigDecimal(0).add(totalValueCouponDecimal).add(totalValueCreditCard)
                .setScale(2, RoundingMode.HALF_DOWN);

        if (total.doubleValue() != orderTotalValue) {
            errors.add(ErrorCode.INVALID_PAYMENT_VALUE_FOR_COUPON);
        }

        return errors;

    }

    @Override
    public double getTotalValue() {
        return creditCardPayments
                .values()
                .stream()
                .reduce(0.0, Double::sum) +
                coupons.stream().mapToDouble(Coupon::getValue)
                        .sum();
    }

}
