package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.io.output.ChairDTO;
import com.chairpick.ecommerce.services.ChairService;
import com.chairpick.ecommerce.services.FreightCalculatorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/chairs")
public class ChairController {

    private final ChairService chairService;

    public ChairController(ChairService chairService, FreightCalculatorService freightCalculatorService) {
        this.chairService = chairService;
    }

    @RequestMapping("/{id}")
    public ModelAndView findChairById(@PathVariable Long id, HttpServletRequest request) {

        ChairDTO chairDTO = chairService.findChairById(id);

        ModelAndView view = new ModelAndView("chairs/chair.html");

        if (request.getAttribute("customerId") != null) {
            Long customerId = (Long) request.getAttribute("customerId");
            view.addObject("customerId", customerId);
        }

        view.addObject("chair", chairDTO);
        return view;
    }

}
