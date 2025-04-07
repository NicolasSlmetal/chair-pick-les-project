package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.EntityNotFoundException;
import com.chairpick.ecommerce.model.Category;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.Item;
import com.chairpick.ecommerce.io.output.AvailableChairDTO;
import com.chairpick.ecommerce.projections.ChairAvailableProjection;
import com.chairpick.ecommerce.io.output.ChairDTO;
import com.chairpick.ecommerce.repositories.ChairRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChairService {

    private final ChairRepository chairRepository;

    public ChairService(ChairRepository chairRepository) {
        this.chairRepository = chairRepository;
    }

    public AvailableChairDTO findAllChairsAvailableGroupingByCategory() {
        Map<Category, List<ChairAvailableProjection>> chairsByCategory = chairRepository
                .findAllChairsAvailableGroupingByCategory();
        return new AvailableChairDTO(chairsByCategory, chairsByCategory
                .values()
                .stream()
                .flatMap(List::stream)
                .toList()
        );
    }

    public ChairDTO findChairById(Long id) {
        Chair chair = chairRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chair not found with id: " + id));
        int totalAmount = chair
                .getItems()
                .stream()
                .map(Item::getAmount)
                .reduce(0, Integer::sum);
        int totalReservedAmount = chair
                .getItems()
                .stream()
                .map(Item::getReservedAmount)
                .reduce(0, Integer::sum);
        return ChairDTO
                .builder()
                .name(chair.getName())
                .width(chair.getWidth())
                .height(chair.getHeight())
                .length(chair.getLength())
                .weight(chair.getWeight())
                .averageRating(chair.getAverageRating())
                .sellPrice(chair.getSellPrice())
                .id(chair.getId())
                .description(chair.getDescription())
                .availableAmount(totalAmount - totalReservedAmount)
                .build();
    }
}
