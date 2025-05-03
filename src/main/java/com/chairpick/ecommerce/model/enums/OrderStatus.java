package com.chairpick.ecommerce.model.enums;

public enum OrderStatus {
    PENDING("EM PROCESSAMENTO"),
    APPROVED("APROVADA"),
    DELIVERING("EM ENTREGA"),
    DELIVERED("ENTREGUE"),
    REPROVED("REPROVADA"),
    SWAP_REQUEST("TROCA SOLICITADA"),
    SWAP_REPROVED("TROCA REPROVADA"),
    IN_SWAP("TROCA EM PROCESSO"),
    SWAPPED("TROCA REALIZADA");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
