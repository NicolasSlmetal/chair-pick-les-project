package com.chairpick.ecommerce.utils.mappers;

import com.chairpick.ecommerce.model.Item;
import com.chairpick.ecommerce.model.Order;
import com.chairpick.ecommerce.model.OrderItem;
import com.chairpick.ecommerce.model.Swap;
import com.chairpick.ecommerce.model.enums.OrderStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SwapRowMapper extends CustomRowMapper<Swap> {
    public SwapRowMapper() {
        super("its");
    }

    @Override
    public Swap mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order order = Order
                .builder()
                .id(rs.getLong(getRelatedTableColumn("order_id", "ori")))
                .build();
        OrderItem orderItem = OrderItem
                .builder()
                .order(order)
                .amount(rs.getInt(getRelatedTableColumn("amount", "ori")))
                .value(rs.getDouble(getRelatedTableColumn("sell_price", "ori")))
                .freightValue(rs.getDouble(getRelatedTableColumn("freight_tax", "ori")))
                .item(Item
                        .builder()
                        .id(rs.getLong(getRelatedTableColumn("item_id", "ori")))
                        .entryDate(rs.getDate(getRelatedTableColumn("entry_date", "itm")).toLocalDate())
                        .unitCost(rs.getDouble(getRelatedTableColumn("unit_cost", "itm")))
                        .amount(rs.getInt(getRelatedTableColumn("amount", "itm")))
                        .reservedAmount(rs.getInt(getRelatedTableColumn("reserved", "itm")))
                        .version(rs.getInt(getRelatedTableColumn("version", "itm")))
                        .build())
                .status(OrderStatus.valueOf(rs.getString(getRelatedTableColumn("status", "ori"))))
                .id(rs.getLong(getColumn("order_item_id")))
                .status(OrderStatus.valueOf(rs.getString(getRelatedTableColumn("status", "ori"))))
                .build();


        return Swap
                .builder()
                .id(rs.getLong(getColumn("id")))
                .amount(rs.getInt(getColumn("amount")))
                .orderItem(orderItem)
                .value(rs.getDouble(getColumn("total_value")))
                .status(OrderStatus.valueOf(rs.getString(getColumn("status"))))
                .build();
    }
}
