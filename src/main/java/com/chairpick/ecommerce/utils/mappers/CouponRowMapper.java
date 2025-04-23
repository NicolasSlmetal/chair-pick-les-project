package com.chairpick.ecommerce.utils.mappers;

import com.chairpick.ecommerce.model.Coupon;
import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.model.enums.CouponType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CouponRowMapper extends CustomRowMapper<Coupon> {

    public CouponRowMapper() {
        super("cpn");
    }

    @Override
    public Coupon mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Coupon
                .builder()
                .id(rs.getLong(getColumn("id")))
                .customer(Customer.builder().id(rs.getLong(getColumn("customer_id"))).build())
                .value(rs.getDouble(getColumn("value")))
                .type(CouponType.valueOf(rs.getString(getColumn("type"))))
                .build();
    }
}
