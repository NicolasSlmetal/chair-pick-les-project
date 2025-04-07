package com.chairpick.ecommerce.model.enums;

public enum CouponType {
    PROMOTIONAL("Promocional"),
    SWAP("Troca");

    private final String description;

    CouponType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
