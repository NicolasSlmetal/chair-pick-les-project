package com.chairpick.ecommerce.utils.mappers;

import com.chairpick.ecommerce.model.CreditCard;
import com.chairpick.ecommerce.model.enums.CreditCardBrand;
import com.chairpick.ecommerce.model.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;


public class CreditCardMapper extends CustomRowMapper<CreditCard> {
    public CreditCardMapper() {
        super("cre");
    }

    @Override
    public CreditCard mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer customer = Customer.builder().id(rs.getLong(getColumn("customer id"))).build();
        return CreditCard.builder()
                .id(rs.getLong(getColumn("id")))
                .number(rs.getString(getColumn("number")))
                .name(rs.getString(getColumn("holder")))
                .brand(CreditCardBrand.valueOf(rs.getString(getRelatedTableColumn("name", "cbr"))))
                .cvv(rs.getString(getColumn("cvv")))
                .customer(customer)
                .isDefault(rs.getBoolean(getColumn("default")))
                .build();
    }
}
