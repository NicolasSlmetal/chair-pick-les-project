package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.Swap;
import com.chairpick.ecommerce.utils.query.QueryResult;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.ObjectQueryMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SwapDAO implements GenericDAO<Swap> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<Swap> rowMapper;
    private final ObjectQueryMapper<Swap> queryMapper;

    public SwapDAO(NamedParameterJdbcTemplate jdbcTemplate, RowMapper<Swap> rowMapper, ObjectQueryMapper<Swap> queryMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
        this.queryMapper = queryMapper;
    }

    @Override
    public Swap save(Swap entity) {
        String sql = """
                INSERT INTO tb_item_swap (its_status, its_amount, its_total_value, its_order_item_id)
                VALUES (:its_status, :its_amount, :its_total_value, :its_order_item_id) RETURNING its_id
                """;
        Map<String, Object> parameters = Map.of(
                "its_status", entity.getStatus().name(),
                "its_amount", entity.getAmount(),
                "its_total_value", entity.getValue(),
                "its_order_item_id", entity.getOrderItem().getId()
        );
        Long id = jdbcTemplate.queryForObject(sql, parameters, Long.class);
        entity.setId(id);
        return entity;
    }

    @Override
    public Swap update(Swap entity) {
        String sql = """
                UPDATE tb_item_swap
                SET its_status = :its_status, its_amount = :its_amount, its_total_value = :its_total_value
                WHERE its_id = :its_id
                """;
        Map<String, Object> parameters = Map.of(
                "its_status", entity.getStatus().name(),
                "its_amount", entity.getAmount(),
                "its_total_value", entity.getValue(),
                "its_id", entity.getId()
        );
        jdbcTemplate.update(sql, parameters);
        return entity;
    }

    @Override
    public List<Swap> findAll() {
        return List.of();
    }

    @Override
    public Optional<Swap> findById(Long id) {
        String sql = """
                SELECT *
                FROM tb_item_swap
                INNER JOIN tb_order_item
                ON its_order_item_id = ori_id
                WHERE its_id = :its_id
                """;
        Map<String, Object> parameters = Map.of("its_id", id);
        List<Swap> swaps = jdbcTemplate.query(sql, parameters, rowMapper);

        return swaps.isEmpty() ? Optional.empty() : Optional.of(swaps.getFirst());
    }

    @Override
    public List<Swap> findBy(Map<String, String> parameters) {
        QueryResult sql = queryMapper.parseParameters(parameters);

        return jdbcTemplate.query(sql.query(), sql.parameters(), rowMapper);
    }

    @Override
    public void delete(Long id) {

    }
}
