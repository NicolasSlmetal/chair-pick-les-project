package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.OrderPaymentDAO;
import com.chairpick.ecommerce.io.output.CreditCardPaymentDTO;
import com.chairpick.ecommerce.io.output.PaymentDTO;
import com.chairpick.ecommerce.model.CreditCard;
import com.chairpick.ecommerce.model.payment.strategy.CreditCardsAndCouponsPayment;
import com.chairpick.ecommerce.model.payment.strategy.CreditCardsPayment;
import com.chairpick.ecommerce.model.payment.strategy.PaymentStrategy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.Map;

public class CreditCardPaymentDAO implements OrderPaymentDAO {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Map<Long, PaymentDTO> paymentCache = new HashMap<>();

    public CreditCardPaymentDAO(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PaymentStrategy save(PaymentStrategy payment, Long orderId) {
        Map<CreditCard, Double> creditCardsPayment;

        if (payment instanceof CreditCardsPayment c) {
            creditCardsPayment = c.getCreditCardPayments();
        } else if (payment instanceof CreditCardsAndCouponsPayment cc) {
            creditCardsPayment = cc.getCreditCardPayments();
        } else {
            creditCardsPayment = new HashMap<>();
        }
        String sql = "INSERT INTO tb_order_credit_card (occ_order_id, occ_credit_card_id, occ_paid_value) " +
                "VALUES (:order_id, :credit_card_id, :paid_value) RETURNING occ_id";
        for (Map.Entry<CreditCard, Double> entry : creditCardsPayment.entrySet()) {
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
    public PaymentDTO findByOrderId(Long orderId) {

        String sql = """
                SELECT occ_order_id, occ_credit_card_id, occ_paid_value, cre_holder FROM tb_order_credit_card occ
                INNER JOIN tb_credit_card cc ON occ.occ_credit_card_id = cc.cre_id
                WHERE occ_order_id = :order_id
                """;
        Map<String, Object> parameters = Map.of("order_id", orderId);
        return paymentCache.computeIfAbsent(orderId, k -> {
            Map<CreditCard, Double> cardValueMap = new HashMap<>();
            jdbcTemplate.query(sql, parameters, rs -> {
                Long creditCardId = rs.getLong("occ_credit_card_id");
                String holder = rs.getString("cre_holder");
                Double paidValue = rs.getDouble("occ_paid_value");

                CreditCard creditCard = CreditCard
                        .builder()
                        .id(creditCardId)
                        .name(holder)
                        .build();

                cardValueMap.put(creditCard, paidValue);
            });
            return new CreditCardPaymentDTO(orderId, cardValueMap);
        });
    }

    @Override
    public void delete(Long orderId) {
        String sql = "DELETE FROM tb_order_credit_card WHERE occ_order_id = :order_id";
        Map<String, Object> parameters = Map.of("order_id", orderId);
        jdbcTemplate.update(sql, parameters);
    }
}
