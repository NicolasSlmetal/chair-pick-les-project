package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.io.output.ChairDTO;
import com.chairpick.ecommerce.io.output.CompleteChairDTO;
import com.chairpick.ecommerce.projections.ChairAvailableProjection;
import com.chairpick.ecommerce.services.ChairService;
import com.chairpick.ecommerce.services.FreightCalculatorService;
import com.chairpick.ecommerce.utils.pagination.PageInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
public class ChairController {

    private final ChairService chairService;

    public ChairController(ChairService chairService, FreightCalculatorService freightCalculatorService) {
        this.chairService = chairService;
    }

    @GetMapping("/chairs/search")
    public ResponseEntity<PageInfo<ChairAvailableProjection>> searchForChairs(@RequestParam Map<String, String> parameters) {
        return ResponseEntity.ok(chairService.searchForChairs(parameters));
    }

    @GetMapping("/chairs/{id}")
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

    @GetMapping("/admin/chairs")
    public ModelAndView redirectToAdminChairs() {
        ModelAndView view = new ModelAndView("chairs/index.html");
        List<CompleteChairDTO> chairs = chairService.findAllChairs();
        view.addObject("chairs", chairs);
        return view;
    }

    @GetMapping("/admin/chairs/new")
    public ModelAndView redirectToNewChairForm() {
        return new ModelAndView("chairs/new.html");
    }



}
