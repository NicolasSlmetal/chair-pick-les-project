package com.chairpick.ecommerce.daos.registry;

import com.chairpick.ecommerce.daos.CouponPaymentDAO;
import com.chairpick.ecommerce.daos.CreditCardPaymentDAO;
import com.chairpick.ecommerce.daos.CompositePaymentDAO;
import com.chairpick.ecommerce.daos.interfaces.OrderPaymentDAO;
import com.chairpick.ecommerce.model.payment.strategy.CouponsPayment;
import com.chairpick.ecommerce.model.payment.strategy.CreditCardsAndCouponsPayment;
import com.chairpick.ecommerce.model.payment.strategy.CreditCardsPayment;
import com.chairpick.ecommerce.model.payment.strategy.PaymentStrategy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PaymentDAORegistry {

    Map<Class<? extends PaymentStrategy>, OrderPaymentDAO> orderPaymentDAOMap;

    public PaymentDAORegistry(NamedParameterJdbcTemplate jdbcTemplate) {
        orderPaymentDAOMap = new HashMap<>();
        CreditCardPaymentDAO creditCardPaymentDAO = new CreditCardPaymentDAO(jdbcTemplate);
        CouponPaymentDAO couponPaymentDAO = new CouponPaymentDAO(jdbcTemplate);

        orderPaymentDAOMap.put(CreditCardsPayment.class, creditCardPaymentDAO);
        orderPaymentDAOMap.put(CouponsPayment.class, couponPaymentDAO);
        orderPaymentDAOMap
                .put(CreditCardsAndCouponsPayment.class, new CompositePaymentDAO(List.of(creditCardPaymentDAO, couponPaymentDAO)));
    }

    public OrderPaymentDAO getOrderPaymentDAO(PaymentStrategy payment) {
        return orderPaymentDAOMap.get(payment.getClass());
    }

    public final Optional<CompositePaymentDAO> getCompositeDAO() {

        return orderPaymentDAOMap
                .values()
                .stream()
                .filter(dao -> dao instanceof CompositePaymentDAO)
                .map(c -> (CompositePaymentDAO) c)
                .findFirst();
    }
}
