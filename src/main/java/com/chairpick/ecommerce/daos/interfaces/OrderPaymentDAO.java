package com.chairpick.ecommerce.daos.interfaces;

import com.chairpick.ecommerce.model.payment.strategy.PaymentStrategy;

public interface OrderPaymentDAO {

    PaymentStrategy save(PaymentStrategy payment, Long orderId);
    PaymentStrategy findByOrderId(Long orderId);

}
