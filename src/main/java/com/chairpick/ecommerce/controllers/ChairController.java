package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.io.input.ChairInput;
import com.chairpick.ecommerce.io.input.ChairStatusChangeInput;
import com.chairpick.ecommerce.io.input.UpdateChairInput;
import com.chairpick.ecommerce.io.output.ChairDTO;
import com.chairpick.ecommerce.io.output.CompleteChairDTO;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.projections.ChairAvailableProjection;
import com.chairpick.ecommerce.services.ChairImageService;
import com.chairpick.ecommerce.services.ChairService;
import com.chairpick.ecommerce.services.FreightCalculatorService;
import com.chairpick.ecommerce.utils.pagination.PageInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Controller
public class ChairController {

    private final ChairService chairService;
    private final ChairImageService chairImageService;

    public ChairController(ChairService chairService, FreightCalculatorService freightCalculatorService, ChairImageService chairImageService) {
        this.chairService = chairService;
        this.chairImageService = chairImageService;
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

    @GetMapping("/admin/chairs/{id}/edit")
    public ModelAndView redirectToEditChairForm(@PathVariable Long id) {
        Chair chair = chairService.findChairByIdToEdit(id);
        Path imageFile = chairImageService.getChairImage(chair.getId()).getFileName();
        ModelAndView view = new ModelAndView("chairs/edit.html");
        view.addObject("chair", chair);
        view.addObject("imageFile", imageFile);
        return view;
    }

    @PostMapping("/admin/chairs")
    public ResponseEntity<Chair> createChair(@RequestPart("image") MultipartFile image, @RequestPart("input") ChairInput input) throws IOException {
        Chair chair = chairService.save(input);
        chairImageService.saveChairImage(chair, image.getInputStream(), image.getOriginalFilename());
        return new ResponseEntity<>(chair, HttpStatus.CREATED);
    }

    @PutMapping("/admin/chairs/{id}")
    public ResponseEntity<Chair> updateChair(@PathVariable Long id, @RequestParam(name = "image", required = false) MultipartFile image, @RequestPart("input") UpdateChairInput input) throws IOException {
        Chair chair = chairService.update(id, input);
        if (image != null && !image.isEmpty()) {
            chairImageService.saveChairImage(chair, image.getInputStream(), image.getOriginalFilename());
        }
        return new ResponseEntity<>(chair, HttpStatus.OK);
    }

    @PatchMapping("/admin/chairs/{id}/status")
    public ResponseEntity<Chair> changeChairStatus(@PathVariable("id") Long chairId, @RequestBody ChairStatusChangeInput input) {
        Chair chair = chairService.changeChairStatus(chairId, input);
        return ResponseEntity.ok(chair);
    }



}
