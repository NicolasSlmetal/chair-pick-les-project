package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.daos.interfaces.ProjectionDAO;
import com.chairpick.ecommerce.model.Category;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.Item;
import com.chairpick.ecommerce.projections.ChairAvailableProjection;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ChairRepository {

    private final ProjectionDAO<Chair, ChairAvailableProjection> chairDAO;
    private final GenericDAO<Item> itemDAO;

    public ChairRepository(ProjectionDAO<Chair, ChairAvailableProjection> chairDAO, GenericDAO<Item> itemDAO) {
        this.chairDAO = chairDAO;
        this.itemDAO = itemDAO;
    }

    public Map<Category, List<ChairAvailableProjection>> findAllChairsAvailableGroupingByCategory() {
        List<ChairAvailableProjection> projections = chairDAO.findAndMapForProjection(Map.of());

        return projections.stream()
                .flatMap(projection -> projection.getCategories()
                        .stream()
                        .map(category -> Map.entry(category, projection))
                )
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
    }

    public Optional<Chair> findById(Long id) {
        return chairDAO.findById(id);
    }

}
