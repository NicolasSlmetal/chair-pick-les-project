package com.chairpick.ecommerce.model;


import com.chairpick.ecommerce.model.enums.StreetType;
import com.chairpick.ecommerce.utils.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Address extends DomainEntity {
    private String name;
    private String street;
    private Integer number;
    private String neighborhood;
    private StreetType streetType;
    private boolean isDefault;
    private String city;
    private String state;
    private String country;
    private String cep;
    private String observations;
    private Customer customer;

    @Override
    public void validate() {

        if (name == null || name.isBlank()) {
            getErrors().add(ErrorCode.INVALID_ADDRESS_NAME);
        }

        if (street == null || street.isBlank()) {
            getErrors().add(ErrorCode.INVALID_STREET);
        }

        if (number == null) {
            getErrors().add(ErrorCode.INVALID_ADDRESS_NUMBER);
        }

        if (neighborhood == null || neighborhood.isBlank()) {
            getErrors().add(ErrorCode.INVALID_NEIGHBORHOOD);
        }

        if (streetType == null) {
            getErrors().add(ErrorCode.INVALID_ADDRESS_TYPE);
        }

        if (city == null || city.isBlank()) {
            getErrors().add(ErrorCode.INVALID_CITY);
        }

        if (state == null || state.isBlank()) {
            getErrors().add(ErrorCode.INVALID_STATE);
        }

        if (country == null || country.isBlank()) {
            getErrors().add(ErrorCode.INVALID_COUNTRY);
        }

        if (!haveValidCEP()) {
            getErrors().add(ErrorCode.INVALID_CEP);
        }

        if (observations == null) {
            observations = "";
        }

        verifyIfHasErrors();
    }

    private boolean haveValidCEP() {
        if (cep == null || cep.isBlank()) {
            return false;
        }
        cep = cep.replace("-", "");
        return cep.matches("^\\d{8}$");
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", number='" + number + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", cep='" + cep + '\'' +
                ", customer=" + customer +
                '}';
    }
}
