package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.io.input.OrderInput;
import com.chairpick.ecommerce.io.output.TotalValueDTO;
import com.chairpick.ecommerce.model.CreditCard;
import com.chairpick.ecommerce.model.Order;
import com.chairpick.ecommerce.projections.CartItemSummaryProjection;
import com.chairpick.ecommerce.services.CartService;
import com.chairpick.ecommerce.services.CreditCardService;
import com.chairpick.ecommerce.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final CreditCardService creditCardService;
    private final CartService cartService;

    public OrderController(OrderService orderService, CreditCardService creditCardService, CartService cartService) {
        this.orderService = orderService;
        this.creditCardService = creditCardService;
        this.cartService = cartService;
    }

    @PostMapping("customers/{customerId}/orders")
    public ResponseEntity<Order> createOrder(@PathVariable Long customerId, @RequestBody OrderInput orderInput) {
        return new ResponseEntity<>(orderService.createOrder(customerId, orderInput), HttpStatus.CREATED);
    }

    @GetMapping("customers/{customerId}/orders/payment")
    public ModelAndView confirmPayment(@PathVariable Long customerId, @CookieValue("deliveryAddressId") Long deliveryAddressId, @CookieValue("billingAddressId") Long billingAddressId) {

        List<CreditCard> creditCards = creditCardService.findCreditCardByCustomerId(customerId);
        Map<CartItemSummaryProjection, TotalValueDTO> totalAmountMap = cartService.showCartConfirmationWithSelectedAddress(customerId, deliveryAddressId);

        ModelAndView view = new ModelAndView("orders/payment.html");
        view.addObject("customerId", customerId);
        view.addObject("creditCards", creditCards);
        view.addObject("deliveryAddressId", deliveryAddressId);
        view.addObject("billingAddressId", billingAddressId);
        view.addObject("totalValue", cartService.getTotalValueWithFreight(totalAmountMap));
        return view;
    }
}
