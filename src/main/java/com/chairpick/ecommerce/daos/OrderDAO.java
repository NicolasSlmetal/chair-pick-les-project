package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.Order;
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
        return List.of();
    }

    @Override
    public void delete(Long id) {

    }
}
