package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.CreditCard;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CreditCardDAO implements GenericDAO<CreditCard> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<CreditCard> rowMapper;

    public CreditCardDAO(NamedParameterJdbcTemplate jdbcTemplate, RowMapper<CreditCard> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public CreditCard save(CreditCard entity) {
        String sql = """
                SELECT cbr_id FROM tb_credit_brand WHERE cbr_name = :cbr_name;
                """;
        Long brandId = jdbcTemplate.queryForObject(sql, Map.of("cbr_name", entity.getBrand().name()), Long.class);
        sql = """
                INSERT INTO tb_credit_card (cre_number, cre_holder, cre_cvv, cre_customer_id, cre_default, cre_credit_brand)
                VALUES (:cre_number, :cre_holder, :cre_cvv, :cre_customer_id, :cre_default, :cre_credit_brand) RETURNING cre_id
                """;

        Map<String, Object> parameters = Map.of(
                "cre_number", entity.getNumber(),
                "cre_holder", entity.getName(),
                "cre_cvv", entity.getCvv(),
                "cre_customer_id", entity.getCustomer().getId(),
                "cre_default", entity.isDefault() ? 1 : 0,
                "cre_credit_brand", brandId
        );

        Long id = jdbcTemplate.queryForObject(sql, parameters, Long.class);
        entity.setId(id);
        return entity;
    }

    @Override
    public CreditCard update(CreditCard entity) {
        String sql = """
                UPDATE tb_credit_card SET cre_number = :cre_number, cre_holder = :cre_holder, cre_cvv = :cre_cvv, cre_default = :cre_default WHERE cre_id = :id;
                """;
        Map<String, Object> parameters = Map.of(
                "cre_number", entity.getNumber(),
                "cre_holder", entity.getName(),
                "cre_cvv", entity.getCvv(),
                "cre_default", entity.isDefault() ? 1 : 0,
                "id", entity.getId()
        );
        jdbcTemplate.update(sql, parameters);
        return entity;
    }

    @Override
    public List<CreditCard> findAll() {
        String sql = """
                SELECT * from tb_credit_card INNER JOIN tb_credit_brand ON cre_credit_brand = cbr_id;
                """;

        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<CreditCard> findById(Long id) {
        String sql = """
                SELECT * FROM tb_credit_card cc INNER JOIN tb_credit_brand cb ON cc.cre_credit_brand = cb.cbr_id WHERE cre_id = :id;
                """;
        List<CreditCard> creditCards = jdbcTemplate.query(sql, Map.of("id", id), rowMapper);

        return creditCards.isEmpty() ? Optional.empty() : Optional.of(creditCards.getFirst());
    }

    @Override
    public List<CreditCard> findBy(Map<String, String> parameters) {
        String sql = parseParameters(parameters);
        return jdbcTemplate.query(sql, parameters, rowMapper);
    }

    private String parseParameters(Map<String, String> parameters) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM tb_credit_card cc INNER JOIN tb_credit_brand cb ON cc.cre_credit_brand = cb.cbr_id WHERE 1 = 1");
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String operator = " ILIKE ";
            String value = "'%" + entry.getValue() + "%'";
            if (entry.getKey().equalsIgnoreCase("cre_customer_id")) {
                operator = " = ";
                value = entry.getValue();
            }

            if (entry.getKey().equalsIgnoreCase("cre_default")) {
                operator = " = ";
                value = entry.getValue();
            }

            sqlBuilder.append(" AND ").append(entry.getKey()).append(operator).append(value);
        }
        sqlBuilder.append(" ORDER BY cre_default DESC");

        return sqlBuilder.toString();
    }

    @Override
    public void delete(Long id) {
        String sql = """
                DELETE FROM tb_credit_card WHERE cre_id = :id;
                """;
        jdbcTemplate.update(sql, Map.of("id", id));
    }
}
