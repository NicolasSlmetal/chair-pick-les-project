package com.chairpick.ecommerce.utils.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public abstract class FilterObject {

    private List<ValueFilter> filters;

    public final boolean hasFilters() {
        return !filters.isEmpty();
    }

    public abstract Object toObjectFilter();
    public abstract boolean hasUndefinedFilters();

    @Override
    public String toString() {
        return filters.toString();
    }
}
