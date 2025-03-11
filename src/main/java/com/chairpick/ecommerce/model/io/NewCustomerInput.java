package com.chairpick.ecommerce.model.io;

import com.chairpick.ecommerce.model.Address;
import com.chairpick.ecommerce.model.CreditCard;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class NewCustomerInput {
    private String name;
    private String email;
    private String cpf;
    private String password;
    private String genre;
    private LocalDate birthDate;
    private String phone;
    private String phoneType;
    private List<Address> addresses;
    private List<CreditCard> creditCards;

    @Override
    public String toString() {
        return "NewCustomerInput{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", cpf='" + cpf + '\'' +
                ", password='" + password + '\'' +
                ", genre='" + genre + '\'' +
                ", birthDate=" + birthDate +
                ", phone='" + phone + '\'' +
                ", phoneType='" + phoneType + '\'' +
                ", addresses=" + addresses +
                ", creditCards=" + creditCards +
                '}';
    }
}
