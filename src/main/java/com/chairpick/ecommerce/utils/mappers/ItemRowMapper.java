package com.chairpick.ecommerce.utils.mappers;

import com.chairpick.ecommerce.model.Item;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemRowMapper extends CustomRowMapper<Item> {
    public ItemRowMapper() {
        super("itm");
    }

    @Override
    public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Item
                .builder()
                .id(rs.getLong(getColumn("id")))
                .amount(rs.getInt(getColumn("amount")))
                .entryDate(rs.getDate(getColumn("entry_date")).toLocalDate())
                .reservedAmount(rs.getInt(getColumn("reserved")))
                .unitCost(rs.getDouble(getColumn("unit_cost")))
                .version(rs.getInt(getColumn("version")))
                .build();
    }
}
