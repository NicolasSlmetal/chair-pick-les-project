package com.chairpick.ecommerce.io.output;

import lombok.Getter;

import java.util.Map;

@Getter
public class CompositePaymentDTO extends PaymentDTO {
    private final Map<PaymentType, PaymentDTO> paymentsByType;

    public CompositePaymentDTO(Long orderId, Map<PaymentType, PaymentDTO> paymentsByType) {
        super(orderId, PaymentType.COMPOSITE);
        this.paymentsByType = paymentsByType;
    }

    @Override
    public String toString() {
        return "CompositePaymentDTO{" +
                "paymentsByType=" + paymentsByType +
                '}';
    }
}
