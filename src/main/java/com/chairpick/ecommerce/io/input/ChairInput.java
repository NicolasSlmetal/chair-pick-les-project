package com.chairpick.ecommerce.io.input;

import java.util.List;

public record ChairInput(
        String name,
        String description,
        double width,
        double height,
        double length,
        double weight,
        double averageRating,
        List<Long> categories,
        Long pricingGroupId) {

    @Override
    public String toString() {
        return "ChairInput{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", length=" + length +
                ", weight=" + weight +
                ", averageRating=" + averageRating +
                ", categories=" + categories +
                ", pricingGroupId=" + pricingGroupId +
                '}';
    }
}
