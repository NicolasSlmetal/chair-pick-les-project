package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.model.Address;
import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.services.AddressService;
import com.chairpick.ecommerce.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class AddressController {

    private final AddressService service;
    private final CustomerService customerService;

    public AddressController(AddressService service, CustomerService customerService) {
        this.service = service;
        this.customerService = customerService;
    }

    @GetMapping("/customers/{customerId}/addresses")
    public ModelAndView getAddresses(@PathVariable("customerId") Long customerId) {
        List<Address> addresses = service.findAddressesByCustomerId(customerId);

        ModelAndView view = new ModelAndView();
        view.addObject("addresses", addresses);
        view.addObject("customer", addresses.getFirst().getCustomer());
        view.setViewName("addresses/index.html");
        return view;
    }

    @GetMapping("/customers/{customerId}/addresses/new")
    public ModelAndView redirectToNewAddress(@PathVariable("customerId") Long customerId) {
        Customer customer = customerService.findById(customerId);
        ModelAndView view = new ModelAndView();
        view.setViewName("addresses/insert.html");
        view.addObject("customerId", customerId);
        view.addObject("customerName", customer.getName());
        return view;
    }

    @GetMapping("/customers/{customerId}/addresses/edit/{addressId}")
    public ModelAndView redirectToEditAddress(@PathVariable("customerId") Long customerId, @PathVariable("addressId") Long addressId, @RequestParam String customerName) {
        Address address = service.findAddressById(addressId);
        ModelAndView view = new ModelAndView();
        view.setViewName("addresses/edit.html");
        view.addObject("address", address);
        view.addObject("customerId", customerId);
        view.addObject("customerName", customerName);
        return view;
    }

    @PostMapping("/customers/{customerId}/addresses")
    public ResponseEntity<Address> createAddress(@PathVariable("customerId") Long customerId, @RequestBody Address address) {
        Address savedAddress = service.createAddress(customerId, address);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }

    @PutMapping("/customers/{customerId}/addresses")
    public ResponseEntity<Address> updateAddress(@PathVariable("customerId") Long customerId, @RequestBody Address address) {
        Address savedAddress = service.updateAddress(customerId, address);
        return new ResponseEntity<>(savedAddress, HttpStatus.OK);
    }

}
