package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.Coupon;
import com.chairpick.ecommerce.utils.query.QueryResult;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.ObjectQueryMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CouponDAO implements GenericDAO<Coupon> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectQueryMapper<Coupon> queryMapper;
    private final RowMapper<Coupon> rowMapper;

    public CouponDAO(NamedParameterJdbcTemplate jdbcTemplate, ObjectQueryMapper<Coupon> queryMapper, RowMapper<Coupon> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryMapper = queryMapper;
        this.rowMapper = rowMapper;
    }


    @Override
    public Coupon save(Coupon entity) {
        String sql = """
                INSERT INTO tb_coupon (cpn_type, cpn_value, cpn_customer_id) 
                VALUES (:type, :value, :customerId) RETURNING cpn_id
                """;
        Map<String, Object> parameters = Map.of(
                "type", entity.getType().name(),
                "value", entity.getValue(),
                "customerId", entity.getCustomer().getId()
        );
        Long id = jdbcTemplate.queryForObject(sql, parameters, Long.class);
        entity.setId(id);
        return entity;
    }

    @Override
    public Coupon update(Coupon entity) {
        return null;
    }

    @Override
    public List<Coupon> findAll() {
        return List.of();
    }

    @Override
    public Optional<Coupon> findById(Long id) {
        String sql = """
                SELECT cpn_id, cpn_type, cpn_value, cpn_customer_id
                FROM tb_coupon
                WHERE cpn_id = :id
                """;
        Map<String, Object> parameters = Map.of("id", id);
        List<Coupon> coupons = jdbcTemplate.query(sql, parameters, rowMapper);
        return coupons.isEmpty() ? Optional.empty() : Optional.of(coupons.getFirst());
    }

    @Override
    public List<Coupon> findBy(Map<String, String> parameters) {
        QueryResult sql = queryMapper.parseParameters(parameters);

        return jdbcTemplate.query(sql.query(), sql.parameters(), rowMapper);
    }

    @Override
    public void delete(Long id) {

    }
}
