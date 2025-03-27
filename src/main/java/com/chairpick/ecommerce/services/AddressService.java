package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.EntityNotFoundException;
import com.chairpick.ecommerce.model.Address;
import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.repositories.AddressRepository;
import com.chairpick.ecommerce.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    public AddressService(AddressRepository addressRepository, CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
    }

    public Address findAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));
    }

    public List<Address> findAddressesByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        List<Address> addresses = addressRepository.findAllByCustomer(customer);

        if (addresses.isEmpty()) {
            throw new EntityNotFoundException("Addresses not found");
        }

        return addresses;
    }

    @Transactional
    public Address createAddress(Long customerId, Address address) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        String unformattedCep = address.getCep().replace("-", "");

        address.setCustomer(customer);
        address.setCep(unformattedCep);
        address.validate();
        setPreviousDefaultAddressToFalse(customerId, address);
        return addressRepository.saveAddress(address);
    }

    public Address findDefaultAddressByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        return addressRepository.findDefaultAddressByCustomer(customer)
                .orElseThrow(() -> new EntityNotFoundException("Default address not found"));
    }

    public Address updateAddress(Long customerId, Address address) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        String unformattedCep = address.getCep().replace("-", "");

        address.setCustomer(customer);
        address.setCep(unformattedCep);
        address.validate();
        setPreviousDefaultAddressToFalse(customerId, address);
        return addressRepository.updateAddress(address);
    }

    public void deleteAddress(Long customerId, Long addressId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        findAddressById(addressId);
        addressRepository.deleteAddress(addressId);
    }

    private void setPreviousDefaultAddressToFalse(Long customerId, Address address) {
        Address defaultAddress = findDefaultAddressByCustomerId(customerId);

        if (address.isDefault()) {
            defaultAddress.setDefault(false);
            addressRepository.updateAddress(defaultAddress);
        }
    }

}
