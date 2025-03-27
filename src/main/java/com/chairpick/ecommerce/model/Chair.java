package com.chairpick.ecommerce.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Chair extends DomainEntity{

    private String name;
    private String description;
    private double sellPrice;
    private double height;
    private double width;
    private double length;
    private double weight;
    private double averageRating;
    private PricingGroup pricingGroup;

    @Override
    public void validate() {

    }
}
