package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.io.input.OrderStatusInput;
import com.chairpick.ecommerce.io.input.SwapInput;
import com.chairpick.ecommerce.model.Swap;
import com.chairpick.ecommerce.services.SwapService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SwapController {

    private final SwapService swapService;

    public SwapController(SwapService swapService) {
        this.swapService = swapService;
    }

    @GetMapping("admin/orders/{orderId}/swaps")
    public ModelAndView getAllOrderSwaps(@PathVariable("orderId") Long orderId) {
        ModelAndView view = new ModelAndView("swaps/index.html");
        view.addObject("swaps", swapService.findAllSwapsByOrderId(orderId));
        view.addObject("orderId", orderId);
        return view;
    }

    @PostMapping("customers/{customerId}/orders/{orderId}/swaps")
    public ResponseEntity<Swap> requestSwap(@PathVariable("customerId") Long customerId, @PathVariable("orderId") Long orderId, @RequestBody SwapInput input) {
        Swap savedSwap = swapService.createSwapRequest(orderId, input);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSwap);
    }

    @PatchMapping("admin/orders/{orderId}/swaps/{swapId}")
    public ResponseEntity<Swap> updateSwap(@PathVariable("orderId") Long orderId, @PathVariable("swapId") Long swapId, @RequestBody OrderStatusInput input) {
        Swap updatedSwap = swapService.updateSwapStatus(orderId, swapId, input);
        return ResponseEntity.ok(updatedSwap);
    }
}
