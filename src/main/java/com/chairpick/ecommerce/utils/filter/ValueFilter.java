package com.chairpick.ecommerce.utils.filter;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValueFilter {

    private String field;
    private String operator;
    private Object value;


}
