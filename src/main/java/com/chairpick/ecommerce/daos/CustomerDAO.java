package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.model.Customer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CustomerDAO implements GenericDAO<Customer> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<Customer> rowMapper;

    public CustomerDAO(NamedParameterJdbcTemplate jdbcTemplate, RowMapper<Customer> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public Customer save(Customer entity) {
        String sql = """
        INSERT INTO tb_customer (cus_name, cus_cpf, cus_phone, cus_phone_ddd, cus_genre, cus_born_date, cus_phone_type, cus_active, cus_user_id)
        VALUES (:name, :cpf, :phone, :phoneDdd, :genre, :bornDate, :phoneType, :active, :userId) RETURNING cus_id
        """;

        Map<String, Object> parameters = Map.of(
            "name", entity.getName(),
            "cpf", entity.getCpf(),
            "phone", entity.getPhone(),
            "phoneDdd", entity.getPhoneDDD(),
            "genre", entity.getGenre().name().charAt(0),
            "bornDate", entity.getBornDate(),
            "phoneType", entity.getPhoneType().name(),
            "active", 1,
            "userId", entity.getUser().getId()
        );

        Long id = jdbcTemplate.queryForObject(sql, parameters, Long.class);
        entity.setId(id);
        return entity;
    }

    @Override
    public Customer update(Customer entity) {
        String sql = """
            UPDATE tb_customer SET cus_name = :name, cus_cpf = :cpf, cus_phone = :phone, cus_phone_ddd = :phoneDdd, cus_genre = :genre, cus_born_date = :bornDate, cus_phone_type = :phoneType, cus_active = :active, cus_user_id = :userId
            WHERE cus_id = :id
            """;
        Map<String, Object> parameters = Map.of(
            "name", entity.getName(),
            "cpf", entity.getCpf(),
            "phone", entity.getPhone(),
            "phoneDdd", entity.getPhoneDDD(),
            "genre", entity.getGenre().name().charAt(0),
            "bornDate", entity.getBornDate(),
            "phoneType", entity.getPhoneType().name(),
            "active", 1,
            "userId", entity.getUser().getId(),
            "id", entity.getId()
        );

        jdbcTemplate.update(sql, parameters);
        return entity;
    }

    @Override
    public List<Customer> findAll() {
        String sql = """
                SELECT * FROM tb_customer c JOIN tb_user u ON u.usr_id = c.cus_user_id WHERE cus_active = 1;
                """;
        return jdbcTemplate.getJdbcTemplate().query(sql, rowMapper);
    }

    @Override
    public Optional<Customer> findById(Long id) {
        String sql = """
                SELECT * FROM tb_customer c JOIN tb_user u ON u.usr_id = c.cus_user_id WHERE cus_id = :id LIMIT 1;
                """;
        Map<String, Object> parameters = Map.of("id", id);
        List<Customer> customers = jdbcTemplate.query(sql, parameters, rowMapper);
        return customers.isEmpty() ? Optional.empty() : Optional.of(customers.getFirst());
    }

    @Override
    public List<Customer> findBy(Map<String, String> parameters) {
        String sql = parseParameters(parameters);
        return jdbcTemplate.query(sql, parameters, rowMapper);
    }

    private String parseParameters(Map<String, String> parameters) {
        StringBuilder baseSql = new StringBuilder("SELECT * FROM tb_customer c JOIN tb_user u ON u.usr_id = c.cus_user_id WHERE cus_active = 1");
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            Object value = "'%"+entry.getValue()+"%'";
            String operator = " ILIKE ";
            String key = entry.getKey().equals("email") ? "usr_email" : "cus_" + entry.getKey();

            if (entry.getKey().equalsIgnoreCase("born_date")) {
                value = "TO_DATE('" + entry.getValue() + "', 'YYYY-MM-DD')";
                operator = "= ";
            }

            if (entry.getKey().equalsIgnoreCase("genre")) {
                value = String.format("'%s'",entry.getValue().charAt(0));
            }

            if (entry.getKey().equalsIgnoreCase("id")) {
                operator = "= ";
            }

            baseSql.append(" AND ").append(key).append(operator).append(value);
        }
        return baseSql.toString();
    }

    @Override
    public void delete(Long id) {
        String sql = """
                UPDATE tb_customer SET cus_active = 0 WHERE cus_id = :id;
                """;
        jdbcTemplate.update(sql, Map.of("id", id));
    }
}
