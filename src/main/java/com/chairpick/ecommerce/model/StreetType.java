package com.chairpick.ecommerce.model;

public enum StreetType {
    STREET("Rua"),
    AVENUE("Avenida"),
    PLATTER("Travessa"),
    SQUARE("Praça"),
    ROAD("Estrada"),
    HIGHWAY("Rodovia"),
    LANE("Alameda"),
    ALLEY("Beco"),
    OTHER("Outro");

    private final String name;

    StreetType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
