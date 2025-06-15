package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.EntityNotFoundException;
import com.chairpick.ecommerce.exceptions.InvalidRequestException;
import com.chairpick.ecommerce.io.input.ItemInput;
import com.chairpick.ecommerce.io.input.SupplierInput;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.Item;
import com.chairpick.ecommerce.model.PricingGroup;
import com.chairpick.ecommerce.model.Supplier;
import com.chairpick.ecommerce.repositories.ChairRepository;
import com.chairpick.ecommerce.repositories.ItemRepository;
import com.chairpick.ecommerce.repositories.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ChairRepository chairRepository;
    private final SupplierRepository supplierRepository;

    public ItemService(ItemRepository itemRepository, ChairRepository chairRepository, SupplierRepository supplierRepository) {
        this.itemRepository = itemRepository;
        this.chairRepository = chairRepository;
        this.supplierRepository = supplierRepository;
    }


    public Item createNewItem(Long chairId, ItemInput input) {
        Chair chair = chairRepository.findById(chairId)
                .orElseThrow(() -> new IllegalArgumentException("Chair not found with id: " + chairId));
        List<Item> items = chair.getItems();
        PricingGroup pricingGroup = chair.getPricingGroup();

        Supplier supplier = getSupplierFromInput(input);

        Item item = Item.builder()
                .entryDate(input.entryDate())
                .chair(chair)
                .amount(input.amount())
                .unitCost(input.unitCost())
                .reservedAmount(0)
                .version(0)
                .supplier(supplier)
                .build();
        item.validate();

        if (items == null || items.isEmpty()) {
            return saveItemAndUpdateSellPrice(pricingGroup, item, chair);
        }

        Item itemWithHighestUnitCost = items.stream()
                .max(Comparator.comparingDouble(Item::getUnitCost))
                .orElse(item);

        if (itemWithHighestUnitCost.getUnitCost() < item.getUnitCost()) {
            return saveItemAndUpdateSellPrice(pricingGroup, item, chair);
        }

        return itemRepository.save(item);
    }

    private Item saveItemAndUpdateSellPrice(PricingGroup pricingGroup, Item item, Chair chair) {
        double sellPrice = pricingGroup.getPercentageValue() * item.getUnitCost();
        chair.setSellPrice(sellPrice);
        return itemRepository.saveAndUpdateChair(item);
    }

    private Supplier getSupplierFromInput(ItemInput item) {
        SupplierInput input = item.supplier();
        if (input == null) {
            throw new InvalidRequestException("Supplier information is required.");
        }

        if (input.id() == null) {
            return Supplier.builder()
                    .name(input.name())
                    .build();
        }

        return supplierRepository.findById(input.id()).orElseThrow(() ->
            new EntityNotFoundException("Supplier not found with id: " + input.id()));
    }


}
