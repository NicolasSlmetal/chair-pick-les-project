package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.WriteOnlyDAO;
import com.chairpick.ecommerce.model.OrderItem;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;

public class OrderItemDAO implements WriteOnlyDAO<OrderItem> {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public OrderItemDAO(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public OrderItem insert(OrderItem entity) {
        String sql = """
                INSERT INTO tb_order_item (ori_status, ori_amount, ori_sell_price, ori_freight_tax, ori_order_id, ori_item_id)
                VALUES (:ori_status, :ori_amount, :ori_sell_price, :ori_freight_tax, :ori_order_id, :ori_item_id) RETURNING ori_id
                """;
        Map<String, Object> parameters = Map.of(
                "ori_status", entity.getStatus().name(),
                "ori_amount", entity.getAmount(),
                "ori_sell_price", entity.getValue(),
                "ori_freight_tax", entity.getFreightValue(),
                "ori_order_id", entity.getOrder().getId(),
                "ori_item_id", entity.getItem().getId()
        );

        Long id = jdbcTemplate.queryForObject(sql, parameters, Long.class);
        entity.setId(id);
        return entity;
    }

    @Override
    public OrderItem update(OrderItem entity) {
        String sql = """
                UPDATE tb_order_item
                SET ori_status = :ori_status, ori_amount = :ori_amount, ori_value = :ori_value, ori_freight_tax = :ori_freight_tax
                WHERE ori_id = :ori_id
                """;
        Map<String, Object> parameters = Map.of(
                "ori_status", entity.getStatus().name(),
                "ori_amount", entity.getAmount(),
                "ori_value", entity.getValue(),
                "ori_freight_tax", entity.getFreightValue(),
                "ori_id", entity.getId()
        );
        jdbcTemplate.update(sql, parameters);
        return entity;
    }
}
