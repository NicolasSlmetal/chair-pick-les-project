package com.chairpick.ecommerce.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PricingGroup extends DomainEntity {
    private String name;
    private double percentageValue;

    @Override
    public void validate() {

    }
}
