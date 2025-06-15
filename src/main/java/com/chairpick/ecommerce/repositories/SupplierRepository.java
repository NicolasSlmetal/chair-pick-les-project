package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.Supplier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SupplierRepository {

    private final GenericDAO<Supplier> supplierDAO;

    public SupplierRepository(GenericDAO<Supplier> supplierDAO) {
        this.supplierDAO = supplierDAO;
    }

    public List<Supplier> findAllSuppliers() {
        return supplierDAO.findAll();
    }

    public Optional<Supplier> findById(Long id) {
        return supplierDAO.findById(id);
    }
}
