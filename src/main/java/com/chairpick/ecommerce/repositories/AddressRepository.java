package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.GenericDAO;
import com.chairpick.ecommerce.model.Address;
import com.chairpick.ecommerce.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AddressRepository {

    private final GenericDAO<Address> addressDAO;

    public AddressRepository(GenericDAO<Address> addressDAO) {
        this.addressDAO = addressDAO;
    }

    public Address saveAddress(Address address) {
        return addressDAO.save(address);
    }

    public List<Address> findAllByCustomer(Customer customer) {
        Map<String, String> parameters = Map.of("add_customer_id", customer.getId().toString());
        List<Address> addresses = addressDAO.findBy(parameters);
        addresses.forEach(address -> address.setCustomer(customer));
        return addresses;
    }

    public Optional<Address> findById(Long id) {
        return addressDAO.findById(id);
    }

    public Optional<Address> findDefaultAddressByCustomer(Customer customer) {
        Map<String, String> parameters = Map.of("add_customer_id", customer.getId().toString(), "add_default", "1");
        List<Address> addresses = addressDAO.findBy(parameters);
        addresses.forEach(address -> address.setCustomer(customer));
        return Optional.ofNullable(addresses.getFirst());
    }

    public Address updateAddress(Address address) {
       return addressDAO.update(address);
    }

    public void deleteAddress(Long addressId) {
        addressDAO.delete(addressId);
    }

}
