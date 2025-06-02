package com.chairpick.ecommerce.utils.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public abstract class FilterObject {

    private List<ValueFilter> filters;
}
