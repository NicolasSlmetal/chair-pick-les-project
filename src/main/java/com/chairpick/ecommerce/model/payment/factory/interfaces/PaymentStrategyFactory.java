package com.chairpick.ecommerce.model.payment.factory.interfaces;

import com.chairpick.ecommerce.model.payment.strategy.PaymentStrategy;

import java.util.List;
import java.util.Map;

public interface PaymentStrategyFactory {

    PaymentStrategy createPayment(List<Object> paymentMethods, Map<Long, Double> paymentValues);
}
