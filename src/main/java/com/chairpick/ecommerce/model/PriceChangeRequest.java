package com.chairpick.ecommerce.model;

import com.chairpick.ecommerce.model.enums.PriceChangeRequestStatus;
import com.chairpick.ecommerce.utils.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class PriceChangeRequest extends DomainEntity {

    private Chair chair;
    private double requestedPrice;
    private String reason;
    private PriceChangeRequestStatus status;

    @Override
    public void validate() {

        if (chair == null) {
            getErrors().add(ErrorCode.CHAIR_REQUIRED);
        }

        if (requestedPrice <= 0) {
            getErrors().add(ErrorCode.PRICE_CHANGE_REQUESTED_PRICE_REQUIRED);
        }

        if (reason == null || reason.isBlank()) {
            getErrors().add(ErrorCode.PRICE_CHANGE_REASON_REQUIRED);
        }

        verifyIfHasErrors();
    }
}
