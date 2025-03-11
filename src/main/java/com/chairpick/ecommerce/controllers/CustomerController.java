package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.model.io.NewCustomerInput;
import com.chairpick.ecommerce.model.io.NewPasswordInput;
import com.chairpick.ecommerce.services.CustomerService;
import com.chairpick.ecommerce.utils.format.CpfFormater;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@RestController
public class CustomerController extends BaseCustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping("/new")
    public ModelAndView redirectToNewCustomer() {
        return new ModelAndView("customers/insert.html");
    }

    @GetMapping("/{id}/alter-password")
    public ModelAndView redirectToAlterPassword(@PathVariable Long id) {
        Customer customer = service.findById(id);
        ModelAndView view = new ModelAndView();
        view.setViewName("customers/alter-password.html");
        view.addObject("customerId", id);
        return view;
    }

    @GetMapping("/{id}/transactions")
    public ModelAndView redirectToCustomerTransactions(@PathVariable Long id) {
        Customer customer = service.findById(id);
        ModelAndView view = new ModelAndView();
        view.setViewName("transactions/index.html");
        view.addObject("customer", customer);
        return view;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView redirectToEditCustomer(@PathVariable Long id) {
        Customer customer = service.findById(id);
        ModelAndView view = new ModelAndView();
        view.setViewName("customers/edit.html");
        view.addObject("customer", customer);
        return view;
    }

    @GetMapping
    public ModelAndView getCustomers(@RequestParam Map<String, String> params) {
        List<Customer> customers = service.findAllActiveCustomers(params);
        ModelAndView view = new ModelAndView();
        view.setViewName("customers/index.html");
        view.addObject("customers", customers);

        return view;
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody NewCustomerInput input) {
        Customer createdCustomer = service.createCustomer(input);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/alter-password")
    public ResponseEntity<Void> alterPassword(@PathVariable Long id, @RequestBody NewPasswordInput input) {
        service.alterPassword(input);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody NewCustomerInput input) {
        Customer updatedCustomer = service.updateCustomer(id, input);
        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        service.deleteCustomer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
