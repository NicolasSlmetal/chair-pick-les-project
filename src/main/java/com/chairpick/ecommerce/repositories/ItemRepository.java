package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.Item;
import com.chairpick.ecommerce.model.Supplier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class ItemRepository {

    private final GenericDAO<Item> itemDAO;
    private final GenericDAO<Supplier> supplierDAO;
    private final GenericDAO<Chair> chairDAO;

    public ItemRepository(GenericDAO<Item> itemDAO, GenericDAO<Supplier> supplierDAO, GenericDAO<Chair> chairDAO) {
        this.itemDAO = itemDAO;
        this.supplierDAO = supplierDAO;
        this.chairDAO = chairDAO;
    }

    public Optional<Item> findItemById(Long id) {
        return itemDAO.findById(id);
    }

    @Transactional
    public Item save(Item item) {
        if (item.getSupplier().getId() == null) {
            Supplier supplier = item.getSupplier();
            Supplier savedSupplier = supplierDAO.save(supplier);
            item.setSupplier(savedSupplier);
        }
        return itemDAO.save(item);
    }

    @Transactional
    public Item saveAndUpdateChair(Item item) {
        Item savedItem = save(item);
        Chair chair = savedItem.getChair();
        chairDAO.update(chair);
        return savedItem;
    }
}
