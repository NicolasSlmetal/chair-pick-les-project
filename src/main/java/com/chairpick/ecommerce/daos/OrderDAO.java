package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.model.Order;
import com.chairpick.ecommerce.model.enums.OrderStatus;
import com.chairpick.ecommerce.utils.query.*;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.ObjectQueryMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderDAO implements GenericDAO<Order> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectQueryMapper<Order> queryMapper;
    private final ResultSetExtractor<List<Order>> extractor;

    public OrderDAO(NamedParameterJdbcTemplate jdbcTemplate, ObjectQueryMapper<Order> queryMapper , ResultSetExtractor<List<Order>> extractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryMapper = queryMapper;
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
        String sql = """
                UPDATE tb_order
                SET ord_status = :ord_status, ord_total_amount = :ord_total_amount, ord_total_value = :ord_total_value, ord_updated_date = :ord_updated_date
                WHERE ord_id = :ord_id
                """;
        Map<String, Object> parameters = Map.of(
                "ord_status", entity.getStatus().name(),
                "ord_total_amount", entity.getTotalAmount(),
                "ord_total_value", entity.getTotalValue(),
                "ord_updated_date", entity.getUpdatedDate(),
                "ord_id", entity.getId()
        );
        jdbcTemplate.update(sql, parameters);
        return entity;
    }

    @Override
    public List<Order> findAll() {
        String sql = """
                SELECT ord_id, ord_status, ord_total_amount, ord_total_value, ord_updated_date, cus_name, cus_id FROM tb_order 
                INNER JOIN tb_customer ON ord_customer_id = cus_id
                ORDER BY ord_updated_date DESC
                """;
        Map<String, Object> parameters = Map.of();
        return jdbcTemplate.query(sql, parameters, rs -> {
            List<Order> orders = new ArrayList<>();
            Customer customer;
            Order order;
            while (rs.next()) {
                customer = Customer.builder()
                        .id(rs.getLong("cus_id"))
                        .name(rs.getString("cus_name"))
                        .build();
                order = Order.builder()
                        .id(rs.getLong("ord_id"))
                        .status(OrderStatus.valueOf(rs.getString("ord_status")))
                        .totalAmount(rs.getInt("ord_total_amount"))
                        .totalValue(rs.getDouble("ord_total_value"))
                        .updatedDate(rs.getDate("ord_updated_date").toLocalDate())
                        .customer(customer)
                        .build();
                orders.add(order);
            }
            return orders;
        });
    }

    @Override
    public Optional<Order> findById(Long id) {
        String sql = """
                SELECT ord_id, ord_status, ord_total_amount, ord_total_value, ord_updated_date, ord_customer_id  FROM tb_order
                WHERE ord_id = :ord_id
                """;
        Map<String, Object> parameters = Map.of("ord_id", id);

        return jdbcTemplate.query(sql, parameters, rs -> {
            if (rs.next()) {
                Order order = Order.builder()
                        .id(rs.getLong("ord_id"))
                        .status(OrderStatus.valueOf(rs.getString("ord_status")))
                        .totalAmount(rs.getInt("ord_total_amount"))
                        .totalValue(rs.getDouble("ord_total_value"))
                        .customer(Customer
                                .builder()
                                .id(rs.getLong("ord_customer_id"))
                                .build())
                        .updatedDate(rs.getDate("ord_updated_date").toLocalDate())
                        .build();
                return Optional.of(order);
            }
            return Optional.empty();
        });
    }

    @Override
    public List<Order> findBy(Map<String, String> parameters) {
        QueryResult sql = queryMapper.parseParameters(parameters);

        return jdbcTemplate.query(sql.query(), sql.parameters(), extractor);
    }

    @Override
    public void delete(Long id) {

    }
}
