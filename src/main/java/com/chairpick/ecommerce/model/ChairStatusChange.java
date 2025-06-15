package com.chairpick.ecommerce.model;

import com.chairpick.ecommerce.utils.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@SuperBuilder
@Getter
@Setter
public class ChairStatusChange extends DomainEntity{

    private Chair chair;
    private boolean status;
    private String reason;
    private LocalDate entryDate;

    @Override
    public void validate() {

        if (chair == null) {
            getErrors().add(ErrorCode.CHAIR_REQUIRED);
        }

        if (reason == null || reason.isBlank()) {
            getErrors().add(ErrorCode.CHAIR_STATUS_CHANGE_REASON_REQUIRED);
        }

        if (entryDate == null) {
            entryDate = LocalDate.now();
        }

        verifyIfHasErrors();
    }
}
