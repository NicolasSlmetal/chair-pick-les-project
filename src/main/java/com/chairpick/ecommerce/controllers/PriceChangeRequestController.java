package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.PriceChangeRequest;
import com.chairpick.ecommerce.model.enums.PriceChangeRequestStatus;
import com.chairpick.ecommerce.services.PriceChangeRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/admin/chairs/{chairId}/price-change-requests")
public class PriceChangeRequestController {

    private final PriceChangeRequestService priceChangeRequestService;

    public PriceChangeRequestController(PriceChangeRequestService priceChangeRequestService) {
        this.priceChangeRequestService = priceChangeRequestService;
    }

    @GetMapping
    public ModelAndView redirectToChairPriceChangeRequests(@PathVariable("chairId") Long chairId) {
        List<PriceChangeRequest> priceChangeRequests = priceChangeRequestService.findAllByChair(chairId);
        Chair chair = priceChangeRequests.isEmpty() ? null : priceChangeRequests.getFirst().getChair();
        ModelAndView view = new ModelAndView("price-change-request/index.html");
        view.addObject("priceChangeRequests", priceChangeRequests);
        view.addObject("chair", chair);
        view.addObject("chairId", chairId);
        return view;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PriceChangeRequest> updatePriceChangeRequestStatus(@PathVariable("chairId") Long chairId, @PathVariable("id") Long priceChangeRequestId, @RequestBody PriceChangeRequestStatus status) {
        PriceChangeRequest updatedRequest = priceChangeRequestService.updatePriceChangeRequestStatus(chairId, priceChangeRequestId, status);
        return ResponseEntity.ok(updatedRequest);
    }
}
