package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.PriceChangeRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PriceChangeRequestRepository {

    private final GenericDAO<PriceChangeRequest> priceChangeRequestDAO;
    private final GenericDAO<Chair> chairDAO;

    public PriceChangeRequestRepository(GenericDAO<PriceChangeRequest> priceChangeRequestDAO, GenericDAO<Chair> chairDAO) {
        this.priceChangeRequestDAO = priceChangeRequestDAO;
        this.chairDAO = chairDAO;
    }

    public List<PriceChangeRequest> findAllByChair(Chair chair) {
        Map<String, String> parameters = Map.of("chairId", String.valueOf(chair.getId()));
        List<PriceChangeRequest> priceChangeRequests = priceChangeRequestDAO.findBy(parameters);
        priceChangeRequests.forEach(pcr -> pcr.setChair(chair));
        return priceChangeRequests;
    }

    public Optional<PriceChangeRequest> findById(Long id) {
        return priceChangeRequestDAO.findById(id);
    }

    public PriceChangeRequest update(PriceChangeRequest priceChangeRequest) {
        return priceChangeRequestDAO.update(priceChangeRequest);
    }

    @Transactional
    public PriceChangeRequest approve(PriceChangeRequest priceChangeRequest, Chair chair) {
        PriceChangeRequest updatedRequest = update(priceChangeRequest);
        Chair updatedChair = chairDAO.update(chair);
        updatedRequest.setChair(updatedChair);
        return updatedRequest;
    }
}
