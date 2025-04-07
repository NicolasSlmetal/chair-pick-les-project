package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.OrderPaymentDAO;
import com.chairpick.ecommerce.model.CreditCard;
import com.chairpick.ecommerce.model.payment.strategy.CreditCardsPayment;
import com.chairpick.ecommerce.model.payment.strategy.PaymentStrategy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;
import java.util.Objects;


public class CreditCardPaymentDAO implements OrderPaymentDAO {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CreditCardPaymentDAO(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PaymentStrategy save(PaymentStrategy payment, Long orderId) {
        CreditCardsPayment creditCardsPayment = (CreditCardsPayment) payment;
        String sql = "INSERT INTO tb_order_credit_card (occ_order_id, occ_credit_card_id, occ_paid_value) " +
                "VALUES (:order_id, :credit_card_id, :paid_value) RETURNING occ_id";
        for (Map.Entry<CreditCard, Double> entry : creditCardsPayment.getCreditCardPayments().entrySet()) {
            Map<String, Object> parameters = Map.of(
                    "order_id", orderId,
                    "credit_card_id", entry.getKey().getId(),
                    "paid_value", entry.getValue()
            );

            jdbcTemplate.queryForObject(sql, parameters, Long.class);
        }
        return payment;
    }

    @Override
    public PaymentStrategy findByOrderId(Long orderId) {
        return null;
    }
}
