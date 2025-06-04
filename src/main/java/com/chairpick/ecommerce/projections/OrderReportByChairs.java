package com.chairpick.ecommerce.projections;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class OrderReportByChairs {

    private String chairName;
    private Double soldValue;
    private LocalDate date;
}
