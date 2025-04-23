package com.chairpick.ecommerce.io.input;

public record SwapInput(Long orderItemId, int amount) {

    @Override
    public String toString() {
        return "SwapInput{" +
                "orderItemId=" + orderItemId +
                ", amount=" + amount +
                '}';
    }
}
