package com.chairpick.ecommerce.utils.mappers;

import com.chairpick.ecommerce.model.Address;
import com.chairpick.ecommerce.model.enums.StreetType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressRowMapper extends CustomRowMapper<Address> {

    public AddressRowMapper() {
        super("add");
    }

    @Override
    public Address mapRow(ResultSet rs, int rowNum) throws SQLException {
        StreetType streetType = StreetType.valueOf(rs.getString(getColumn("street_type")));

        Address address = Address.builder()
                .name(rs.getString(getColumn("name")))
                .streetType(streetType)
                .cep(rs.getString(getColumn("cep")))
                .city(rs.getString(getColumn("city")))
                .country(rs.getString(getColumn("country")))
                .isDefault(rs.getBoolean(getColumn("default")))
                .neighborhood(rs.getString(getColumn("neighborhood")))
                .number(rs.getInt(getColumn("number")))
                .observations(rs.getString(getColumn("observation")))
                .state(rs.getString(getColumn("state")))
                .street(rs.getString(getColumn("street")))
                .build();
        address.setId(rs.getLong(getColumn("id")));
        return address;
    }
}
