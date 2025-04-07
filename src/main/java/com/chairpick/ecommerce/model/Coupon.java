package com.chairpick.ecommerce.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class Coupon extends DomainEntity {

    private Customer customer;
    private double value;

    @Override
    public void validate() {

    }
}
