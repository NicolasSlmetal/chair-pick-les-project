package com.chairpick.ecommerce.projections;

import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.model.enums.CartItemStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class CartItemSummaryProjection {
    private Chair chair;
    private int amount;
    private double price;
    private int limit;
    private Customer customer;
    private LocalDateTime entryDateTime;

    @Override
    public String toString() {
        return "CartItemSummaryProjection{" +
                ", amount=" + amount +
                ", price=" + price +
                ", limit=" + limit +
                '}';
    }
}
