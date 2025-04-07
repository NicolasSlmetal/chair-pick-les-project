package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.ProjectionDAO;
import com.chairpick.ecommerce.model.Cart;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.projections.CartItemSummaryProjection;
import com.chairpick.ecommerce.utils.query.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CartDAO implements ProjectionDAO<Cart, CartItemSummaryProjection> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<Cart> rowMapper;

    public CartDAO(NamedParameterJdbcTemplate jdbcTemplate, RowMapper<Cart> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public Cart save(Cart entity) {
        String sql = """
                INSERT INTO tb_cart (car_item_id, car_customer_id, car_item_amount, car_item_price, car_item_entry_datetime, car_item_status, car_item_limit)
                VALUES (:item_id, :customer_id, :item_amount, :item_price, :item_entry_datetime, :item_status, :car_item_limit)
                RETURNING car_id;
                """;
        Map<String, Object> parameters = Map.of(
                "item_id", entity.getItem().getId(),
                "customer_id", entity.getCustomer().getId(),
                "item_amount", entity.getAmount(),
                "item_price", entity.getPrice(),
                "item_entry_datetime", entity.getEntryDate(),
                "item_status", entity.getStatus().name(),
                "car_item_limit", entity.getLimit()
        );
        Long id = jdbcTemplate.queryForObject(sql, parameters, Long.class);
        entity.setId(id);
        return entity;
    }

    @Override
    public Cart update(Cart entity) {
        String sql = """
                UPDATE tb_cart SET car_item_amount = :item_amount, car_item_price = :item_price, car_item_status = :item_status, car_item_limit = :car_item_limit
                WHERE car_id = :id
                """;
        Map<String, Object> parameters = Map.of(
                "item_amount", entity.getAmount(),
                "item_price", entity.getPrice(),
                "item_status", entity.getStatus().name(),
                "car_item_limit", entity.getLimit(),
                "id", entity.getId()
        );
        jdbcTemplate.update(sql, parameters);

        return entity;
    }

    @Override
    public List<Cart> findAll() {
        String sql = "SELECT * FROM tb_cart INNER JOIN tb_item ON car_item_id = itm_id";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<Cart> findById(Long id) {
        String sql = """
                SELECT * FROM tb_cart INNER JOIN tb_item ON car_item_id = itm_id WHERE car_id = :id
                """;

        Map<String, Object> parameters = Map.of("id", id);

        List<Cart> carts = jdbcTemplate.query(sql, parameters, rowMapper);
        return carts.isEmpty() ? Optional.empty() : Optional.of(carts.getFirst());
    }

    @Override
    public List<Cart> findBy(Map<String, String> parameters) {
        QueryResult sql = parseParameters(parameters);

        return jdbcTemplate.query(sql.query(), sql.parameters(), rowMapper);
    }

    private QueryResult parseParameters(Map<String, String> parameters) {
        SelectTable sql = SqlQueryBuilder.create().selectingAllFromTable("tb_cart");
        sql.join("tb_item").innerJoinOn("car_item_id", "itm_id");
        sql.joinDifferentTables("tb_item", "tb_chair").innerJoinOn("itm_chair_id", "chr_id");
        Where where = sql.where();
        int size = parameters.size();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String key = "car_" + entry.getKey();
            String value = entry.getValue();

            if (entry.getKey().startsWith("itm_")) {
                key = entry.getKey();
            }

            if (value.matches("0|[1-9]\\d*")) {
                where.equals(key, value);

            } else if (value.matches("(\\d+)-(\\d+)-(\\d+)")) {
                where.equalDate(key, value);
            } else {
                where.ilike(key, value);
            }

            if (--size > 0) {
                where.and();
            }
        }
        EndingOptions ending = where.end().endingOptions();
        return ending.build();
    }

    @Override
    public void delete(Long id) {
        String sql = """
                DELETE FROM tb_cart WHERE car_id = :id
                """;
        Map<String, Object> parameters = Map.of("id", id);
        jdbcTemplate.update(sql, parameters);
    }

    @Override
    public List<CartItemSummaryProjection> findAndMapForProjection(Map<String, String> parameters) {
        String sql = """
                SELECT chr_id, chr_name, chr_weight, chr_length, chr_width, chr_height, chr_sell_price, SUM(car_item_amount) AS amount, MAX(car_item_limit) AS car_item_limit
                FROM tb_cart INNER JOIN tb_item ON car_item_id = itm_id
                INNER JOIN tb_chair ON itm_chair_id = chr_id
                WHERE car_customer_id = CAST(:customer_id AS INTEGER)
                GROUP BY chr_id
                """;

        return jdbcTemplate.query(sql, parameters, (rs) -> {
            List<CartItemSummaryProjection> list = new ArrayList<>();
            while (rs.next()) {
                list.add(CartItemSummaryProjection
                        .builder()
                        .chair(Chair
                                .builder()
                                .id(rs.getLong("chr_id"))
                                .name(rs.getString("chr_name"))
                                .sellPrice(rs.getDouble("chr_sell_price"))
                                .weight(rs.getDouble("chr_weight"))
                                .height(rs.getDouble("chr_height"))
                                .length(rs.getDouble("chr_length"))
                                .width(rs.getDouble("chr_width"))
                                .build())
                        .price(rs.getDouble("chr_sell_price"))
                        .amount(rs.getInt("amount"))
                        .limit(rs.getInt("car_item_limit"))
                        .build());

            }
            return list;
        });
    }
}
