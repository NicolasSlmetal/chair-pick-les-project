package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.EntityNotFoundException;
import com.chairpick.ecommerce.model.CreditCard;
import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.repositories.CreditCardRepository;
import com.chairpick.ecommerce.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final CustomerRepository customerRepository;

    public CreditCardService(CreditCardRepository creditCardRepository, CustomerRepository customerRepository) {
        this.creditCardRepository = creditCardRepository;
        this.customerRepository = customerRepository;
    }

    public List<CreditCard> findCreditCardByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        return creditCardRepository.findByCustomer(customer);
    }

    @Transactional
    public CreditCard createCreditCard(Long customerId, CreditCard creditCard) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        String unformattedNumber = creditCard.getNumber().replace(" ", "");

        creditCard.setNumber(unformattedNumber);
        creditCard.setCustomer(customer);
        creditCard.validate();

        setPreviousDefaultCardToFalse(customerId, creditCard);

        return creditCardRepository.saveCreditCard(creditCard);
    }

    private void setPreviousDefaultCardToFalse(Long customerId, CreditCard creditCard) {
        CreditCard defaultCreditCard = findDefaultCreditCardByCustomerId(customerId);

        if (creditCard.isDefault()) {
            defaultCreditCard.setDefault(false);
            creditCardRepository.updateCreditCard(defaultCreditCard);
        }
    }

    public CreditCard findCreditCardById(Long creditCardId) {
        return creditCardRepository.findById(creditCardId)
                .orElseThrow(() -> new EntityNotFoundException("Credit card not found"));
    }

    public CreditCard findDefaultCreditCardByCustomerId(Long customerId) {
        return creditCardRepository.findDefaultCreditCardByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Default credit card not found"));
    }

    public CreditCard updateCreditCard(Long customerId, CreditCard providedCreditCard) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        String unformattedNumber = providedCreditCard.getNumber().replace(" ", "");

        CreditCard creditCard = findCreditCardById(providedCreditCard.getId());
        creditCard.setNumber(unformattedNumber);
        creditCard.setName(providedCreditCard.getName());
        creditCard.setBrand(providedCreditCard.getBrand());
        creditCard.setCvv(providedCreditCard.getCvv());
        creditCard.setDefault(providedCreditCard.isDefault());
        creditCard.setCustomer(providedCreditCard.getCustomer());
        creditCard.validate();

        setPreviousDefaultCardToFalse(customerId, creditCard);

        return creditCardRepository.updateCreditCard(creditCard);

    }

    public void deleteCreditCard(Long customerId, Long creditCardId) {
        customerRepository.findById(customerId);
        CreditCard creditCard = findCreditCardById(creditCardId);
        creditCardRepository.deleteCreditCard(creditCardId);
    }
}
