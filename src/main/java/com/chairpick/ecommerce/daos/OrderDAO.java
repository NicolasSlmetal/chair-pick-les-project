package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.Order;
import com.chairpick.ecommerce.utils.query.*;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderDAO implements GenericDAO<Order> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ResultSetExtractor<List<Order>> extractor;

    public OrderDAO(NamedParameterJdbcTemplate jdbcTemplate, ResultSetExtractor<List<Order>> extractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.extractor = extractor;
    }

    @Override
    public Order save(Order entity) {
        String sql = """
                INSERT INTO tb_order (ord_status, ord_total_amount, ord_total_value, ord_customer_id, ord_delivery_address_id, ord_billing_address_id, ord_created_date, ord_updated_date)
                VALUES (:ord_status, :ord_total_amount, :ord_total_value, :ord_customer_id, :ord_delivery_address_id, :ord_billing_address_id, :ord_created_date, :ord_updated_date) RETURNING ord_id
                """;
        Map<String, Object> parameters = Map.of(
                "ord_status", entity.getStatus().name(),
                "ord_total_amount", entity.getTotalAmount(),
                "ord_total_value", entity.getTotalValue(),
                "ord_customer_id", entity.getCustomer().getId(),
                "ord_delivery_address_id", entity.getDeliveryAddress().getId(),
                "ord_billing_address_id", entity.getBillingAddress().getId(),
                "ord_created_date", entity.getCreatedDate(),
                "ord_updated_date", entity.getUpdatedDate()
        );

        Long id = jdbcTemplate.queryForObject(sql, parameters, Long.class);
        entity.setId(id);
        return entity;
    }

    @Override
    public Order update(Order entity) {
        return null;
    }

    @Override
    public List<Order> findAll() {
        return List.of();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Order> findBy(Map<String, String> parameters) {
        QueryResult sql = parseParameters(parameters);

        return jdbcTemplate.query(sql.query(), sql.parameters(), extractor);
    }

    public QueryResult parseParameters(Map<String, String> parameters) {
        SelectTable selectTable = SqlQueryBuilder
                .create()
                .selectingAllFromTable("tb_order");
        selectTable.join("tb_order_item")
                .innerJoinOn("ord_id", "ori_order_id")
                .joinDifferentTables("tb_order_item", "tb_item")
                .innerJoinOn("ori_item_id", "itm_id")
                .joinDifferentTables("tb_item", "tb_chair")
                .innerJoinOn("itm_chair_id", "chr_id");
        if (parameters.isEmpty()) {
            return selectTable.endingOptions()
                    .orderByDescending("ord_id")
                    .build();
        }
        Where where = selectTable.where();
        int size = parameters.size();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String key = "ord_"+entry.getKey();
            String value = entry.getValue();

            if (key.equals("ord_status")) {
                where.equalString("ord_status", value);
            }

            if (key.equals("ord_customer_id")) {
                where.equals("ord_customer_id", value);
            }

            if (--size > 0) {
                where = where.and();
            }
        }
        EndingOptions endingOptions = where.end().endingOptions().orderByDescending("ord_id");
        return endingOptions.build();
    }

    @Override
    public void delete(Long id) {

    }
}
