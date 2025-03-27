package com.chairpick.ecommerce.config;

import com.chairpick.ecommerce.daos.*;
import com.chairpick.ecommerce.model.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class DAOProvider {

    @Bean
    GenericDAO<Chair> provideChairDAO() {
        return new ChairDAO();
    }

    @Bean
    GenericDAO<Customer> provideCustomerDAO(NamedParameterJdbcTemplate jdbcTemplate, RowMapper<Customer> rowMapper) {
        return new CustomerDAO(jdbcTemplate, rowMapper);
    }

    @Bean
    GenericDAO<User> provideUserDAO(NamedParameterJdbcTemplate jdbcTemplate, RowMapper<User> rowMapper) {
        return new UserDAO(jdbcTemplate, rowMapper);
    }

    @Bean
    GenericDAO<Address> provideAddressDAO(NamedParameterJdbcTemplate jdbcTemplate, RowMapper<Address> rowMapper) {
        return new AddressDAO(jdbcTemplate, rowMapper);
    }

    @Bean
    GenericDAO<CreditCard> provideCreditCardDAO(NamedParameterJdbcTemplate jdbcTemplate, RowMapper<CreditCard> rowMapper) {
        return new CreditCardDAO(jdbcTemplate, rowMapper);
    }
}
