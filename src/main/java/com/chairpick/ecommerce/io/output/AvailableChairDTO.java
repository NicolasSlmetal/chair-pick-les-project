package com.chairpick.ecommerce.io.output;

import com.chairpick.ecommerce.model.Category;
import com.chairpick.ecommerce.projections.ChairAvailableProjection;

import java.util.List;
import java.util.Map;

public record AvailableChairDTO (Map<Category, List<ChairAvailableProjection>> chairsByCategory, List<ChairAvailableProjection> allChairs) {
    @Override
    public String toString() {
        return "AvailableChairDTO{" +
                "availableChairs=" + chairsByCategory +
                ", allChairs=" + allChairs +
                '}';
    }
}
