package com.chairpick.ecommerce.projections;

import com.chairpick.ecommerce.model.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChairAvailableProjection that = (ChairAvailableProjection) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
