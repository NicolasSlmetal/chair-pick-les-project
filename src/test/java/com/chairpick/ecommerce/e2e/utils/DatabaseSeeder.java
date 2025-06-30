package com.chairpick.ecommerce.e2e.utils;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DatabaseSeeder {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DatabaseSeeder(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void execute(String query, Map<String, Object> parameters) {
        jdbcTemplate.update(query, parameters);
    }

    public Long executeReturningId(String query, Map<String, Object> parameters) {
        return jdbcTemplate.queryForObject(query, parameters, Long.class);
    }

    public void truncateAllTables() {
        String truncateQuery = "TRUNCATE TABLE tb_user, tb_customer, tb_address, tb_credit_card, tb_order, tb_chair, tb_coupon, tb_cart, tb_category CASCADE;";
        jdbcTemplate.update(truncateQuery, Map.of());
        String resetSequenceQuery = "ALTER SEQUENCE tb_user_usr_id_seq RESTART WITH 1; " +
                "ALTER SEQUENCE tb_customer_cus_id_seq RESTART WITH 1; " +
                "ALTER SEQUENCE tb_address_add_id_seq RESTART WITH 1; " +
                "ALTER SEQUENCE tb_credit_card_cre_id_seq RESTART WITH 1; " +
                "ALTER SEQUENCE tb_order_ord_id_seq RESTART WITH 1;" +
                "ALTER SEQUENCE tb_chair_chr_id_seq RESTART WITH 1;"
                + "ALTER SEQUENCE tb_coupon_cpn_id_seq RESTART WITH 1;" +
                "ALTER SEQUENCE tb_cart_car_id_seq RESTART WITH 1;" +
                "ALTER SEQUENCE tb_category_cat_id_seq RESTART WITH 1;";
        jdbcTemplate.update(resetSequenceQuery, Map.of());
    }

}
