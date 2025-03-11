package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.model.Customer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CustomerRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final RowMapper<Customer> customerRowMapper;

    public CustomerRepository(NamedParameterJdbcTemplate jdbcTemplate, DataSource dataSource,  RowMapper<Customer> customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
        this.customerRowMapper = customerRowMapper;
    }

    public List<Customer> findAllCustomers(Map<String, String> params) {
        StringBuilder sql = new StringBuilder("SELECT * FROM tb_customer c JOIN tb_user u ON u.usr_id = c.cus_user_id WHERE c.cus_active = 1");
        Map<String, Object> queryParams = new HashMap<>();

        params.forEach((key, value) -> {
            if (!value.isEmpty()) {
                String column = key.equals("email") ? "u.usr_email" : "c.cus_" + key;

                if (key.equals("born_date")) {
                    sql.append(" AND ").append(column).append(" = TO_DATE(:").append(key).append(", 'YYYY-MM-DD')");
                    queryParams.put(key, value);
                } else {
                    sql.append(" AND ").append(column).append(" ILIKE :").append(key);
                    queryParams.put(key, "%" + value + "%");
                }
            }
        });

        return jdbcTemplate.query(sql.toString(), queryParams, customerRowMapper);
    }

    public Optional<Customer> findById(Long id) {
        List<Customer> customers = jdbcTemplate
                .query("SELECT * FROM tb_customer c JOIN tb_user u ON u.usr_id = c.cus_user_id WHERE c.cus_id = :id",
                        Map.of("id", id), customerRowMapper);
        return customers.isEmpty() ? Optional.empty() : Optional.of(customers.getFirst());
    }

    public Customer saveCustomer(Customer customer) {
        Long savedUserId = new SimpleJdbcInsert(dataSource)
                .withTableName("tb_user")
                .usingGeneratedKeyColumns("usr_id")
                .executeAndReturnKey(Map.
                        of("usr_email", customer.getUser().getEmail(),
                                "usr_password", customer.getUser().getPassword(),
                                "usr_type", "CUSTOMER")).longValue();
        Long savedCustomerId = new SimpleJdbcInsert(dataSource)
                .withTableName("tb_customer")
                .usingGeneratedKeyColumns("cus_id")
                .executeAndReturnKey(Map.
                        of("cus_name", customer.getName(),
                                "cus_cpf", customer.getCpf(),
                                "cus_born_date", customer.getBornDate(),
                                "cus_phone", customer.getPhone(),
                                "cus_phone_ddd", customer.getPhoneDDD(),
                                "cus_genre", customer.getGenre().name().charAt(0),
                                "cus_phone_type", customer.getPhoneType().name(),
                                "cus_active", 1,
                                "cus_user_id", savedUserId)).longValue();
        customer.setId(savedCustomerId);
        return customer;
    }

    public Customer updatePassword(Customer customer) {
        jdbcTemplate.update("UPDATE tb_user SET usr_password = :password WHERE usr_id = :id",
                Map.of("password", customer.getUser().getPassword(),
                        "id", customer.getUser().getId()));
        return customer;
    }

    public Customer updateCustomer(Customer customer) {
        jdbcTemplate.update("UPDATE tb_user SET usr_email = :email WHERE usr_id = :id",
                Map.of("email", customer.getUser().getEmail(),
                        "id", customer.getUser().getId()));
        jdbcTemplate.update("UPDATE tb_customer SET cus_name = :name, cus_cpf = :cpf, cus_born_date = :bornDate, cus_phone = :phone, cus_phone_ddd = :phoneDDD, cus_genre = :genre, cus_phone_type = :phoneType WHERE cus_id = :id",
                Map.of("name", customer.getName(),
                        "cpf", customer.getCpf(),
                        "bornDate", customer.getBornDate(),
                        "phone", customer.getPhone(),
                        "phoneDDD", customer.getPhoneDDD(),
                        "genre", customer.getGenre().name().charAt(0),
                        "phoneType", customer.getPhoneType().name(),
                        "id", customer.getId()));
        return customer;
    }

    public void deleteCustomer(Long id) {
        jdbcTemplate.update("UPDATE tb_customer SET cus_active = 0 WHERE cus_id = :id", Map.of("id", id));
    }

}
