package com.chairpick.ecommerce.io.output;

import java.time.Instant;

public record TokenResponseDTO(String token, String role, Instant expiration) {
}
