package com.chairpick.ecommerce.utils.mappers;

import com.chairpick.ecommerce.model.*;
import com.chairpick.ecommerce.model.enums.OrderStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderRowMapper extends CustomRowMapper<Order> implements ResultSetExtractor<List<Order>>
{
    public OrderRowMapper() {
        super("ord");
    }

    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {

        return null;
    }

    @Override
    public List<Order> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Set<Order> orders = new HashSet<>();
        while (rs.next()) {
            Order order = Order.builder()
                    .id(rs.getLong(getColumn("id")))
                    .totalAmount(rs.getInt(getColumn("total_amount")))
                    .createdDate(rs.getDate(getColumn("created_date")).toLocalDate())
                    .updatedDate(rs.getDate(getColumn("updated_date")).toLocalDate())
                    .customer(Customer
                            .builder()
                            .id(rs.getLong(getColumn("customer_id")))
                            .build())
                    .items(new ArrayList<>())
                    .status(OrderStatus.valueOf(rs.getString(getColumn("status"))))
                    .totalValue(rs.getDouble(getColumn("total_value")))
                    .build();

            OrderItem orderItem = OrderItem.builder()
                    .id(rs.getLong(getRelatedTableColumn("item_id", "ori")))
                    .status(OrderStatus.valueOf(rs.getString(getRelatedTableColumn("status", "ori"))))
                    .value(rs.getDouble(getRelatedTableColumn("sell_price", "ori")))
                    .amount(rs.getInt(getRelatedTableColumn("amount", "ori")))
                    .freightValue(rs.getDouble(getRelatedTableColumn("freight_tax", "ori")))
                    .item(Item
                            .builder()
                            .id(rs.getLong(getRelatedTableColumn("id", "ori")))
                            .chair(Chair
                                    .builder()
                                    .id(rs.getLong(getRelatedTableColumn("id","chr")))
                                    .name(rs.getString(getRelatedTableColumn("name", "chr")))
                                    .build())
                            .build())
                    .build();

            order.setId(rs.getLong(getColumn("id")));
            order.getItems().add(orderItem);
            if (orders.contains(order)) {
                orders.stream()
                        .filter(o -> o.equals(order))
                        .forEach(o -> o.getItems().add(orderItem));
                continue;
            }
            orders.add(order);
        }

        return orders.stream().toList();
    }
}
