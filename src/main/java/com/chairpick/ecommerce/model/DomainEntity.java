package com.chairpick.ecommerce.model;

import com.chairpick.ecommerce.exceptions.DomainValidationException;
import com.chairpick.ecommerce.utils.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
public abstract class DomainEntity {
    private Long id;
    private final List<ErrorCode> errors = new ArrayList<>();

    public DomainEntity(Long id) {
        this.id = id;
    }

    public DomainEntity() {

    }

    protected final void verifyIfHasErrors() {
        if (!errors.isEmpty()) {
            List<String> errorMessages = errors.stream().map(ErrorCode::name).toList();
            throw new DomainValidationException(String.join("\n", errorMessages));
        }
    }

    public abstract void validate();
}
