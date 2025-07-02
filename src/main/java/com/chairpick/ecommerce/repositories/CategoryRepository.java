package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.interfaces.ProjectionDAO;
import com.chairpick.ecommerce.model.Category;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.projections.OrderReportByCategory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class CategoryRepository {

    private final ProjectionDAO<Category, OrderReportByCategory> categoryDAO;

    public CategoryRepository(ProjectionDAO<Category, OrderReportByCategory> categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public List<Category> findAllCategories() {
        return categoryDAO.findAll();
    }

    public List<OrderReportByCategory> findOrderReportsByCategory(LocalDate startDate, LocalDate endDate) {
        return categoryDAO.findAndMapForProjection(Map.of(
                "start_date", startDate.toString(),
                "end_date", endDate.toString()
        ));
    }

    public List<Category> findAllByIds(List<Long> ids) {
        return categoryDAO.findBy(Map.of(
                "ids", String.join(",", ids.stream().map(String::valueOf).toArray(String[]::new))
        ));
    }

    public List<Category> findAllPresentInChair(Chair chair) {
        return categoryDAO.findBy(Map.of(
                "chairId", String.valueOf(chair.getId())
        ));
    }
}
