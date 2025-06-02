package com.chairpick.ecommerce.projections;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatBotResponse {

    private ChairAvailableProjection chair;
    private String message;
}
