package com.chairpick.ecommerce.utils.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public abstract class FilterObject {

    public FilterObject() {

    }
    protected Boolean relevant;
    protected Integer limit;
    protected Double[] priceRange;
    protected Double[] widthRange;
    protected Double[] heightRange;
    protected Double[] lengthRange;
    protected Double[] weightRange;
    protected Double[] ratingRange;
    protected String additionalKeywords;
}
