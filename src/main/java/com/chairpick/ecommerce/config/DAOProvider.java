package com.chairpick.ecommerce.config;

import com.chairpick.ecommerce.daos.*;
import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.daos.interfaces.ProjectionDAO;
import com.chairpick.ecommerce.daos.interfaces.WriteOnlyDAO;
import com.chairpick.ecommerce.model.*;
import com.chairpick.ecommerce.projections.CartItemSummaryProjection;
import com.chairpick.ecommerce.projections.ChairAvailableProjection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

@Configuration
public class DAOProvider {

    @Bean
    ProjectionDAO<Chair, ChairAvailableProjection> provideChairDAO(NamedParameterJdbcTemplate jdbcTemplate, ResultSetExtractor<List<Chair>> extractor) {
        return new ChairDAO(jdbcTemplate, extractor);
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

    @Bean
    GenericDAO<Item> provideItemDAO(NamedParameterJdbcTemplate jdbcTemplate, RowMapper<Item> rowMapper) {
        return new ItemDAO(jdbcTemplate, rowMapper);
    }

    @Bean
    ProjectionDAO<Cart, CartItemSummaryProjection> provideCartDAO(NamedParameterJdbcTemplate jdbcTemplate, RowMapper<Cart> rowMapper) {
        return new CartDAO(jdbcTemplate, rowMapper);
    }

    @Bean
    GenericDAO<Order> provideOrderDAO(NamedParameterJdbcTemplate jdbcTemplate, ResultSetExtractor<List<Order>> extractor) {
        return new OrderDAO(jdbcTemplate, extractor);
    }

    @Bean
    WriteOnlyDAO<OrderItem> provideOrderItemDAO(NamedParameterJdbcTemplate jdbcTemplate) {
        return new OrderItemDAO(jdbcTemplate);
    }

}
