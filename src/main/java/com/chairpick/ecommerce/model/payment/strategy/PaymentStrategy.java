package com.chairpick.ecommerce.model.payment.strategy;

import com.chairpick.ecommerce.utils.ErrorCode;

import java.util.List;

public interface PaymentStrategy {

    List<ErrorCode> validatePayment(double orderTotalValue);
}
