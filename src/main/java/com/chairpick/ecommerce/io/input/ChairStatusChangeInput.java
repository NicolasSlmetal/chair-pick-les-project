package com.chairpick.ecommerce.io.input;

public record ChairStatusChangeInput(
        boolean active,
        String reason
) {
}
