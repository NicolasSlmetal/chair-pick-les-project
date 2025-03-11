package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.model.Address;
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
public class AddressRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<Address> addressRowMapper;
    private final DataSource dataSource;

    public AddressRepository(NamedParameterJdbcTemplate jdbcTemplate, RowMapper<Address> addressRowMapper, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.addressRowMapper = addressRowMapper;
        this.dataSource = dataSource;
    }


    public Address saveAddress(Address address) {
        Map<String, Object> parameters = getParameters(address);
        parameters.put("add_customer_id", address.getCustomer().getId());

        Long savedAddressId = new SimpleJdbcInsert(dataSource)
                .withTableName("tb_address")
                .usingGeneratedKeyColumns("add_id")
                .executeAndReturnKey(parameters).longValue();
        address.setId(savedAddressId);
        return address;
    }

    private static Map<String, Object> getParameters(Address address) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("add_name", address.getName());
        parameters.put("add_cep", address.getCep());
        parameters.put("add_city", address.getCity());
        parameters.put("add_country", address.getCountry());
        parameters.put("add_default", address.isDefault() ? 1 : 0);
        parameters.put("add_neighborhood", address.getNeighborhood());
        parameters.put("add_number", address.getNumber());
        parameters.put("add_observation", address.getObservations());
        parameters.put("add_state", address.getState());
        parameters.put("add_street_type", address.getStreetType().name());
        parameters.put("add_street", address.getStreet());
        return parameters;
    }

    public List<Address> findAllByCustomer(Customer customer) {
        List<Address> addresses = jdbcTemplate
                .query("SELECT * FROM tb_address a WHERE a.add_customer_id = :customerId ORDER BY a.add_default DESC",
                        Map.of("customerId", customer.getId()),
                        addressRowMapper
                );
        addresses.forEach(address -> address.setCustomer(customer));
        return addresses;
    }

    public Optional<Address> findById(Long id) {
        Address address = jdbcTemplate
                .queryForObject("SELECT * FROM tb_address a WHERE a.add_id = :id",
                        Map.of("id", id),
                        addressRowMapper
                );
        return Optional.ofNullable(address);
    }

    public Optional<Address> findDefaultAddressByCustomer(Customer customer) {
        Address address = jdbcTemplate
                .queryForObject("SELECT * FROM tb_address a WHERE a.add_customer_id = :customerId AND a.add_default = 1",
                        Map.of("customerId", customer.getId()),
                        addressRowMapper
                );
        return Optional.ofNullable(address);
    }

    public Address updateAddress(Address address) {
        Map<String, Object> parameters = getParameters(address);
        parameters.put("id", address.getId());
        jdbcTemplate.update("UPDATE tb_address SET add_name = :add_name, add_cep = :add_cep, add_city = :add_city, add_country = :add_country, add_default = :add_default, add_neighborhood = :add_neighborhood, add_number = :add_number, add_observation = :add_observation, add_state = :add_state, add_street_type = :add_street_type, add_street = :add_street WHERE add_id = :id",
                parameters);
        return address;
    }

    public void deleteAddress(Long addressId) {
        jdbcTemplate.update("DELETE FROM tb_address WHERE add_id = :id",
                Map.of("id", addressId));
    }

}
