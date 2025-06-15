package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.PricingGroup;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PricingGroupDAO implements GenericDAO<PricingGroup> {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public PricingGroupDAO(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PricingGroup save(PricingGroup entity) {
        // Implementation for saving a PricingGroup
        return null;
    }

    @Override
    public PricingGroup update(PricingGroup entity) {
        // Implementation for updating a PricingGroup
        return null;
    }

    @Override
    public List<PricingGroup> findAll() {
        String sql = "SELECT * FROM tb_pricing_group";

        return jdbcTemplate.query(sql, (rs, rowNum) -> PricingGroup
                .builder()
                .id(rs.getLong("pgr_id"))
                .name(rs.getString("pgr_name"))
                .percentageValue(rs.getDouble("pgr_percent_value"))
                .build());
    }

    @Override
    public Optional<PricingGroup> findById(Long id) {
        String sql = "SELECT * FROM tb_pricing_group WHERE pgr_id = :id";
        Map<String, Object> params = Map.of("id", id);
        List<PricingGroup> pricingGroups = jdbcTemplate.query(sql, params, (rs, rowNum) -> PricingGroup
                .builder()
                .id(rs.getLong("pgr_id"))
                .name(rs.getString("pgr_name"))
                .percentageValue(rs.getDouble("pgr_percent_value"))
                .build());

        return pricingGroups.isEmpty() ? Optional.empty() : Optional.of(pricingGroups.getFirst());
    }

    @Override
    public List<PricingGroup> findBy(Map<String, String> parameters) {
        return List.of();
    }



    @Override
    public void delete(Long id) {

    }
}
