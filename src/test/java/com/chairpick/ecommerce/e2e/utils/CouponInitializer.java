package com.chairpick.ecommerce.e2e.utils;

import com.chairpick.ecommerce.model.enums.CouponType;

import java.util.Map;


public class CouponInitializer {

    private final DatabaseSeeder seeder;
    public CouponInitializer(DatabaseSeeder seeder) {
        this.seeder = seeder;
    }


    public Long insertCouponForCustomerWithValue(Long customerId, Double value, CouponType type) {
        String query = """
                INSERT INTO tb_coupon (
                cpn_customer_id,
                cpn_type,
                cpn_value
                )
                VALUES (
                :customerId,
                :type,
                :value
                ) RETURNING cpn_id;
                """;
        Map<String, Object> params =  Map.of(
                "customerId", customerId,
                "type", type.name(),
                "value", value
        );
        return seeder.executeReturningId(query, params);
    }
}
