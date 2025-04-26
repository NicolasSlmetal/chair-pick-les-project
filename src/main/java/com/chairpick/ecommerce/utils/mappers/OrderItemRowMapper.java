package com.chairpick.ecommerce.utils.mappers;

import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.Item;
import com.chairpick.ecommerce.model.Order;
import com.chairpick.ecommerce.model.OrderItem;
import com.chairpick.ecommerce.model.enums.OrderStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemRowMapper extends CustomRowMapper<OrderItem> {
    public OrderItemRowMapper() {
        super("ori");
    }

    @Override
    public OrderItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        Chair chair = Chair
                .builder()
                .name(rs.getString(getRelatedTableColumn("name", "chr")))
                .build();
        Item item = Item
                .builder()
                .id(rs.getLong(getColumn("item_id")))
                .chair(chair)
                .entryDate(rs.getDate(getRelatedTableColumn("entry_date", "itm")).toLocalDate())
                .unitCost(rs.getDouble(getRelatedTableColumn("unit_cost", "itm")))
                .amount(rs.getInt(getRelatedTableColumn("amount", "itm")))
                .version(rs.getInt(getRelatedTableColumn("version", "itm")))
                .reservedAmount(rs.getInt(getRelatedTableColumn("reserved", "itm")))
                .build();
        return OrderItem
                .builder()
                .id(rs.getLong(getColumn("id")))
                .item(item)
                .status(OrderStatus.valueOf(rs.getString(getColumn("status"))))
                .amount(rs.getInt(getColumn("amount")))
                .value(rs.getDouble(getColumn("sell_price")))
                .freightValue(rs.getDouble(getColumn("freight_tax")))
                .order(Order
                        .builder()
                        .id(rs.getLong(getColumn("order_id")))
                        .build())
                .build();
    }
}
