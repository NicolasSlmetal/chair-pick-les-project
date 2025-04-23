package com.chairpick.ecommerce.config;

import com.chairpick.ecommerce.model.*;
import com.chairpick.ecommerce.utils.mappers.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

@Configuration
public class RowMapperProvider {

    @Bean
    RowMapper<User> getRowMapperForUser() {
        return new UserRowMapper();
    }

    @Bean
    RowMapper<Customer> getRowMapperForCustomer() {
        return new CustomerRowMapper();
    }

    @Bean
    RowMapper<CreditCard> getRowMapperForCreditCard() {
        return new CreditCardMapper();
    }

    @Bean
    RowMapper<Address> getRowMapperForAddress() {
        return new AddressRowMapper();
    }

    @Bean
    RowMapper<Chair> getRowMapperForChair() {
        return new ChairRowMapper();
    }

    @Bean
    RowMapper<Item> getRowMapperForItem() {
        return new ItemRowMapper();
    }

    @Bean
    RowMapper<Cart> getRowMapperForCart() {
        return new CartRowMapper();
    }

    @Bean
    RowMapper<OrderItem> getRowMapperForOrderItem() {
        return new OrderItemRowMapper();
    }

    @Bean
    RowMapper<Swap> getRowMapperForSwap() {
        return new SwapRowMapper();
    }

    @Bean
    RowMapper<Coupon> getRowMapperForCoupon() {
        return new CouponRowMapper();
    }
}
