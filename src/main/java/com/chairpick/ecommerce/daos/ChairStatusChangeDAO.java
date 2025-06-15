package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.daos.interfaces.WriteRelationDAO;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.ChairStatusChange;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ChairStatusChangeDAO implements GenericDAO<ChairStatusChange> {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ChairStatusChangeDAO(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public ChairStatusChange save(ChairStatusChange entity) {

        String sql = """
                INSERT INTO tb_chair_status_change (csc_chair_id, csc_status, csc_reason)
                VALUES (:chair_id, :status, :reason)
                """;

        Map<String, Object> parameters = Map.of(
                "chair_id", entity.getChair().getId(),
                "status", entity.isStatus(),
                "reason", entity.getReason()
        );
        return null;
    }

    @Override
    public ChairStatusChange update(ChairStatusChange entity) {
        return null;
    }

    @Override
    public List<ChairStatusChange> findAll() {
        return List.of();
    }

    @Override
    public Optional<ChairStatusChange> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<ChairStatusChange> findBy(Map<String, String> parameters) {
        return List.of();
    }

    @Override
    public void delete(Long id) {

    }
}
