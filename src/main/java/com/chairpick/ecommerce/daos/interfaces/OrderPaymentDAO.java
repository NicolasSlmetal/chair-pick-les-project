package com.chairpick.ecommerce.daos.interfaces;

import com.chairpick.ecommerce.io.output.PaymentDTO;
import com.chairpick.ecommerce.model.payment.strategy.PaymentStrategy;

public interface OrderPaymentDAO {

    PaymentStrategy save(PaymentStrategy payment, Long orderId);
    PaymentDTO findByOrderId(Long orderId);
    void delete(Long orderId);

}
