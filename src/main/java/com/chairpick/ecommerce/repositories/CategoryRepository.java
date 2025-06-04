package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.interfaces.ProjectionDAO;
import com.chairpick.ecommerce.model.Category;
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

    public List<OrderReportByCategory> findOrderReportsByCategory(LocalDate startDate, LocalDate endDate) {
        return categoryDAO.findAndMapForProjection(Map.of(
                "startDate", startDate.toString(),
                "endDate", endDate.toString()
        ));
    }
}
