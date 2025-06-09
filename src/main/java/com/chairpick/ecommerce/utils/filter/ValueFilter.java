package com.chairpick.ecommerce.utils.filter;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValueFilter {

    private String field;
    private String operator;
    private Object value;
    private boolean negate; 

    @Override
    public String toString() {
        return "ValueFilter{" +
                "field='" + field + '\'' +
                ", operator='" + operator + '\'' +
                ", value=" + value +
                '}';
    }
}
