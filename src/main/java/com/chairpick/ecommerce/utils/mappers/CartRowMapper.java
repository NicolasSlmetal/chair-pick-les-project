package com.chairpick.ecommerce.utils.mappers;

import com.chairpick.ecommerce.model.Cart;
import com.chairpick.ecommerce.model.enums.CartItemStatus;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.Item;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CartRowMapper extends CustomRowMapper<Cart>{
    public CartRowMapper() {
        super("car");
    }

    @Override
    public Cart mapRow(ResultSet rs, int rowNum) throws SQLException {
        Item item = Item.builder()
                .id(rs.getLong(getColumn("item_id")))
                .amount(rs.getInt(getRelatedTableColumn("amount", "itm")))
                .reservedAmount(rs.getInt(getRelatedTableColumn("reserved", "itm")))
                .unitCost(rs.getDouble(getRelatedTableColumn("unit_cost", "itm")))
                .version(rs.getInt(getRelatedTableColumn("version", "itm")))
                .entryDate(rs.getDate(getRelatedTableColumn("entry_date", "itm")).toLocalDate())
                .chair(Chair
                        .builder()
                        .weight(rs.getDouble(getRelatedTableColumn("weight", "chr")))
                        .height(rs.getDouble(getRelatedTableColumn("height", "chr")))
                        .length(rs.getDouble(getRelatedTableColumn("length", "chr")))
                        .width(rs.getDouble(getRelatedTableColumn("width", "chr")))
                        .sellPrice(rs.getDouble(getRelatedTableColumn("sell_price", "chr")))
                        .name(rs.getString(getRelatedTableColumn("name", "chr")))
                        .id(rs.getLong(getRelatedTableColumn("chair_id", "itm"))).build())
                .build();

        return Cart.builder()
                .id(rs.getLong(getColumn("id")))
                .item(item)
                .limit(rs.getInt(getColumn("item_limit")))
                .status(CartItemStatus.valueOf(rs.getString(getColumn("item_status"))))
                .price(rs.getDouble(getColumn("item_price")))
                .amount(rs.getInt(getColumn("item_amount")))
                .entryDate(rs.getTimestamp(getColumn("item_entry_datetime")).toLocalDateTime())
                .build();
    }
}
