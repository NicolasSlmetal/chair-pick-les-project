package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.GenericDAO;
import com.chairpick.ecommerce.model.Chair;
import org.springframework.stereotype.Repository;

@Repository
public class ChairRepository {

    private final GenericDAO<Chair> chairDAO;

    public ChairRepository(GenericDAO<Chair> chairDAO) {
        this.chairDAO = chairDAO;
    }
}
