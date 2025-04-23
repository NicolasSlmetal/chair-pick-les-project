package com.chairpick.ecommerce.io.output;

import com.chairpick.ecommerce.model.CreditCard;
import lombok.Getter;

import java.util.Map;

@Getter
public class CreditCardPaymentDTO extends PaymentDTO {

    private final Map<CreditCard, Double> creditCards;

    public CreditCardPaymentDTO(Long orderId, Map<CreditCard, Double> creditCards) {
        super(orderId, PaymentType.CREDIT_CARD);
        this.creditCards = creditCards;
    }
}
