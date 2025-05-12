package com.chairpick.ecommerce.controllers;


import com.chairpick.ecommerce.model.Cart;
import com.chairpick.ecommerce.model.User;
import com.chairpick.ecommerce.projections.CartItemSummaryProjection;
import com.chairpick.ecommerce.io.output.TotalValueDTO;
import com.chairpick.ecommerce.io.input.UpdateCartItemInput;
import com.chairpick.ecommerce.security.AuthenticatedUser;
import com.chairpick.ecommerce.services.CartService;
import com.chairpick.ecommerce.services.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customers/{customerId}/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    @GetMapping
    public ModelAndView redirectToCart(@PathVariable Long customerId) {
        List<CartItemSummaryProjection> cartList = cartService.findCartByCustomer(customerId);

        ModelAndView view = new ModelAndView();
        view.addObject("cart" ,cartList);
        view.addObject("customerId", customerId);
        view.addObject("pageTitle", "Cart");

        view.setViewName("cart/index.html");
        return view;
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCartCount(@PathVariable Long customerId) {
        int cartCount = cartService.findCartByCustomer(customerId).size();
        return ResponseEntity.ok(cartCount);
    }

    @GetMapping("/expired")
    public ResponseEntity<List<Cart>> getExpiredCarts(@PathVariable Long customerId) {
        List<Cart> expiredCarts = cartService.findExpiredCartsByCustomer(customerId);
        return ResponseEntity.ok(expiredCarts);
    }

    @GetMapping("/confirm")
    public ModelAndView redirectToConfirm(@PathVariable Long customerId) {
        Map<CartItemSummaryProjection, TotalValueDTO> totalAmountMap = cartService.showCartConfirmation(customerId);

        if (totalAmountMap.isEmpty()) {
            return new ModelAndView("redirect:/cart");
        }

        ModelAndView view = new ModelAndView();

        view.addObject("cart" , totalAmountMap);
        view.addObject("total", cartService.getTotalValue(totalAmountMap));
        view.addObject("freight", cartService.getTotalFreight(totalAmountMap));
        view.addObject("pageTitle", "Confirm");
        view.setViewName("cart/confirm.html");
        return view;
    }

    @GetMapping("/chair/{chairId}")
    public ResponseEntity<List<Cart>> findCartByCustomerAndChairId(@PathVariable(name = "customerId") Long customerId, @PathVariable(name = "chairId") Long chairId) {
        List<Cart> cartList = cartService.findCartByCustomerAndChair(customerId, chairId);
        return ResponseEntity.ok(cartList);
    }

    @PostMapping
    public ResponseEntity<Cart> addToCart(@PathVariable(name = "customerId") Long customerId, @RequestBody Long chairId) {
        Cart cart = cartService.addItemToCart(customerId, chairId);

        return new ResponseEntity<>(cart, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<List<Cart>> updateCart(@PathVariable("customerId") Long customerId, @RequestBody UpdateCartItemInput updateCartItemInput) {
        List<Cart> cartList = cartService.updateChairAmountInCart(customerId, updateCartItemInput);

        return new ResponseEntity<>(cartList, HttpStatus.OK);
    }

    @DeleteMapping("/expired")
    public ResponseEntity<Void> deleteExpiredCarts(@PathVariable(name = "customerId") Long customerId) {
        cartService.deleteExpiredCarts(customerId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/chair/{chairId}")
    public ResponseEntity<Void> deleteChairFromCart(@PathVariable(name = "customerId") Long customerId, @PathVariable("chairId") Long chairId) {
        cartService.deleteChairFromCart(customerId, chairId);

        return ResponseEntity.noContent().build();
    }
}
