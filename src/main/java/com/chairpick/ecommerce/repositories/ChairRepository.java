package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.daos.interfaces.PaginatedProjectionDAO;
import com.chairpick.ecommerce.daos.interfaces.SemanticDAO;
import com.chairpick.ecommerce.daos.interfaces.WriteRelationDAO;
import com.chairpick.ecommerce.model.*;
import com.chairpick.ecommerce.params.UpsertChairParams;
import com.chairpick.ecommerce.projections.ChairAvailableProjection;
import com.chairpick.ecommerce.projections.SemanticResultProjection;
import com.chairpick.ecommerce.utils.filter.FilterObject;
import com.chairpick.ecommerce.utils.pagination.PageInfo;
import com.chairpick.ecommerce.utils.pagination.PageOptions;
import lombok.Getter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ChairRepository {

    private final PaginatedProjectionDAO<Chair, ChairAvailableProjection> chairDAO;
    private final SemanticDAO<Chair> semanticChairDAO;
    private final WriteRelationDAO<Chair, Category> chairCategoryDAO;
    private final GenericDAO<PriceChangeRequest> priceChangeRequestDAO;
    private final GenericDAO<ChairStatusChange> chairStatusChangeDAO;
    private final GenericDAO<Cart> cartDAO;

    public ChairRepository(PaginatedProjectionDAO<Chair, ChairAvailableProjection> chairDAO, SemanticDAO<Chair> semanticChairDAO, WriteRelationDAO<Chair, Category> chairCategoryDAO, GenericDAO<PriceChangeRequest> priceChangeRequestDAO, GenericDAO<Item> itemDAO, GenericDAO<ChairStatusChange> chairStatusChangeDAO, GenericDAO<Cart> cartDAO) {
        this.chairDAO = chairDAO;
        this.semanticChairDAO = semanticChairDAO;
        this.chairCategoryDAO = chairCategoryDAO;
        this.priceChangeRequestDAO = priceChangeRequestDAO;
        this.chairStatusChangeDAO = chairStatusChangeDAO;
        this.cartDAO = cartDAO;
    }

    public List<Chair> findAllChairs() {
        return chairDAO.findAll();
    }

    public Map<Category, List<ChairAvailableProjection>> findAllChairsAvailableGroupingByCategory() {
        List<ChairAvailableProjection> projections = chairDAO.findAndMapForProjection(Map.of(
                "active", "true"
        ));

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
        parameters.put("active", "true");
        return chairDAO.findAndPaginateForProjection(parameters, pageOptions);
    }

    public Optional<Chair> findById(Long id) {
        return chairDAO.findById(id);
    }

    public List<ChairAvailableProjection> findBySemanticSearch(float[] vector) {
        List<SemanticResultProjection> results = semanticChairDAO.findByVector(vector);

        return getProjectionsByResults(results);
    }

    public List<ChairAvailableProjection> findBySemanticSearch(float[] vector, FilterObject filterObject) {
        List<SemanticResultProjection> results = semanticChairDAO.findByVector(vector, filterObject);

        return getProjectionsByResults(results);
    }

    @Transactional
    public Chair save(UpsertChairParams params) {
        Chair chair = params.chair();
        float[] vector = params.vector();

        Chair savedChair = chairDAO.save(chair);
        semanticChairDAO.upsertVectorWithEntityMetadata(chair, vector);
        return savedChair;
    }

    @Transactional
    public Chair update(UpsertChairParams params) {
        Chair chair = params.chair();
        float[] vector = params.vector();
        List<Category> categoriesToInsert = params.categoriesToInsert();
        List<Category> categoriesToRemove = params.categoriesToRemove();

        Chair updatedChair = chairDAO.update(chair);
        semanticChairDAO.upsertVectorWithEntityMetadata(chair, vector);
        categoriesToInsert.forEach(category -> chairCategoryDAO.insertRelation(updatedChair, category));
        categoriesToRemove.forEach(category -> chairCategoryDAO.deleteRelation(updatedChair, category));
        return updatedChair;
    }

    @Transactional
    public Chair updateWithPriceChangeRequest(UpsertChairParams params) {
        Chair updatedChair = update(params);
        PriceChangeRequest priceChangeRequest = params.priceChangeRequest();
        priceChangeRequestDAO.save(priceChangeRequest);
        return updatedChair;
    }

    @Transactional
    public Chair deactivate(Chair chair, ChairStatusChange chairStatusChange) {
        chairDAO.delete(chair.getId());
        chairStatusChangeDAO.save(chairStatusChange);
        List<Cart> carts = cartDAO.findBy(Map.of("chairId", String.valueOf(chair.getId())));
        carts.forEach(cart -> cartDAO.delete(cart.getId()));
        return chair;
    }

    public Chair activate(Chair chair, ChairStatusChange chairStatusChange) {
        Chair activatedChair = chairDAO.update(chair);
        chairStatusChangeDAO.save(chairStatusChange);
        return activatedChair;
    }

    private List<ChairAvailableProjection> getProjectionsByResults(List<SemanticResultProjection> results) {
        if (results.isEmpty()) {
            return List.of();
        }

        Map<String, String> parameters = Map.of("ids", results.stream()
                .map(SemanticResultProjection::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(",")),
                "active", "true");

        return chairDAO.findAndMapForProjection(parameters);
    }

}
