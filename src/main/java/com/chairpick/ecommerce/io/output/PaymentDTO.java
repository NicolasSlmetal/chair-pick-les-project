package com.chairpick.ecommerce.io.output;

import com.chairpick.ecommerce.model.enums.OrderStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public abstract class PaymentDTO {

    private final Long orderId;
    private final PaymentType paymentType;
    private OrderStatus status;
    private Double totalValue;
}
