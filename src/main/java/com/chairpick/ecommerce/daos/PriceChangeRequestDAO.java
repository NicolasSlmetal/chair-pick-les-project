package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.PriceChangeRequest;
import com.chairpick.ecommerce.model.enums.PriceChangeRequestStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PriceChangeRequestDAO implements GenericDAO<PriceChangeRequest> {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PriceChangeRequestDAO(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PriceChangeRequest save(PriceChangeRequest entity) {
        String sql = "INSERT INTO tb_price_change_request (pcr_chair_id, pcr_requested_price, pcr_reason, pcr_status) VALUES (:chairId, :requestedPrice, :reason, :status) RETURNING pcr_id";

        Map<String, Object> parameters = Map.of(
            "chairId", entity.getChair().getId(),
            "requestedPrice", entity.getRequestedPrice(),
            "reason", entity.getReason(),
            "status", entity.getStatus().name()
        );

        Long id = jdbcTemplate.queryForObject(sql, parameters, Long.class);
        entity.setId(id);
        return entity;
    }

    @Override
    public PriceChangeRequest update(PriceChangeRequest entity) {
        String sql = "UPDATE tb_price_change_request SET pcr_requested_price = :requestedPrice, pcr_reason = :reason, pcr_status = :status WHERE pcr_id = :id";
        Map<String, Object> parameters = Map.of(
            "requestedPrice", entity.getRequestedPrice(),
            "reason", entity.getReason(),
            "status", entity.getStatus().name(),
            "id", entity.getId()
        );
        jdbcTemplate.update(sql, parameters);
        return entity;
    }

    @Override
    public List<PriceChangeRequest> findAll() {
        return List.of();
    }

    @Override
    public Optional<PriceChangeRequest> findById(Long id) {

        String sql = "SELECT * FROM tb_price_change_request WHERE pcr_id = :id";

        Map<String, Object> params = Map.of("id", id);
        List<PriceChangeRequest> requests = jdbcTemplate.query(sql, params, (rs, rowNum) ->
             PriceChangeRequest.builder()
                    .id(rs.getLong("pcr_id"))
                    .requestedPrice(rs.getDouble("pcr_requested_price"))
                    .reason(rs.getString("pcr_reason"))
                    .status(PriceChangeRequestStatus.valueOf(rs.getString("pcr_status")))
                    .build()
        );
        return requests.isEmpty() ? Optional.empty() : Optional.of(requests.getFirst());
    }

    @Override
    public List<PriceChangeRequest> findBy(Map<String, String> parameters) {
        String sql = "SELECT * FROM tb_price_change_request WHERE pcr_chair_id = :chairId";
        Long chairId = Long.valueOf(parameters.get("chairId"));
        Map<String, Object> namedParameters = Map.of("chairId", chairId);
        return jdbcTemplate.query(sql, namedParameters, (rs, rowNum) -> {;
            return PriceChangeRequest.builder()
                    .requestedPrice(rs.getDouble("pcr_requested_price"))
                    .reason(rs.getString("pcr_reason"))
                    .status(PriceChangeRequestStatus.valueOf(rs.getString("pcr_status")))
                    .id(rs.getLong("pcr_id"))
                    .build();
        });
    }

    @Override
    public void delete(Long id) {

    }
}
