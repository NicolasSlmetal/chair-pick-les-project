package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.DomainValidationException;
import com.chairpick.ecommerce.exceptions.EntityNotFoundException;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.PriceChangeRequest;
import com.chairpick.ecommerce.model.enums.PriceChangeRequestStatus;
import com.chairpick.ecommerce.repositories.ChairRepository;
import com.chairpick.ecommerce.repositories.PriceChangeRequestRepository;
import com.chairpick.ecommerce.utils.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceChangeRequestService {

    private final PriceChangeRequestRepository priceChangeRequestRepository;
    private final ChairRepository chairRepository;

    public PriceChangeRequestService(PriceChangeRequestRepository priceChangeRequestRepository, ChairRepository chairRepository) {
        this.priceChangeRequestRepository = priceChangeRequestRepository;
        this.chairRepository = chairRepository;
    }

    public List<PriceChangeRequest> findAllByChair(Long chairId) {
        Chair chair = chairRepository.findById(chairId)
                .orElseThrow(() -> new IllegalArgumentException("Chair not found with id: " + chairId));
        return priceChangeRequestRepository.findAllByChair(chair);
    }

    public PriceChangeRequest updatePriceChangeRequestStatus(Long chairId, Long priceChangeRequestId, PriceChangeRequestStatus status) {
        Chair chair = chairRepository.findById(chairId)
                .orElseThrow(() -> new IllegalArgumentException("Chair not found with id: " + chairId));

        PriceChangeRequest priceChangeRequest = priceChangeRequestRepository
                .findById(priceChangeRequestId)
                .orElseThrow(() -> new EntityNotFoundException("PriceChangeRequest not found with id: " + priceChangeRequestId));

        validateStatusChange(priceChangeRequest, status);

        priceChangeRequest.setStatus(status);

        if (status == PriceChangeRequestStatus.APPROVED) {

            chair.setSellPrice(priceChangeRequest.getRequestedPrice());
            return priceChangeRequestRepository.approve(priceChangeRequest, chair);
        }

        return priceChangeRequestRepository.update(priceChangeRequest);
    }

    private void validateStatusChange(PriceChangeRequest priceChangeRequest, PriceChangeRequestStatus newStatus) {
        PriceChangeRequestStatus beforeStatus = priceChangeRequest.getStatus();
        boolean isValid = switch (beforeStatus) {
            case PENDING ->  newStatus == PriceChangeRequestStatus.APPROVED || newStatus == PriceChangeRequestStatus.REPROVED;
            default -> false;
        };

        if (!isValid) {
            throw new DomainValidationException(ErrorCode.INVALID_STATUS_CHANGE.name());
        }
    }
}
