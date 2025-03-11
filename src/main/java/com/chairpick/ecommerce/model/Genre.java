package com.chairpick.ecommerce.model;

import lombok.Getter;

@Getter
public enum Genre {
    MALE("Masculino"),
    FEMALE("Feminino");

    public final String name;

    Genre(String name) {
        this.name = name;
    }

}
