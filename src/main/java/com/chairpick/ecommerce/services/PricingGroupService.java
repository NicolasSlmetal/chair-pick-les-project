package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.model.PricingGroup;
import com.chairpick.ecommerce.repositories.PricingGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PricingGroupService {

    private final PricingGroupRepository pricingGroupRepository;

    public PricingGroupService(PricingGroupRepository pricingGroupRepository) {
        this.pricingGroupRepository = pricingGroupRepository;
    }

    public List<PricingGroup> findAllPricingGroups() {
        return pricingGroupRepository.findAllPricingGroups();
    }
}
