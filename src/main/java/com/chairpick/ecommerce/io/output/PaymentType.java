package com.chairpick.ecommerce.io.output;

public enum PaymentType {
    CREDIT_CARD("CREDIT_CARD"),
    COUPON("COUPON"),
    COMPOSITE("COMPOSITE");
    private final String type;

    PaymentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}