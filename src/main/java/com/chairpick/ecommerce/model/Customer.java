package com.chairpick.ecommerce.model;

import com.chairpick.ecommerce.model.enums.Genre;
import com.chairpick.ecommerce.model.enums.PhoneType;
import com.chairpick.ecommerce.utils.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
@SuperBuilder
public class Customer extends DomainEntity {

    private String name;
    private String cpf;
    private PhoneType phoneType;
    private String phoneDDD;
    private String phone;
    private LocalDate bornDate;
    private List<Address> addresses;
    private List<CreditCard> creditCards;
    private Genre genre;
    private User user;

    @Override
    public void validate() {
        validateMainFields();

        if (addresses == null || addresses.isEmpty()) {
            getErrors().add(ErrorCode.ADDRESSES_REQUIRED);
        }

        if (creditCards == null || creditCards.isEmpty()) {
            getErrors().add(ErrorCode.CREDIT_CARDS_REQUIRED);
        }

        verifyIfHasErrors();

        addresses.forEach(Address::validate);

        creditCards.forEach(CreditCard::validate);
    }

    public void validateMainFields() {
        if (name == null || name.isBlank()) {
            getErrors().add(ErrorCode.INVALID_CUSTOMER_NAME);
        }

        if (!haveValidCPF()) {
            getErrors().add(ErrorCode.INVALID_CPF);
        }

        if (!haveValidPhoneDDD()) {
            getErrors().add(ErrorCode.INVALID_PHONE_DDD);
        }

        if (!haveValidPhone()) {
            getErrors().add(ErrorCode.INVALID_PHONE);
        }

        if (!haveValidBornDate()) {
            getErrors().add(ErrorCode.INVALID_BORN_DATE);
        }

        if (genre == null) {
            getErrors().add(ErrorCode.GENRE_REQUIRED);
        }

        if (phoneType == null) {
            getErrors().add(ErrorCode.INVALID_PHONE_TYPE);
        }


        if (user == null) {
            getErrors().add(ErrorCode.REQUIRED_USER);
        }

        verifyIfHasErrors();
    }

    private boolean haveValidCPF() {
        if (cpf == null || cpf.isBlank()) {
            return false;
        }

        if (cpf.length() != 11) {
            return false;
        }

        for (char c : cpf.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        if (cpf.chars().allMatch(c -> c == '0' || c == '1')) {
            return false;
        }

        int firstVerifierDigit = Integer.parseInt(cpf.substring(9, 10));
        int secondVerifierDigit = Integer.parseInt(cpf.substring(10, 11));
        int sum = 0;

        for (int i = 0; i < 9; i++) {
            int digit = Integer.parseInt(cpf.substring(i, i + 1));
            sum += digit * (10 - i);
        }

        Integer firstVerifierDigitResult = sum % 11;
        firstVerifierDigitResult = firstVerifierDigitResult < 2 ? 0 : 11 - firstVerifierDigitResult;
        if (!firstVerifierDigitResult.equals(firstVerifierDigit)) {

            return false;
        }

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Integer.parseInt(cpf.substring(i, i + 1)) * (11 - i);
        }

        Integer secondVerifierDigitResult = sum % 11;
        secondVerifierDigitResult = secondVerifierDigitResult < 2 ? 0 : 11 - secondVerifierDigitResult;
        return secondVerifierDigitResult.equals(secondVerifierDigit);
    }

    private boolean haveValidPhone() {
        if (phone == null || phone.isBlank()) {
            return false;
        }

        if (phone.length() != 9 && phone.length() != 8) {
            return false;
        }

        for (char c : phone.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }

    private boolean haveValidBornDate() {
        LocalDate now = LocalDate.now();
        if (bornDate == null) {
            return false;
        }

        if (bornDate.isAfter(now)) {
            return false;
        }

        return now.getYear() - bornDate.getYear() >= 18;

    }

    private boolean haveValidPhoneDDD() {
        if (phoneDDD == null || phoneDDD.isBlank()) {
            return false;
        }

        if (phoneDDD.length() != 2) {
            return false;
        }

        return phoneDDD.matches("[0-9]{2}");
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", cpf='" + cpf + '\'' +
                ", phoneType=" + phoneType +
                ", phoneDDD='" + phoneDDD + '\'' +
                ", phone='" + phone + '\'' +
                ", bornDate=" + bornDate +
                ", addresses=" + addresses +
                ", creditCards=" + creditCards +
                ", genre=" + genre +
                ", user=" + user +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(getCpf(), customer.getCpf());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCpf());
    }
}
