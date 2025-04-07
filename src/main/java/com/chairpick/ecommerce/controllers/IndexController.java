package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.io.output.AvailableChairDTO;
import com.chairpick.ecommerce.services.ChairImageLocatorService;
import com.chairpick.ecommerce.services.ChairService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class IndexController {

    private final ChairService chairService;

    public IndexController(ChairService chairService, ChairImageLocatorService chairImageLocatorService) {
        this.chairService = chairService;
    }

    @GetMapping()
    public ModelAndView redirectToIndex(HttpServletRequest request) {
        AvailableChairDTO availableChairs = chairService.findAllChairsAvailableGroupingByCategory();


        ModelAndView view = new ModelAndView("index/index.html");
        view.addObject("pageTitle", "Home");
        if (request.getAttribute("customerId") != null) {
            Long customerId = (Long) request.getAttribute("customerId");
            view.addObject("customerId", customerId);
        }
        view.addObject("chairsByCategories", availableChairs.chairsByCategory());
        view.addObject("chairs", availableChairs.allChairs());
        return view;
    }

    @GetMapping("/404")
    public ModelAndView redirectToNotFound() {
        ModelAndView view = new ModelAndView("notFound.html");
        view.addObject("pageTitle", "Page Not Found");
        return view;
    }


}
