package com.chairpick.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum PhoneType {
    LANE("Fixo"),
    CELL_PHONE("Celular");

    private String type;

    PhoneType(String type) {
        this.type = type;
    }

}
