package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.Item;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ItemRepository {

    private final GenericDAO<Item> itemDAO;

    public ItemRepository(GenericDAO<Item> itemDAO) {
        this.itemDAO = itemDAO;
    }

    public Optional<Item> findItemById(Long id) {
        return itemDAO.findById(id);
    }
}
