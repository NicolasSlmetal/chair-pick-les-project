package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.OrderPaymentDAO;
import com.chairpick.ecommerce.io.output.CouponPaymentDTO;
import com.chairpick.ecommerce.io.output.PaymentDTO;
import com.chairpick.ecommerce.model.Coupon;
import com.chairpick.ecommerce.model.enums.CouponType;
import com.chairpick.ecommerce.model.payment.strategy.CouponsPayment;
import com.chairpick.ecommerce.model.payment.strategy.CreditCardsAndCouponsPayment;
import com.chairpick.ecommerce.model.payment.strategy.PaymentStrategy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CouponPaymentDAO implements OrderPaymentDAO {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Map<Long, PaymentDTO> paymentCache = new HashMap<>();

    public CouponPaymentDAO(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public PaymentStrategy save(PaymentStrategy payment, Long orderId) {
        String sql = """
                INSERT INTO tb_order_coupon (ocp_order_id, ocp_coupon_id, ocp_paid_value)
                VALUES (:orderId, :couponId, :paidValue) RETURNING ocp_id
                """;
        List<Coupon> coupons;

        if (payment instanceof CouponsPayment couponPayment) {
            coupons = couponPayment.getCouponList();
        } else if (payment instanceof CreditCardsAndCouponsPayment creditCardsAndCouponsPayment) {
            coupons = creditCardsAndCouponsPayment.getCoupons();
        } else {
            coupons = List.of();
        }

        for (Coupon coupon : coupons) {
            Map<String, Object> parameters = Map.of(
                    "orderId", orderId,
                    "couponId", coupon.getId(),
                    "paidValue", coupon.getValue()
            );
            jdbcTemplate.queryForObject(sql, parameters, Long.class);
        }
        return payment;
    }

    @Override
    public PaymentDTO findByOrderId(Long orderId) {
        String sql = """
                SELECT ocp_order_id, ocp_coupon_id, ocp_paid_value, cpn_type, cpn_value FROM tb_order_coupon ocp
                INNER JOIN tb_coupon cpn ON ocp.ocp_coupon_id = cpn.cpn_id
                WHERE ocp_order_id = :orderId
                """;
        Map<String, Object> parameters = Map.of("orderId", orderId);
        return paymentCache.computeIfAbsent(orderId, k -> {

            List<Coupon> coupons = jdbcTemplate.query(sql, parameters, rs -> {
                List<Coupon> couponsInTable = new ArrayList<>();
                while (rs.next()) {
                    Coupon coupon = Coupon.builder()
                            .id(rs.getLong("ocp_coupon_id"))
                            .type(CouponType.valueOf(rs.getString("cpn_type")))
                            .value(rs.getDouble("cpn_value"))
                            .build();
                    couponsInTable.add(coupon);
                }
                return couponsInTable;
            });

            return new CouponPaymentDTO(orderId, coupons);
        });
    }

    @Override
    public void delete(Long orderId) {
        String sql = "DELETE FROM tb_order_coupon WHERE ocp_order_id = :orderId";
        Map<String, Object> parameters = Map.of("orderId", orderId);
        jdbcTemplate.update(sql, parameters);
    }
}
