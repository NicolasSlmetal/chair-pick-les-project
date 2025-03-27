package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.GenericDAO;
import com.chairpick.ecommerce.model.CreditCard;
import com.chairpick.ecommerce.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CreditCardRepository {

    private final GenericDAO<CreditCard> creditCardDAO;

    public CreditCardRepository(GenericDAO<CreditCard> creditCardDAO) {
        this.creditCardDAO = creditCardDAO;
    }

    public List<CreditCard> findByCustomer(Customer customer) {
        Map<String, String> parameters = Map.of("cre_customer_id", customer.getId().toString());

        List<CreditCard> creditCards = creditCardDAO.findBy(parameters);
        creditCards.forEach(creditCard -> creditCard.setCustomer(customer));
        return creditCards;
    }

    public CreditCard saveCreditCard(CreditCard creditCard) {
        return creditCardDAO.save(creditCard);
    }

    public Optional<CreditCard> findDefaultCreditCardByCustomerId(Long customerId) {
        return Optional.ofNullable(creditCardDAO.findBy(Map.of("cre_customer_id", customerId.toString(), "cre_default", "1")).getFirst());
    }

    public Optional<CreditCard> findById(Long creditCardId) {
        return creditCardDAO.findById(creditCardId);
    }

    public CreditCard updateCreditCard(CreditCard creditCard) {
        return creditCardDAO.update(creditCard);
    }

    public void deleteCreditCard(Long creditCardId) {
        creditCardDAO.delete(creditCardId);
    }
}
