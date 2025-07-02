package com.chairpick.ecommerce.io.output;

import lombok.Getter;

import java.util.Map;
import java.util.Objects;

@Getter
public class CompositePaymentDTO extends PaymentDTO {
    private final Map<PaymentType, PaymentDTO> paymentsByType;

    public CompositePaymentDTO(Long orderId, Map<PaymentType, PaymentDTO> paymentsByType) {
        super(orderId, PaymentType.COMPOSITE);

        this.paymentsByType = paymentsByType;
        this.setTotalValue(paymentsByType.values()
                .stream()
                .map(PaymentDTO::getTotalValue)
                .reduce(0.0, Double::sum));
    }

    @Override
    public String toString() {
        return "CompositePaymentDTO{" +
                "paymentsByType=" + paymentsByType +
                '}';
    }
}
