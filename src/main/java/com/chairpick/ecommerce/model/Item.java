package com.chairpick.ecommerce.model;

import com.chairpick.ecommerce.utils.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@SuperBuilder
public class Item extends DomainEntity {

    private Chair chair;
    private int amount;
    private double unitCost;
    private int reservedAmount;
    private LocalDate entryDate;
    private Supplier supplier;
    private int version;

    @Override
    public void validate() {

        if (chair == null) {
            getErrors().add(ErrorCode.CHAIR_REQUIRED);
        }

        if (amount < 0) {
            getErrors().add(ErrorCode.AMOUNT_REQUIRED);
        }

        if (unitCost <= 0) {
            getErrors().add(ErrorCode.UNIT_COST_REQUIRED);
        }

        if (reservedAmount < 0) {
            getErrors().add(ErrorCode.RESERVED_AMOUNT_REQUIRED);
        }

        if (reservedAmount > amount) {
            getErrors().add(ErrorCode.RESERVED_AMOUNT_EXCEEDS_LIMIT);
        }

        if (!validateEntryDate()) {
            getErrors().add(ErrorCode.INVALID_ENTRY_DATE);
        }

        verifyIfHasErrors();
    }

    public boolean validateEntryDate() {
        if (entryDate == null) {
            getErrors().add(ErrorCode.INVALID_ENTRY_DATE);
            return false;
        }
        return !entryDate.isAfter(LocalDate.now());
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + getId() +
                ", amount=" + amount +
                ", unitCost=" + unitCost +
                ", reservedAmount=" + reservedAmount +
                ", supplier=" + supplier +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(getChair(), item.getChair()) && Objects.equals(getEntryDate(), item.getEntryDate()) && Objects.equals(getSupplier(), item.getSupplier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChair(), getEntryDate(), getSupplier());
    }


}
