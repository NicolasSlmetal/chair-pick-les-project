package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.PaginatedWithProjectionDAO;
import com.chairpick.ecommerce.model.*;
import com.chairpick.ecommerce.model.enums.OrderStatus;
import com.chairpick.ecommerce.projections.OrderReportByChairs;
import com.chairpick.ecommerce.utils.pagination.PageInfo;
import com.chairpick.ecommerce.utils.pagination.PageOptions;
import com.chairpick.ecommerce.utils.query.*;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.GeneralObjectQueryMapper;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.ObjectQueryMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;

public class OrderDAO implements PaginatedWithProjectionDAO<Order, OrderReportByChairs> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final GeneralObjectQueryMapper<Order> queryMapper;
    private final ObjectQueryMapper<OrderReportByChairs> projectionQueryMapper;
    private final ResultSetExtractor<List<Order>> extractor;

    public OrderDAO(NamedParameterJdbcTemplate jdbcTemplate, GeneralObjectQueryMapper<Order> queryMapper, ObjectQueryMapper<OrderReportByChairs> projectionQueryMapper, ResultSetExtractor<List<Order>> extractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryMapper = queryMapper;
        this.projectionQueryMapper = projectionQueryMapper;
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
                        .updatedDate(rs.getTimestamp("ord_updated_date").toLocalDateTime())
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
                        .updatedDate(rs.getTimestamp("ord_updated_date").toLocalDateTime())
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
        String sql = """
                DELETE FROM tb_order WHERE ord_id = :ord_id
                """;
        Map<String, Object> parameters = Map.of("ord_id", id);
        jdbcTemplate.update(sql, parameters);
    }

    @Override
    public PageInfo<Order> findAndPaginate(Map<String, String> parameters, PageOptions pageOptions) {
        QueryResult sql = queryMapper.parseParameters(parameters, pageOptions);

        return jdbcTemplate.query(sql.query(), sql.parameters(), rs -> {
            Set<Order> orders = new HashSet<>();
            int totalPages = 0;
            while (rs.next()) {

                totalPages = rs.getInt("total_count");
                Order order = Order.builder()
                        .id(rs.getLong("ord_id"))
                        .status(OrderStatus.valueOf(rs.getString("ord_status")))
                        .totalAmount(rs.getInt("ord_total_amount"))
                        .totalValue(rs.getDouble("ord_total_value"))
                        .items(new ArrayList<>())
                        .customer(Customer
                                .builder()
                                .id(rs.getLong("ord_customer_id"))
                                .build())
                        .createdDate(rs.getDate("ord_created_date").toLocalDate())
                        .updatedDate(rs.getTimestamp("ord_updated_date").toLocalDateTime())
                        .build();
                orders.add(order);
            }

            return new PageInfo<>(totalPages, orders.stream().toList());
        });
    }

    @Override
    public List<OrderReportByChairs> findAndMapForProjection(Map<String, String> parameters) {
        QueryResult sql = projectionQueryMapper.parseParameters(parameters);
        return jdbcTemplate.query(sql.query(), sql.parameters(), (rs) -> {
            List<OrderReportByChairs> reports = new ArrayList<>();
            while (rs.next()) {
                OrderReportByChairs report = OrderReportByChairs
                        .builder()
                        .chairName(rs.getString("chr_name"))
                        .date(rs.getDate("ord_created_date").toLocalDate())
                        .soldValue(rs.getDouble("total_value"))
                        .build();
                reports.add(report);
            }
            return reports;
        });
    }
}
