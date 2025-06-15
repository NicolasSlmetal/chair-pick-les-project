package com.chairpick.ecommerce.io.input;

import java.util.List;

public record UpdateChairInput(
        String name,
        String description,
        Double width,
        Double height,
        Double length,
        Double weight,
        Double averageRating,
        Long pricingGroupId,
        Double price,
        String reason,
        List<Long> categories
) {
}
