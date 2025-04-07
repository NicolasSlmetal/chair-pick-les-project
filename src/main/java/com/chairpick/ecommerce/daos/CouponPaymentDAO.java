package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.OrderPaymentDAO;
import com.chairpick.ecommerce.model.payment.strategy.PaymentStrategy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class CouponPaymentDAO implements OrderPaymentDAO {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CouponPaymentDAO(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public PaymentStrategy save(PaymentStrategy payment, Long orderId) {
        return null;
    }

    @Override
    public PaymentStrategy findByOrderId(Long orderId) {
        return null;
    }
}
