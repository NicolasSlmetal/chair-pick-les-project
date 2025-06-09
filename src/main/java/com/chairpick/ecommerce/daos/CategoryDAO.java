package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.ProjectionDAO;
import com.chairpick.ecommerce.model.Category;
import com.chairpick.ecommerce.projections.OrderReportByCategory;
import com.chairpick.ecommerce.utils.query.QueryResult;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.ObjectQueryMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CategoryDAO implements ProjectionDAO<Category, OrderReportByCategory> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectQueryMapper<OrderReportByCategory> projectionQueryMapper;

    public CategoryDAO(NamedParameterJdbcTemplate jdbcTemplate, ObjectQueryMapper<OrderReportByCategory> projectionQueryMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.projectionQueryMapper = projectionQueryMapper;
    }

    @Override
    public List<OrderReportByCategory> findAndMapForProjection(Map<String, String> parameters) {
        QueryResult sql = projectionQueryMapper.parseParameters(parameters);
        return jdbcTemplate.query(sql.query(), sql.parameters(), (rs) -> {
           List<OrderReportByCategory> reports = new ArrayList<>();
           while (rs.next()) {
               OrderReportByCategory report = OrderReportByCategory
                       .builder()
                       .categoryName(rs.getString("cat_name"))
                       .soldValue(rs.getDouble("total_value"))
                       .date(rs.getDate("ord_created_date").toLocalDate())
                       .build();
                reports.add(report);
           }
           return reports;
        });
    }

    @Override
    public Category save(Category entity) {
        return null;
    }

    @Override
    public Category update(Category entity) {
        return null;
    }

    @Override
    public List<Category> findAll() {

        String sql = "SELECT * FROM tb_category";
        return jdbcTemplate.query(sql, (rs) -> {;
            List<Category> categories = new ArrayList<>();
            while (rs.next()) {
                Category category = Category.builder()
                        .id(rs.getLong("cat_id"))
                        .name(rs.getString("cat_name"))
                        .build();
                categories.add(category);
            }
            return categories;
        });
    }

    @Override
    public Optional<Category> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Category> findBy(Map<String, String> parameters) {
        return List.of();
    }

    @Override
    public void delete(Long id) {

    }
}
