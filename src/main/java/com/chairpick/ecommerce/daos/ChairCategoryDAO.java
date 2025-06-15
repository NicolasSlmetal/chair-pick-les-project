package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.WriteRelationDAO;
import com.chairpick.ecommerce.model.Category;
import com.chairpick.ecommerce.model.Chair;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;

public class ChairCategoryDAO implements WriteRelationDAO<Chair, Category> {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ChairCategoryDAO(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insertRelation(Chair leftEntity, Category rightEntity) {
        String sql = "INSERT INTO tb_chair_category (chc_chair_id, chc_category_id) VALUES (:chairId, :categoryId) RETURNING chc_id";

        Map<String, Object> parameters = Map.of(
                "chairId", leftEntity.getId(),
                "categoryId", rightEntity.getId()
        );
        jdbcTemplate.queryForObject(sql, parameters, Long.class);
    }

    @Override
    public void deleteRelation(Chair leftEntity, Category rightEntity) {
        String sql = "DELETE FROM tb_chair_category WHERE chc_chair_id = :chairId AND chc_category_id = :categoryId";
        Map<String, Object> parameters = Map.of(
                "chairId", leftEntity.getId(),
                "categoryId", rightEntity.getId()
        );
        jdbcTemplate.update(sql, parameters);
    }
}
