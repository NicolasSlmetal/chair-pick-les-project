package com.chairpick.ecommerce.model.enums;

public enum PriceChangeRequestStatus {
    PENDING("Pendente"),
    APPROVED("Autorizado"),
    REPROVED("Reprovado");

    private final String description;

    PriceChangeRequestStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
