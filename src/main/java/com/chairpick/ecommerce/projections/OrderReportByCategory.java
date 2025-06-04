package com.chairpick.ecommerce.projections;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class OrderReportByCategory {

    private String categoryName;
    private Double soldValue;
    private LocalDate date;
}
