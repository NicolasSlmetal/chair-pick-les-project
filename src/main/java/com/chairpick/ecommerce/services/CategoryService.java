package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.model.Category;
import com.chairpick.ecommerce.projections.OrderReportByCategory;
import com.chairpick.ecommerce.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAllCategories();
    }

    public List<OrderReportByCategory> findOrderReportsByCategory(LocalDate startDate, LocalDate endDate) {
        LocalDate now = LocalDate.now();
        if (startDate == null || startDate.isAfter(now)) {
            startDate = now.minusMonths(1);
        }
        if (endDate == null || endDate.isAfter(now) || endDate.isBefore(startDate)) {
            endDate = now;
        }

        return categoryRepository.findOrderReportsByCategory(startDate, endDate);
    }
}
