package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.exceptions.OptimisticLockException;
import com.chairpick.ecommerce.model.Item;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemDAO implements GenericDAO<Item> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<Item> rowMapper;

    public ItemDAO(NamedParameterJdbcTemplate jdbcTemplate, RowMapper<Item> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }


    @Override
    public Item save(Item entity) {
        return null;
    }

    @Override
    public Item update(Item entity) {
        String sql = """
                UPDATE tb_item SET itm_amount = :amount, itm_reserved = :reserved, itm_version = itm_version + 1
                WHERE itm_id = :id AND itm_version = :version;
                """;

        Map<String, Object> parameters = Map.of(
                "amount", entity.getAmount(),
                "reserved", entity.getReservedAmount(),
                "id", entity.getId(),
                "version", entity.getVersion()
        );
        int updated = jdbcTemplate.update(sql, parameters);

        if (updated == 0) {
            throw new OptimisticLockException("Item with id " + entity.getId() + " was updated by another transaction");
        }

        entity.setVersion(entity.getVersion() + updated);
        return entity;
    }

    @Override
    public List<Item> findAll() {
        return List.of();
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = """
                SELECT * FROM tb_item WHERE itm_id = :id;
                """;
        Map<String, Object> parameters = Map.of("id", id);
        List<Item> items = jdbcTemplate.query(sql, parameters, rowMapper);
        return items.isEmpty() ? Optional.empty() : Optional.of(items.getFirst());
    }

    @Override
    public List<Item> findBy(Map<String, String> parameters) {
        String sql = """
                """;

        return jdbcTemplate.query(sql, parameters, rowMapper);
    }

    @Override
    public void delete(Long id) {
        String sql = """
                DELETE FROM tb_item WHERE itm_id = :id;
                """;
        Map<String, Object> parameters = Map.of("id", id);
        jdbcTemplate.update(sql, parameters);
    }
}
