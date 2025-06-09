package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.model.PricingGroup;
import com.chairpick.ecommerce.services.PricingGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/pricing-groups")
public class PricingGroupController {

    private final PricingGroupService pricingGroupService;

    public PricingGroupController(PricingGroupService pricingGroupService) {
        this.pricingGroupService = pricingGroupService;
    }

    @GetMapping
    public ResponseEntity<List<PricingGroup>> findAllPricingGroups() {

        return ResponseEntity.ok(pricingGroupService.findAllPricingGroups());
    }

}
