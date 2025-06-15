package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.model.Supplier;
import com.chairpick.ecommerce.repositories.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }


    public List<Supplier> findAllSuppliers() {
        return supplierRepository.findAllSuppliers();
    }
}
