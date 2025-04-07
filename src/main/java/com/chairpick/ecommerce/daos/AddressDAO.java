package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.Address;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AddressDAO implements GenericDAO<Address> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<Address> rowMapper;

    public AddressDAO(NamedParameterJdbcTemplate jdbcTemplate, RowMapper<Address> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public Address save(Address entity) {
        String sql = """ 
                INSERT INTO tb_address (add_street, add_name, add_number, add_observation, add_city, add_state, add_country, add_cep, add_neighborhood, add_street_type, add_customer_id, add_default)
                VALUES (:add_street, :add_name, :add_number, :add_observation, :add_city, :add_state, :add_country, :add_cep, :add_neighborhood, :add_street_type, :add_customer_id, :add_default) 
                RETURNING add_id
                """;

        Map<String, Object> parameters = getParameters(entity);

        Long id = jdbcTemplate.queryForObject(sql, parameters, Long.class);
        entity.setId(id);
        return entity;
    }

    private static Map<String, Object> getParameters(Address entity) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("add_street", entity.getStreet());
        parameters.put("add_name", entity.getName());
        parameters.put("add_number", entity.getNumber());
        parameters.put("add_observation", entity.getObservations());
        parameters.put("add_city", entity.getCity());
        parameters.put("add_state", entity.getState());
        parameters.put("add_country", entity.getCountry());
        parameters.put("add_cep", entity.getCep());
        parameters.put("add_neighborhood", entity.getNeighborhood());
        parameters.put("add_street_type", entity.getStreetType().name());
        parameters.put("add_customer_id", entity.getCustomer().getId());
        parameters.put("add_default", entity.isDefault() ? 1 : 0);
        return parameters;
    }

    @Override
    public Address update(Address entity) {
        String sql = """
                UPDATE tb_address SET add_street = :add_street, add_name = :add_name, add_number = :add_number, add_observation = :add_observation, add_city = :add_city, add_state = :add_state, add_country = :add_country, add_cep = :add_cep, add_neighborhood = :add_neighborhood, add_street_type = :add_street_type, add_customer_id = :add_customer_id, add_default = :add_default
                WHERE add_id = :add_id
                """;
        Map<String, Object> parameters = getParameters(entity);
        parameters.put("add_id", entity.getId());
        jdbcTemplate.update(sql, parameters);
        return entity;
    }

    @Override
    public List<Address> findAll() {
        String sql = """
                SELECT * FROM tb_address;
                """;
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<Address> findById(Long id) {
        String sql = """
                SELECT * FROM tb_address WHERE add_id = :id;
                """;
        List<Address> addresses = jdbcTemplate.query(sql, Map.of("id", id), rowMapper);
        return addresses.isEmpty() ? Optional.empty() : Optional.of(addresses.getFirst());
    }

    @Override
    public List<Address> findBy(Map<String, String> parameters) {

        String sql = parseParameters(parameters);

        return jdbcTemplate.query(sql, parameters, rowMapper);
    }

    private String parseParameters(Map<String, String> parameters) {
        StringBuilder baseSql = new StringBuilder("SELECT * FROM tb_address WHERE 1 = 1");
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String value = entry.getValue();
            String operator = " ILIKE ";
            if (entry.getKey().equalsIgnoreCase("add_customer_id")) {
                value = entry.getValue();
                operator = " = ";
            }

            if (entry.getKey().equalsIgnoreCase("add_default")) {
                value = entry.getValue();
                operator = " = ";
            }

            baseSql.append(" AND ").append(entry.getKey()).append(operator).append(value);
        }

        baseSql.append(" ORDER BY add_default DESC");

        return baseSql.toString();
    }

    @Override
    public void delete(Long id) {
        String sql = """
                DELETE FROM tb_address WHERE add_id = :id;
                """;
        jdbcTemplate.update(sql, Map.of("id", id));
    }
}
