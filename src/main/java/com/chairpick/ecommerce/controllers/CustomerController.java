package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.io.input.NewCustomerInput;
import com.chairpick.ecommerce.io.input.NewPasswordInput;
import com.chairpick.ecommerce.io.output.TokenResponseDTO;
import com.chairpick.ecommerce.projections.CustomerRankProjection;
import com.chairpick.ecommerce.security.AuthenticatedUser;
import com.chairpick.ecommerce.services.CustomerService;
import com.chairpick.ecommerce.services.TokenService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Controller
public class CustomerController {

    private final CustomerService customerService;
    private final TokenService tokenService;

    public CustomerController(CustomerService customerService, TokenService tokenService) {
        this.customerService = customerService;
        this.tokenService = tokenService;
    }

    @GetMapping("/customers/new")
    public ModelAndView redirectToNewCustomer() {
        return new ModelAndView("customers/insert.html");
    }

    @GetMapping("/customers/{id}/alter-password")
    public ModelAndView redirectToAlterPassword(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        ModelAndView view = new ModelAndView();
        view.setViewName("customers/alter-password.html");
        view.addObject("customerId", id);
        return view;
    }

    @GetMapping("/customers/{id}")
    public ModelAndView redirectToCustomerProfile(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        ModelAndView view = new ModelAndView();
        view.setViewName("customers/profile.html");
        view.addObject("customer", customer);
        view.addObject("pageTitle", "Profile");
        return view;
    }

    @GetMapping("/customers/{id}/edit")
    public ModelAndView redirectToEditCustomer(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        ModelAndView view = new ModelAndView();
        view.setViewName("customers/edit.html");
        view.addObject("customer", customer);
        return view;
    }

    @GetMapping("/admin/customers")
    public ModelAndView getCustomers(@RequestParam Map<String, String> params) {
        List<CustomerRankProjection> customers = customerService.findAllActiveCustomers(params);
        ModelAndView view = new ModelAndView();
        view.setViewName("customers/index.html");
        view.addObject("customers", customers);

        return view;
    }

    @PostMapping("/customers")
    public ResponseEntity<Customer> createCustomer(@RequestBody NewCustomerInput input) {
        Customer createdCustomer = customerService.createCustomer(input);
        TokenResponseDTO tokenResponseDTO = tokenService.generateToken(new AuthenticatedUser(createdCustomer.getUser(), createdCustomer));
        ResponseCookie cookie = ResponseCookie.from("token", tokenResponseDTO.token())
                .httpOnly(true)
                .path("/")
                .sameSite("None")
                .secure(true)
                .maxAge(Duration.ofSeconds(Instant.now().until(tokenResponseDTO.expiration(), ChronoUnit.SECONDS)))
                .build();
        Map<String, String> headerMap = Map.of("Set-Cookie", cookie.toString());
        return new ResponseEntity<>(createdCustomer, MultiValueMap.fromSingleValue(headerMap), HttpStatus.CREATED);
    }

    @PatchMapping("/customers/{id}/alter-password")
    public ResponseEntity<Void> alterPassword(@PathVariable Long id, @RequestBody NewPasswordInput input) {
        customerService.alterPassword(input);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody NewCustomerInput input) {
        Customer updatedCustomer = customerService.updateCustomer(id, input);
        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        ResponseCookie tokenCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .path("/")
                .sameSite("None")
                .secure(true)
                .maxAge(Duration.ZERO)
                .build();
        return ResponseEntity.noContent().header("Set-Cookie", tokenCookie.toString()).build();
    }
}
