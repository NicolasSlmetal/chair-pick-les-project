package com.chairpick.ecommerce.config;

import com.chairpick.ecommerce.model.Address;
import com.chairpick.ecommerce.model.CreditCard;
import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.model.User;
import com.chairpick.ecommerce.utils.mappers.AddressMapper;
import com.chairpick.ecommerce.utils.mappers.CreditCardMapper;
import com.chairpick.ecommerce.utils.mappers.CustomerMapper;
import com.chairpick.ecommerce.utils.mappers.UserMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

@Configuration
public class RowMapperProvider {

    @Bean
    RowMapper<User> getRowMapperForUser() {
        return new UserMapper();
    }

    @Bean
    RowMapper<Customer> getRowMapperForCustomer() {
        return new CustomerMapper();
    }

    @Bean
    RowMapper<CreditCard> getRowMapperForCreditCard() {
        return new CreditCardMapper();
    }

    @Bean
    RowMapper<Address> getRowMapperForAddress() {
        return new AddressMapper();
    }
}
