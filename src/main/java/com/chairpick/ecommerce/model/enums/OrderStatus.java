package com.chairpick.ecommerce.model.enums;

public enum OrderStatus {
    PENDING("EM PROCESSAMENTO"),
    APPROVED("APROVADA"),
    DELIVERED("ENTREGUE"),
    CANCELED("CANCELADA");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
