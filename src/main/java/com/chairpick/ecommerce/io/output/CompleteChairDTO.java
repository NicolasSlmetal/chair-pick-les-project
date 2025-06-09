package com.chairpick.ecommerce.io.output;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class CompleteChairDTO {
    private Long id;
    private String name;
    private Double sellPrice;
    private double cost;
    private boolean isActive;
    private String pricingGroupName;
    private int stockAmount;
    private double averageRating;
    private String dimensions;
    private LocalDate lastEntryDate;
    private Double weight;
}
