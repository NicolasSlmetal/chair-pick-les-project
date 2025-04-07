package com.chairpick.ecommerce.projections;

import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.Customer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CartItemSummaryProjection {
    private Chair chair;
    private int amount;
    private double price;
    private int limit;
    private Customer customer;

    @Override
    public String toString() {
        return "CartItemSummaryProjection{" +
                ", amount=" + amount +
                ", price=" + price +
                ", limit=" + limit +
                '}';
    }
}
