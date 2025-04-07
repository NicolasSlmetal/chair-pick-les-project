package com.chairpick.ecommerce.projections;

import com.chairpick.ecommerce.model.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ChairAvailableProjection {

    private Long id;
    private String name;
    private double price;
    private List<Category> categories;

    @Override
    public String toString() {
        return "ChairAvailableProjection{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", categories=" + categories +
                '}';
    }
}
