package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.io.input.ItemInput;
import com.chairpick.ecommerce.io.output.ChairDTO;
import com.chairpick.ecommerce.model.Item;
import com.chairpick.ecommerce.services.ChairService;
import com.chairpick.ecommerce.services.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/admin/chairs/{id}/items")
public class ItemController {

    private final ItemService itemService;
    private final ChairService chairService;

    public ItemController(ItemService itemService, ChairService chairService) {
        this.itemService = itemService;
        this.chairService = chairService;
    }

    @GetMapping("/new")
    public ModelAndView redirectToCreateNewItems(@PathVariable("id") Long chairId) {
        ChairDTO chair = chairService.findChairById(chairId);
        ModelAndView view = new ModelAndView("items/insert.html");
        view.addObject("chair", chair);
        return view;
    }

    @PostMapping
    public ResponseEntity<Item> createNewItem(@PathVariable("id") Long chairId, @RequestBody ItemInput item) {
        Item createdItem = itemService.createNewItem(chairId, item);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }
}
