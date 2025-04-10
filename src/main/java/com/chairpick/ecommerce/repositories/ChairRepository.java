package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.daos.interfaces.PaginatedProjectionDAO;
import com.chairpick.ecommerce.daos.interfaces.ProjectionDAO;
import com.chairpick.ecommerce.io.output.ChairDTO;
import com.chairpick.ecommerce.model.Category;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.Item;
import com.chairpick.ecommerce.projections.ChairAvailableProjection;
import com.chairpick.ecommerce.utils.pagination.PageInfo;
import com.chairpick.ecommerce.utils.pagination.PageOptions;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ChairRepository {

    private final PaginatedProjectionDAO<Chair, ChairAvailableProjection> chairDAO;
    private final GenericDAO<Item> itemDAO;

    public ChairRepository(PaginatedProjectionDAO<Chair, ChairAvailableProjection> chairDAO, GenericDAO<Item> itemDAO) {
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

    public PageInfo<ChairAvailableProjection> searchForPaginatedChairs(Map<String, String> parameters) {
        PageOptions pageOptions = PageOptions
                .builder()
                .page(Integer.parseInt(parameters.get("page")))
                .size(Integer.parseInt(parameters.get("limit")))
                .build();
        parameters.remove("page");
        parameters.remove("limit");
        return chairDAO.findAndPaginateForProjection(parameters, pageOptions);
    }

    public Optional<Chair> findById(Long id) {
        return chairDAO.findById(id);
    }

}
