package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.EntityNotFoundException;
import com.chairpick.ecommerce.io.input.ChairInput;
import com.chairpick.ecommerce.io.input.ChairStatusChangeInput;
import com.chairpick.ecommerce.io.input.UpdateChairInput;
import com.chairpick.ecommerce.io.output.CompleteChairDTO;
import com.chairpick.ecommerce.model.*;
import com.chairpick.ecommerce.io.output.AvailableChairDTO;
import com.chairpick.ecommerce.model.enums.PriceChangeRequestStatus;
import com.chairpick.ecommerce.params.UpsertChairParams;
import com.chairpick.ecommerce.projections.ChairAvailableProjection;
import com.chairpick.ecommerce.io.output.ChairDTO;
import com.chairpick.ecommerce.repositories.*;
import com.chairpick.ecommerce.utils.pagination.PageInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChairService {

    private final ChairRepository chairRepository;
    private final PricingGroupRepository pricingGroupRepository;
    private final CategoryRepository categoryRepository;
    private final EmbeddingService embeddingService;

    public ChairService(ChairRepository chairRepository, PricingGroupRepository pricingGroupRepository, ItemRepository itemRepository, CategoryRepository categoryRepository, EmbeddingService embeddingService) {
        this.chairRepository = chairRepository;
        this.pricingGroupRepository = pricingGroupRepository;
        this.categoryRepository = categoryRepository;
        this.embeddingService = embeddingService;
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
                        .dimensions(String.format("%s cm x %s cm x %s cm", chair.getLength(), chair.getWidth(), chair.getHeight()))
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

    public Chair findChairByIdToEdit(Long id) {
        Chair chair = chairRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chair not found with id: " + id));
        List<Category> chairCategories = categoryRepository.findAllPresentInChair(chair);
        chair.setCategories(chairCategories);

        return chair;
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

    public Chair save(ChairInput input) {

        List<Category> categories = categoryRepository.findAllByIds(input.categories());

        if (categories.size() != input.categories().size()) {
            throw new EntityNotFoundException("One or more categories not found");
        }

        PricingGroup pricingGroup = pricingGroupRepository
                .findById(input.pricingGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Pricing group not found with id: " + input.pricingGroupId()));
        Chair chair = Chair.builder()
                .name(input.name())
                .description(input.description())
                .width(input.width())
                .height(input.height())
                .length(input.length())
                .weight(input.weight())
                .averageRating(input.averageRating())
                .pricingGroup(pricingGroup)
                .categories(categories)
                .isActive(true)
                .build();
        chair.validate();
        float[] embedding = embeddingService.generateEmbeddingForChair(chair);
        UpsertChairParams params = new UpsertChairParams(chair, embedding, Collections.emptyList(), Collections.emptyList(), null);

        return chairRepository.save(params);
    }

    public Chair update(Long id, UpdateChairInput input) {
        Chair chair = chairRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chair not found with id: " + id));
        List<Category>  categories = categoryRepository.findAllPresentInChair(chair);
        updateFields(input, chair);

        List<Category> categoriesToInsert = new ArrayList<>();
        List<Category> categoriesToRemove = new ArrayList<>();
        if (input.categories() != null) {
            List<Long> existingCategoryIds = categories.stream()
                    .map(Category::getId)
                    .toList();

            List<Long> inputCategoryIds = input.categories();

            Set<Long> toInsertIds = new HashSet<>(inputCategoryIds);
            existingCategoryIds.forEach(toInsertIds::remove);

            Set<Long> toRemoveIds = new HashSet<>(existingCategoryIds);
            inputCategoryIds.forEach(toRemoveIds::remove);

            if (!toInsertIds.isEmpty()) {
                List<Category> toInsert = categoryRepository.findAllByIds(toInsertIds.stream().toList());
                categoriesToInsert.addAll(toInsert);
            }

            categories.stream()
                    .filter(cat -> toRemoveIds.contains(cat.getId()))
                    .forEach(categoriesToRemove::add);


            List<Category> updatedCategories = new ArrayList<>(categories);
            updatedCategories.removeAll(categoriesToRemove);
            updatedCategories.addAll(categoriesToInsert);
            chair.setCategories(updatedCategories);
        }

        chair.validate();
        float[] embedding = embeddingService.generateEmbeddingForChair(chair);

        if (isPriceChangeUpdate(input)) {
            PriceChangeRequest priceChangeRequest = PriceChangeRequest.builder()
                    .requestedPrice(input.price())
                    .reason(input.reason())
                    .chair(chair)
                    .status(PriceChangeRequestStatus.PENDING)
                    .build();
            priceChangeRequest.validate();
            UpsertChairParams params = new UpsertChairParams(chair, embedding, categoriesToInsert, categoriesToRemove, priceChangeRequest);
            return chairRepository.updateWithPriceChangeRequest(params);
        }

        UpsertChairParams params = new UpsertChairParams(chair, embedding, categoriesToInsert, categoriesToRemove, null);
        return chairRepository.update(params);
    }

    public Chair changeChairStatus(Long chairId, ChairStatusChangeInput input) {
        Chair chair = chairRepository
                .findById(chairId)
                .orElseThrow(() -> new EntityNotFoundException("Chair not found with id: " + chairId));

        if (input.active() != chair.isActive()) {
            chair.setActive(input.active());

            ChairStatusChange chairStatusChange = ChairStatusChange
                    .builder()
                    .chair(chair)
                    .status(input.active())
                    .reason(input.reason())
                    .build();
            chairStatusChange.validate();

            if (!chairStatusChange.isStatus()) {

                return chairRepository.deactivate(chair, chairStatusChange);

            }

            return chairRepository.activate(chair, chairStatusChange);
        }

        return chair;
    }

    private static boolean isPriceChangeUpdate(UpdateChairInput input) {
        return input.reason() != null && !input.reason().isBlank() && input.price() != null && input.price() > 0;
    }

    private void updateFields(UpdateChairInput input, Chair chair) {
        if (input.name() != null) {
            chair.setName(input.name());
        }
        if (input.description() != null) {
            chair.setDescription(input.description());
        }
        if (input.width() != null) {
            chair.setWidth(input.width());
        }
        if (input.height() != null) {
            chair.setHeight(input.height());
        }
        if (input.length() != null) {
            chair.setLength(input.length());
        }
        if (input.weight() != null) {
            chair.setWeight(input.weight());
        }
        if (input.averageRating() != null) {
            chair.setAverageRating(input.averageRating());
        }
        if (input.pricingGroupId() != null) {
            updatePricingGroup(input, chair);
        }
    }

    private void updatePricingGroup(UpdateChairInput input, Chair chair) {
        PricingGroup chairPricingGroup = chair.getPricingGroup();

        if (chairPricingGroup != null && !input.pricingGroupId().equals(chairPricingGroup.getId())) {
            PricingGroup pricingGroup = pricingGroupRepository
                    .findById(input.pricingGroupId())
                    .orElseThrow(() -> new EntityNotFoundException("Pricing group not found with id: " + input.pricingGroupId()));
            chair.setPricingGroup(pricingGroup);
            Item itemWithHighestUnitCost = chair.getItems()
                    .stream()
                    .max(Comparator.comparingDouble(Item::getUnitCost))
                    .orElse(null);
            if (itemWithHighestUnitCost != null) {
                double sellPrice = pricingGroup.getPercentageValue() * itemWithHighestUnitCost.getUnitCost();
                chair.setSellPrice(sellPrice);
            }
        }
    }
}
