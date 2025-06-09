package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.model.Category;
import com.chairpick.ecommerce.projections.OrderReportByCategory;
import com.chairpick.ecommerce.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/admin/categories/reports")
    public ResponseEntity<List<OrderReportByCategory>> findOrderReportsByCategory(
            @RequestParam(name = "startDate", defaultValue = "#{T(java.time.LocalDate).now().minusMonths(1)}")
            LocalDate startDate, @RequestParam(name = "endDate", defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate endDate) {
        return ResponseEntity.ok(categoryService.findOrderReportsByCategory(startDate, endDate));
    }

    @GetMapping("/admin/categories")
    public ResponseEntity<List<Category>> findAllCategories() {
        return ResponseEntity.ok(categoryService.findAllCategories());
    }
}
