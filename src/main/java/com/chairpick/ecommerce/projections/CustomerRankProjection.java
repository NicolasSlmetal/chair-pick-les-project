package com.chairpick.ecommerce.projections;

import com.chairpick.ecommerce.model.Customer;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CustomerRankProjection {
    private Customer customer;
    private int rank;
}
