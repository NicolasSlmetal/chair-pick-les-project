package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.model.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserDAO implements GenericDAO<User> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<User> rowMapper;

    public UserDAO(NamedParameterJdbcTemplate jdbcTemplate, RowMapper<User> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public User save(User entity) {
        String sql = """
                INSERT INTO tb_user ( usr_email, usr_password, usr_type)
                VALUES (:email, :password, :usr_type) RETURNING usr_id
                """;
        Map<String, Object> parameters = Map.of(
                "usr_type", entity.getType().name(),
                "email", entity.getEmail(),
                "password", entity.getPassword()
        );

        Long id = jdbcTemplate.queryForObject(sql, parameters, Long.class);
        entity.setId(id);
        return entity;
    }

    @Override
    public User update(User entity) {
        return null;
    }

    @Override
    public List<User> findAll() {
        String sql = """
                SELECT * FROM tb_user;
                """;
        return jdbcTemplate.getJdbcTemplate().query(sql, rowMapper);
    }

    @Override
    public Optional<User> findById(Long id) {

        String sql = """
                SELECT * FROM tb_user WHERE usr_id = :id LIMIT 1;
                """;
        Map<String, Object> parameters = Map.of("id", id);
        List<User> user = jdbcTemplate.query(sql, parameters, rowMapper);
        return user.isEmpty() ? Optional.empty() : Optional.of(user.getFirst());
    }

    @Override
    public List<User> findBy(Map<String, String> parameters) {
        return List.of();
    }

    @Override
    public void delete(Long id) {

    }
}
