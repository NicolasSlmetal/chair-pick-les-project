package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.Order;
import com.chairpick.ecommerce.model.OrderItem;
import com.chairpick.ecommerce.model.enums.OrderStatus;
import com.chairpick.ecommerce.utils.pagination.PageOptions;
import com.chairpick.ecommerce.utils.query.QueryResult;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.GeneralObjectQueryMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderItemDAO implements GenericDAO<OrderItem> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final GeneralObjectQueryMapper<OrderItem> queryMapper;
    private final RowMapper<OrderItem> rowMapper;

    public OrderItemDAO(NamedParameterJdbcTemplate jdbcTemplate, GeneralObjectQueryMapper<OrderItem> queryMapper, RowMapper<OrderItem> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryMapper = queryMapper;
        this.rowMapper = rowMapper;
    }

    @Override
    public OrderItem save(OrderItem entity) {
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
                SET ori_status = :ori_status, ori_amount = :ori_amount, ori_sell_price = :ori_sell_price, ori_freight_tax = :ori_freight_tax
                WHERE ori_id = :ori_id
                """;
        Map<String, Object> parameters = Map.of(
                "ori_status", entity.getStatus().name(),
                "ori_amount", entity.getAmount(),
                "ori_sell_price", entity.getValue(),
                "ori_freight_tax", entity.getFreightValue(),
                "ori_id", entity.getId()
        );
        jdbcTemplate.update(sql, parameters);
        return entity;
    }

    @Override
    public List<OrderItem> findAll() {
        return List.of();
    }

    @Override
    public Optional<OrderItem> findById(Long id) {
        String sql = """
                SELECT * FROM tb_order_item WHERE ori_id = :id
                """;
        Map<String, Object> parameters = Map.of("id", id);
        List<OrderItem> orderItems = jdbcTemplate.query(sql, parameters, (rs) -> {
            List<OrderItem> orderItemsRs = new ArrayList<>();
            while (rs.next()) {
            OrderItem item = OrderItem.builder()
                    .id(rs.getLong("ori_id"))
                    .order(Order.builder()
                            .id(rs.getLong("ori_order_id"))
                            .build())
                    .status(OrderStatus.valueOf(rs.getString("ori_status")))
                    .amount(rs.getInt("ori_amount"))
                    .value(rs.getDouble("ori_sell_price"))
                    .freightValue(rs.getDouble("ori_freight_tax"))
                    .build();
                orderItemsRs.add(item);
            }
            return orderItemsRs;
        });
        return orderItems == null || orderItems.isEmpty() ? Optional.empty() : Optional.of(orderItems.getFirst());
    }

    @Override
    public List<OrderItem> findBy(Map<String, String> parameters) {
        QueryResult sql;
        if (parameters.containsKey("page") && parameters.containsKey("limit")) {
            PageOptions pageOptions = PageOptions.builder()
                    .size(Integer.parseInt(parameters.get("limit")))
                    .page(Integer.parseInt(parameters.get("page")))
                    .build();

            sql = queryMapper.parseParameters(parameters, pageOptions);
        } else {
            sql = queryMapper.parseParameters(parameters);
        }

        return jdbcTemplate.query(sql.query(), sql.parameters(), rowMapper);
    }

    @Override
    public void delete(Long id) {
        String sql = """
                DELETE FROM tb_order_item WHERE ori_id = :id
                """;
        Map<String, Object> parameters = Map.of("id", id);
        jdbcTemplate.update(sql, parameters);
    }
}
