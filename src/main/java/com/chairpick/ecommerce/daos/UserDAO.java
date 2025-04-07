package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

        String sql = """
                UPDATE tb_user
                SET usr_email = :email, usr_password = :password
                WHERE usr_id = :id
                """;

        Map<String, Object> parameters = Map.of(
                "id", entity.getId(),
                "email", entity.getEmail(),
                "password", entity.getPassword()
        );

        jdbcTemplate.update(sql, parameters);
        return entity;
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
        String sql = parseParameters(parameters);

        Map<String, Object> sqlParameters = parameters.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return jdbcTemplate.query(sql, sqlParameters, rowMapper);
    }

    private String parseParameters(Map<String, String> parameters) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM tb_user WHERE 1 = 1");
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String column = "usr_" + entry.getKey();
            sqlBuilder.append(" AND ").append(column).append(" = :").append(entry.getKey());
        }

        return sqlBuilder.toString();
    }

    @Override
    public void delete(Long id) {
        String sql = """
                DELETE FROM tb_user WHERE usr_id = :id;
                """;
        Map<String, Object> parameters = Map.of("id", id);
        jdbcTemplate.update(sql, parameters);
    }
}
