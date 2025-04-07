package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.io.output.FreightValueDTO;
import com.chairpick.ecommerce.services.FreightCalculatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/freight")
public class FreightController {

    private final FreightCalculatorService freightCalculatorService;

    public FreightController(FreightCalculatorService freightCalculatorService) {
        this.freightCalculatorService = freightCalculatorService;
    }

    @GetMapping
    public ResponseEntity<FreightValueDTO> calculateFreightForChairAndAddress(@RequestParam("chairId") Long chairId, @RequestParam("addressId") Long addressId) {
        FreightValueDTO freight = freightCalculatorService.calculateFreight(chairId, addressId);
        return ResponseEntity.ok(freight);
    }
}
