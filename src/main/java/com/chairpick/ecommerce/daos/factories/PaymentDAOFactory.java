package com.chairpick.ecommerce.daos.factories;

import com.chairpick.ecommerce.daos.CouponPaymentDAO;
import com.chairpick.ecommerce.daos.CreditCardAndCouponPaymentDAO;
import com.chairpick.ecommerce.daos.CreditCardPaymentDAO;
import com.chairpick.ecommerce.daos.interfaces.OrderPaymentDAO;
import com.chairpick.ecommerce.model.payment.strategy.CouponsPayment;
import com.chairpick.ecommerce.model.payment.strategy.CreditCardsAndCouponsPayment;
import com.chairpick.ecommerce.model.payment.strategy.CreditCardsPayment;
import com.chairpick.ecommerce.model.payment.strategy.PaymentStrategy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentDAOFactory {

    Map<Class<? extends PaymentStrategy>, OrderPaymentDAO> orderPaymentDAOMap;

    public PaymentDAOFactory(NamedParameterJdbcTemplate jdbcTemplate) {
        orderPaymentDAOMap = new HashMap<>();
        orderPaymentDAOMap.put(CreditCardsPayment.class, new CreditCardPaymentDAO(jdbcTemplate));
        orderPaymentDAOMap.put(CouponsPayment.class, new CouponPaymentDAO(jdbcTemplate));
        orderPaymentDAOMap.put(CreditCardsAndCouponsPayment.class, new CreditCardAndCouponPaymentDAO(jdbcTemplate));
    }

    public OrderPaymentDAO getOrderPaymentDAO(PaymentStrategy payment) {
        return orderPaymentDAOMap.get(payment.getClass());
    }
}
