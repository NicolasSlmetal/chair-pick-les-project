package com.chairpick.ecommerce.io.output;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChairDTO {

    private Long id;
    private String name;
    private double sellPrice;
    private String description;
    private double height;
    private double width;
    private double length;
    private double weight;
    private int availableAmount;
    private double averageRating;

}
