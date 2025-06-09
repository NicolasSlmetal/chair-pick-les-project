package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.EntityNotFoundException;
import com.chairpick.ecommerce.io.output.CompleteChairDTO;
import com.chairpick.ecommerce.model.Category;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.Item;
import com.chairpick.ecommerce.io.output.AvailableChairDTO;
import com.chairpick.ecommerce.projections.ChairAvailableProjection;
import com.chairpick.ecommerce.io.output.ChairDTO;
import com.chairpick.ecommerce.repositories.ChairRepository;
import com.chairpick.ecommerce.utils.pagination.PageInfo;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChairService {

    private final ChairRepository chairRepository;

    public ChairService(ChairRepository chairRepository) {
        this.chairRepository = chairRepository;
    }

    public List<CompleteChairDTO> findAllChairs() {
        List<Chair> chairs = chairRepository.findAllChairs();
        Map<Long, List<Item>> groupedItems = chairs.stream().collect(Collectors.toMap(Chair::getId, Chair::getItems));

        Map<Long, Double> mapMaxCosts = groupedItems.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toMap(
                        item -> item.getChair().getId(),
                        Item::getUnitCost,
                        Double::max
                ));

        Map<Long, Integer> mapStockAmounts = groupedItems.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toMap(
                        item -> item.getChair().getId(),
                        Item::getAmount,
                        Integer::sum
                ));

        Map<Long, LocalDate> mapLastEntryInStock = groupedItems.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toMap(
                        item -> item.getChair().getId(),
                        Item::getEntryDate,
                        (date1, date2) -> date1.isAfter(date2) ? date1 : date2
                ));
        return chairs.stream().map(
                chair -> CompleteChairDTO
                        .builder()
                        .name(chair.getName())
                        .isActive(chair.isActive())
                        .dimensions(String.format("%s cm x %s cm x %s cm", chair.getWidth(), chair.getHeight(), chair.getLength()))
                        .cost(mapMaxCosts.getOrDefault(chair.getId(), 0.0))
                        .pricingGroupName(chair.getPricingGroup().getName())
                        .stockAmount(mapStockAmounts.getOrDefault(chair.getId(), 0))
                        .averageRating(chair.getAverageRating())
                        .lastEntryDate(mapLastEntryInStock.get(chair.getId()))
                        .id(chair.getId())
                        .sellPrice(chair.getSellPrice())
                        .weight(chair.getWeight())
                        .build()
        ).toList();
    }

    public AvailableChairDTO findAllChairsAvailableGroupingByCategory() {
        Map<Category, List<ChairAvailableProjection>> chairsByCategory = chairRepository
                .findAllChairsAvailableGroupingByCategory();
        return new AvailableChairDTO(chairsByCategory, chairsByCategory
                .values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet())
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

    public PageInfo<ChairAvailableProjection> searchForChairs(Map<String, String> parameters) {
        if (!parameters.containsKey("limit")) {
            parameters.put("limit", "5");

        }
        if (!parameters.containsKey("page")) {
            parameters.put("page", "1");
        }

        return chairRepository.searchForPaginatedChairs(parameters);
    }
}
