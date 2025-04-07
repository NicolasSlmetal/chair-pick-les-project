package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.EntityNotFoundException;
import com.chairpick.ecommerce.model.*;
import com.chairpick.ecommerce.io.input.NewCustomerInput;
import com.chairpick.ecommerce.io.input.NewPasswordInput;
import com.chairpick.ecommerce.model.enums.Genre;
import com.chairpick.ecommerce.model.enums.PhoneType;
import com.chairpick.ecommerce.model.enums.UserType;
import com.chairpick.ecommerce.repositories.AddressRepository;
import com.chairpick.ecommerce.repositories.CreditCardRepository;
import com.chairpick.ecommerce.repositories.CustomerRepository;
import com.chairpick.ecommerce.utils.format.CpfFormater;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final CreditCardRepository creditCardRepository;


    public CustomerService(CustomerRepository customerRepository, AddressRepository addressRepository, CreditCardRepository creditCardRepository) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.creditCardRepository = creditCardRepository;
    }

    public List<Customer> findAllActiveCustomers(Map<String, String> params) {
        List<Customer> customers = customerRepository.findAllCustomers(params);
        customers.forEach(customer -> CpfFormater.format(customer.getCpf()));
        return customers;
    }

    @Transactional
    public Customer createCustomer(NewCustomerInput input) {

        String[] splitPhone = input.getPhone().split(" ");
        String ddd = splitPhone[0].replaceAll("[()]", "").trim();
        String phone = splitPhone[1].replaceAll("[^0-9]", "").trim();
        String cpf = input.getCpf().replaceAll("[^0-9]", "").trim();

        User user = User.builder()
                .email(input.getEmail().trim())
                .password(input.getPassword().trim())
                .type(UserType.CUSTOMER)
                .build();
        user.validate();

        Customer customer = Customer.builder()
                .name(input.getName().trim())
                .cpf(cpf)
                .bornDate(input.getBirthDate())
                .phone(phone)
                .user(user)
                .genre(Genre.valueOf(input.getGenre().toUpperCase()))
                .phoneDDD(ddd)
                .phoneType(PhoneType.valueOf(input.getPhoneType().toUpperCase()))
                .creditCards(input.getCreditCards())
                .addresses(input.getAddresses())
                .build();

        customer.validate();

        customer.getCreditCards().forEach(creditCard -> creditCard.setNumber(creditCard.getNumber().replaceAll("[^0-9]", "").trim()));
        customer.getAddresses().forEach(address -> address.setCep(address.getCep().replaceAll("[^0-9]", "").trim()));

        String salt = BCrypt.gensalt();
        customer.getUser().setPassword(BCrypt.hashpw(input.getPassword(), salt));

        return customerRepository.saveCustomer(customer);
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Customer not found"));
    }

    public Customer updateCustomer(Long id, NewCustomerInput input) {
        Customer customer = findById(id);

        String[] splitPhone = input.getPhone().split(" ");
        String ddd = splitPhone[0].replaceAll("[()]", "").trim();
        String phone = splitPhone[1].replaceAll("[^0-9]", "").trim();
        String cpf = input.getCpf().replaceAll("[^0-9]", "").trim();

        customer.getUser().setEmail(input.getEmail());
        customer.getUser().validateOnlyEmail();

        customer.setName(input.getName());
        customer.setBornDate(input.getBirthDate());
        customer.setGenre(Genre.valueOf(input.getGenre().toUpperCase()));
        customer.setPhone(phone);
        customer.setPhoneDDD(ddd);
        customer.setCpf(cpf);
        customer.setPhoneType(PhoneType.valueOf(input.getPhoneType().toUpperCase()));
        customer.validateMainFields();

        return customerRepository.updateCustomer(customer);
    }

    public void alterPassword(NewPasswordInput input) {
        Customer customer = findById(input.getId());
        customer.getUser().setPassword(input.getPassword());
        customer.getUser().validateOnlyPassword();
        String salt = BCrypt.gensalt();
        customer.getUser().setPassword(BCrypt.hashpw(input.getPassword(), salt));
        customerRepository.updatePassword(customer);
    }

    public void deleteCustomer(Long id) {
        findById(id);
        customerRepository.deleteCustomer(id);
    }
}
