package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.model.Supplier;
import com.chairpick.ecommerce.services.SupplierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<List<Supplier>> findAllSuppliers() {
        List<Supplier> suppliers = supplierService.findAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }
}
