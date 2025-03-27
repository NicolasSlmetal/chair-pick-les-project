package com.chairpick.ecommerce.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Item extends DomainEntity {

    private Chair chair;
    private int amount;
    private double unitCost;
    private int reservedAmount;
    private Supplier supplier;

    @Override
    public void validate() {

    }
}
