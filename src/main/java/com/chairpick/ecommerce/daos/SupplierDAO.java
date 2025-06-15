package com.chairpick.ecommerce.daos;


import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.Supplier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SupplierDAO implements GenericDAO<Supplier> {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SupplierDAO(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Supplier save(Supplier entity) {
        String sql = "INSERT INTO tb_supplier (sup_name) VALUES (:name) RETURNING sup_id";
        Map<String, Object> params = Map.of("name", entity.getName());
        Long id = jdbcTemplate.queryForObject(sql, params, Long.class);
        entity.setId(id);
        return entity;
    }

    @Override
    public Supplier update(Supplier entity) {
        return null;
    }

    @Override
    public List<Supplier> findAll() {
        String sql = "SELECT * FROM tb_supplier";
        return jdbcTemplate.query(sql, Map.of(), (rs, rowNum) -> Supplier
                .builder()
                .id(rs.getLong("sup_id"))
                .name(rs.getString("sup_name"))
                .build());
    }

    @Override
    public Optional<Supplier> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Supplier> findBy(Map<String, String> parameters) {
        return List.of();
    }

    @Override
    public void delete(Long id) {

    }
}
