package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.model.CreditCard;
import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.services.CreditCardService;
import com.chairpick.ecommerce.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@RestController
public class CreditCardController {

    private final CreditCardService creditCardService;
    private final CustomerService customerService;

    public CreditCardController(CreditCardService creditCardService, CustomerService customerService) {
        this.creditCardService = creditCardService;
        this.customerService = customerService;
    }

    @GetMapping("/customers/{customerId}/credit-cards")
    public ModelAndView getCreditCards(@PathVariable("customerId") Long customerId) {
        List<CreditCard> creditCards = creditCardService.findCreditCardByCustomerId(customerId);

        ModelAndView view = new ModelAndView();
        view.addObject("creditCards", creditCards);
        view.addObject("customer", creditCards.getFirst().getCustomer());
        view.setViewName("credit-cards/index.html");
        return view;
    }

    @GetMapping("/customers/{customerId}/credit-cards/new")
    public ModelAndView redirectToNewCreditCard(@PathVariable("customerId") Long customerId) {
        Customer customer = customerService.findById(customerId);
        ModelAndView view = new ModelAndView();
        view.setViewName("credit-cards/insert.html");
        view.addObject("customerId", customerId);
        view.addObject("customerName", customer.getName());
        return view;
    }

    @GetMapping("/customers/{customerId}/credit-cards/edit/{creditCardId}")
    public ModelAndView redirectToEditCreditCard(@PathVariable("customerId") Long customerId, @PathVariable("creditCardId") Long creditCardId, @RequestParam String customerName) {
        CreditCard creditCard = creditCardService.findCreditCardById(creditCardId);
        ModelAndView view = new ModelAndView();
        view.setViewName("credit-cards/edit.html");
        view.addObject("creditCard", creditCard);
        view.addObject("customerId", customerId);
        view.addObject("customerName", customerName);
        return view;
    }

    @PostMapping("/customers/{customerId}/credit-cards")
    public ResponseEntity<CreditCard> createCreditCard(@PathVariable("customerId") Long customerId, @RequestBody CreditCard creditCard) {
        CreditCard savedCreditCard = creditCardService.createCreditCard(customerId, creditCard);
        return new ResponseEntity<>(savedCreditCard, HttpStatus.CREATED);
    }

    @PutMapping("/customers/{customerId}/credit-cards")
    public ResponseEntity<CreditCard> updateCreditCard(@PathVariable("customerId") Long customerId, @RequestBody CreditCard creditCard) {
        CreditCard updatedCreditCard = creditCardService.updateCreditCard(customerId, creditCard);
        return new ResponseEntity<>(updatedCreditCard, HttpStatus.OK);
    }
}


