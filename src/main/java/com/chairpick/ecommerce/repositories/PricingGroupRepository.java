package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.PricingGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PricingGroupRepository {


    private final GenericDAO<PricingGroup> pricingGroupDAO;

    public PricingGroupRepository(GenericDAO<PricingGroup> pricingGroupDAO) {
        this.pricingGroupDAO = pricingGroupDAO;
    }

    public List<PricingGroup> findAllPricingGroups() {
        return pricingGroupDAO.findAll();
    }
}
