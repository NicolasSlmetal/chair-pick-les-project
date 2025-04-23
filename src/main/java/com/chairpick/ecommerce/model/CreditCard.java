package com.chairpick.ecommerce.model;

import com.chairpick.ecommerce.model.enums.CreditCardBrand;
import com.chairpick.ecommerce.utils.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Builder
public class CreditCard extends DomainEntity  {

    private Long id;
    private String number;
    private String name;
    private CreditCardBrand brand;
    private String cvv;
    private boolean isDefault;
    private Customer customer;

    @Override
    public void validate() {
        if (!haveValidNumber()) {
            getErrors().add(ErrorCode.INVALID_CREDIT_CARD_NUMBER);
        }

        if (brand == null) {
            getErrors().add(ErrorCode.INVALID_CARD_BRAND);
        }

        if (name == null || name.isBlank()) {
            getErrors().add(ErrorCode.INVALID_CARD_HOLDER);
        }

        if (cvv == null || cvv.isBlank() || !cvv.matches("^\\d{3}$")) {
            getErrors().add(ErrorCode.INVALID_CVV);
        }

        verifyIfHasErrors();
    }

    public boolean haveValidNumber() {
        if (number == null || number.isBlank()) {
            return false;
        }

        return number.replace(" ", "").matches("^\\d{16}$");
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", brand=" + brand +
                ", cvv='" + cvv + '\'' +
                ", isDefault=" + isDefault +
                ", customer=" + customer +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreditCard that = (CreditCard) o;
        return Objects.equals(getNumber(), that.getNumber()) && Objects.equals(getName(), that.getName()) && getBrand() == that.getBrand() && Objects.equals(getCvv(), that.getCvv());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNumber(), getName(), getBrand(), getCvv());
    }
}
