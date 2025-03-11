package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.model.CreditCard;
import com.chairpick.ecommerce.model.Customer;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CreditCardRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final RowMapper<CreditCard> creditCardRowMapper;

    public CreditCardRepository(NamedParameterJdbcTemplate jdbcTemplate, DataSource dataSource, RowMapper<CreditCard> creditCardRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
        this.creditCardRowMapper = creditCardRowMapper;
    }

    public List<CreditCard> findByCustomer(Customer customer) {
        List<CreditCard> creditCards = jdbcTemplate
                .query("SELECT * FROM tb_credit_card cc INNER JOIN tb_credit_brand cb ON cc.cre_credit_brand = cb.cbr_id WHERE cre_customer_id = :customerId ORDER BY cre_default DESC",
                        Map.of("customerId", customer.getId()),
                        creditCardRowMapper);

        creditCards.forEach(creditCard -> creditCard.setCustomer(customer));
        return creditCards;
    }

    public CreditCard saveCreditCard(CreditCard creditCard) {
        Long creditCardBrandId = jdbcTemplate.queryForObject("SELECT cbr_id FROM tb_credit_brand WHERE cbr_name = :brand", Map.of("brand", creditCard.getBrand().name()), Long.class);

        Long savedCreditCardId = new SimpleJdbcInsert(dataSource)
                .withTableName("tb_credit_card")
                .usingGeneratedKeyColumns("cre_id")
                .executeAndReturnKey(Map.
                        of("cre_number", creditCard.getNumber(),
                                "cre_holder", creditCard.getName(),
                                "cre_cvv", creditCard.getCvv(),
                                "cre_customer_id", creditCard.getCustomer().getId(),
                                "cre_default", creditCard.isDefault() ? 1 : 0,
                                "cre_credit_brand", creditCardBrandId)).longValue();
        creditCard.setId(savedCreditCardId);
        return creditCard;
    }

    public Optional<CreditCard> findDefaultCreditCardByCustomerId(Long customerId) {
        return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM tb_credit_card cc INNER JOIN tb_credit_brand cb ON cc.cre_credit_brand = cb.cbr_id WHERE cre_customer_id = :customerId AND cre_default = 1",
                Map.of("customerId", customerId),
                creditCardRowMapper));
    }

    public Optional<CreditCard> findById(Long creditCardId) {
        return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM tb_credit_card cc INNER JOIN tb_credit_brand cb ON cc.cre_credit_brand = cb.cbr_id WHERE cre_id = :creditCardId",
                Map.of("creditCardId", creditCardId),
                creditCardRowMapper));
    }

    public CreditCard updateCreditCard(CreditCard creditCard) {
        jdbcTemplate.update("UPDATE tb_credit_card SET cre_number = :number, cre_holder = :holder, cre_cvv = :cvv, cre_default = :default WHERE cre_id = :id",
                Map.of("number", creditCard.getNumber(),
                        "holder", creditCard.getName(),
                        "cvv", creditCard.getCvv(),
                        "default", creditCard.isDefault() ? 1 : 0,
                        "id", creditCard.getId()));
        return creditCard;
    }

    public void deleteCreditCard(Long creditCardId) {
        jdbcTemplate.update("DELETE FROM tb_credit_card WHERE cre_id = :creditCardId", Map.of("creditCardId", creditCardId));
    }
}
