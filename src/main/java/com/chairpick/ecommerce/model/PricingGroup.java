package com.chairpick.ecommerce.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class PricingGroup extends DomainEntity {
    private String name;
    private double percentageValue;

    @Override
    public void validate() {

    }

    @Override
    public String toString() {
        return "PricingGroup{" +
                "name='" + name + '\'' +
                ", percentageValue=" + percentageValue +
                '}';
    }
}
