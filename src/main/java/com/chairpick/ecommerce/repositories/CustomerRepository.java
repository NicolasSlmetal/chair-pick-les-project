package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.Address;
import com.chairpick.ecommerce.model.CreditCard;
import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CustomerRepository {

    private final GenericDAO<Customer> customerDAO;
    private final GenericDAO<User> userDAO;
    private final GenericDAO<Address> addressDAO;
    private final GenericDAO<CreditCard> creditCardDAO;

    public CustomerRepository(GenericDAO<Customer> customerDAO, GenericDAO<User> userDAO, GenericDAO<Address> addressDAO, GenericDAO<CreditCard> creditCardDAO) {
        this.customerDAO = customerDAO;
        this.userDAO = userDAO;
        this.addressDAO = addressDAO;
        this.creditCardDAO = creditCardDAO;
    }

    public List<Customer> findAllCustomers(Map<String, String> params) {
        if (params.isEmpty()) {
            return customerDAO.findAll();
        }
        return customerDAO.findBy(params);
    }

    public Optional<Customer> findById(Long id) {
        return customerDAO.findById(id);
    }

    @Transactional
    public Customer saveCustomer(Customer customer) {
        User savedUser = userDAO.save(customer.getUser());
        customer.setUser(savedUser);

        Customer savedCustomer = customerDAO.save(customer);
        customer.getAddresses().forEach(address -> {
            address.setCustomer(savedCustomer);
            addressDAO.save(address);
        });

        customer.getCreditCards().forEach(creditCard -> {
            creditCard.setCustomer(savedCustomer);
            creditCardDAO.save(creditCard);
        });

        return savedCustomer;
    }

    public Optional<Customer> findByUser(User user) {
        Map<String, String> params = Map.of("user_id", String.valueOf(user.getId()));
        return customerDAO.findBy(params).stream().findFirst();
    }

    public Customer updatePassword(Customer customer) {
        userDAO.update(customer.getUser());
        return customer;
    }

    @Transactional
    public Customer updateCustomer(Customer customer) {
        userDAO.update(customer.getUser());
        customerDAO.update(customer);
        return customer;
    }

    public void deleteCustomer(Long id) {
        customerDAO.delete(id);
    }

}
