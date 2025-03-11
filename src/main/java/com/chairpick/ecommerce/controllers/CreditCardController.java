package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.model.CreditCard;
import com.chairpick.ecommerce.services.CreditCardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@RestController
public class CreditCardController extends BaseCustomerController {

    private final CreditCardService creditCardService;

    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @GetMapping("/{customerId}/credit-cards")
    public ModelAndView getCreditCards(@PathVariable("customerId") Long customerId) {
        List<CreditCard> creditCards = creditCardService.findCreditCardByCustomerId(customerId);

        ModelAndView view = new ModelAndView();
        view.addObject("creditCards", creditCards);
        view.addObject("customer", creditCards.getFirst().getCustomer());
        view.setViewName("credit-cards/index.html");
        return view;
    }

    @GetMapping("/{customerId}/credit-cards/new")
    public ModelAndView redirectToNewCreditCard(@PathVariable("customerId") Long customerId, @RequestParam("customerName") String customerName) {
        ModelAndView view = new ModelAndView();
        view.setViewName("credit-cards/insert.html");
        view.addObject("customerId", customerId);
        view.addObject("customerName", customerName);
        return view;
    }

    @GetMapping("/{customerId}/credit-cards/edit/{creditCardId}")
    public ModelAndView redirectToEditCreditCard(@PathVariable("customerId") Long customerId, @PathVariable("creditCardId") Long creditCardId, @RequestParam String customerName) {
        CreditCard creditCard = creditCardService.findCreditCardById(creditCardId);
        ModelAndView view = new ModelAndView();
        view.setViewName("credit-cards/edit.html");
        view.addObject("creditCard", creditCard);
        view.addObject("customerId", customerId);
        view.addObject("customerName", customerName);
        return view;
    }

    @PostMapping("/{customerId}/credit-cards")
    public ResponseEntity<CreditCard> createCreditCard(@PathVariable("customerId") Long customerId, @RequestBody CreditCard creditCard) {
        CreditCard savedCreditCard = creditCardService.createCreditCard(customerId, creditCard);
        return new ResponseEntity<>(savedCreditCard, HttpStatus.CREATED);
    }

    @PutMapping("/{customerId}/credit-cards")
    public ResponseEntity<CreditCard> updateCreditCard(@PathVariable("customerId") Long customerId, @RequestBody CreditCard creditCard) {
        CreditCard updatedCreditCard = creditCardService.updateCreditCard(customerId, creditCard);
        return new ResponseEntity<>(updatedCreditCard, HttpStatus.OK);
    }

    @DeleteMapping("/{customerId}/credit-cards/{creditCardId}")
    public ResponseEntity<Void> deleteCreditCard(@PathVariable("customerId") Long customerId, @PathVariable("creditCardId") Long creditCardId) {
        creditCardService.deleteCreditCard(customerId, creditCardId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}


