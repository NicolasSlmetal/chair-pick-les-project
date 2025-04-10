package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.PaginatedProjectionDAO;
import com.chairpick.ecommerce.daos.interfaces.ProjectionDAO;
import com.chairpick.ecommerce.model.Category;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.Item;
import com.chairpick.ecommerce.projections.ChairAvailableProjection;
import com.chairpick.ecommerce.utils.pagination.PageInfo;
import com.chairpick.ecommerce.utils.pagination.PageOptions;
import com.chairpick.ecommerce.utils.query.QueryResult;
import com.chairpick.ecommerce.utils.query.SelectTable;
import com.chairpick.ecommerce.utils.query.SqlQueryBuilder;
import com.chairpick.ecommerce.utils.query.Where;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.GeneralObjectQueryMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ChairDAO implements PaginatedProjectionDAO<Chair, ChairAvailableProjection> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ResultSetExtractor<List<Chair>> extractor;
    private final GeneralObjectQueryMapper<ChairAvailableProjection> projectionQueryMapper;

    public ChairDAO(NamedParameterJdbcTemplate jdbcTemplate, ResultSetExtractor<List<Chair>> extractor, GeneralObjectQueryMapper<ChairAvailableProjection> projectionQueryMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.extractor = extractor;
        this.projectionQueryMapper = projectionQueryMapper;
    }

    @Override
    public Chair save(Chair entity) {
        return null;
    }

    @Override
    public Chair update(Chair entity) {
        return null;
    }

    @Override
    public List<Chair> findAll() {
        String sql = """
                SELECT * FROM tb_chair INNER JOIN tb_item ON chr_id = itm_chair_id;
                """;

        return jdbcTemplate.query(sql, extractor);
    }

    @Override
    public Optional<Chair> findById(Long id) {
        String sql = """
                SELECT * FROM tb_chair INNER JOIN tb_item ON chr_id = itm_chair_id WHERE chr_id = :id;
                """;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);
        List<Chair> chairs = jdbcTemplate.query(sql, parameters, extractor);
        return chairs.isEmpty() ? Optional.empty() : Optional.of(chairs.getFirst());
    }

    @Override
    public List<Chair> findBy(Map<String, String> parameters) {
        QueryResult sql = parseParameters(parameters);
        return jdbcTemplate.query(sql.query(), sql.parameters(), extractor);
    }

    @Override
    public List<ChairAvailableProjection> findAndMapForProjection(Map<String, String> parameters) {
        QueryResult sql = projectionQueryMapper.parseParameters(parameters);
        System.out.println(sql.query());
        System.out.println(sql.parameters());
        return jdbcTemplate.query(sql.query(), sql.parameters(), (rs) -> {
            Set<ChairAvailableProjection> projections = new HashSet<>();
            while (rs.next()) {
                List<Category> categories = new ArrayList<>();
                categories.add(Category.builder()
                        .name(rs.getString("cat_name")).build());

                projections.add(ChairAvailableProjection
                        .builder()
                        .id(rs.getLong("chr_id"))
                        .name(rs.getString("chr_name"))
                        .price(rs.getDouble("chr_sell_price"))
                        .categories(categories)
                        .totalResults(rs.getInt("total_count"))
                        .build()
                );
            }
            return projections.stream().toList();
        });
    }

    private QueryResult parseParameters(Map<String, String> parameters) {
        SqlQueryBuilder builder = SqlQueryBuilder.create();
        SelectTable select = builder.selectingColumnsFromTable("tb_chair",
                "chr_name", "chr_sell_price", "cat_name", "chr_id")
                .join("tb_item")
                .innerJoinOn("chr_id","itm_chair_id")
                .join("tb_chair_category")
                .innerJoinOn("chr_id", "chc_chair_id")
                .joinDifferentTables("tb_chair_category","tb_category")
                .innerJoinOn("chc_category_id", "cat_id");

        if (parameters.isEmpty()) {
            return select.endingOptions().groupBy("chr_id, cat_name")
                    .having().sumHigherThan("itm_amount", "0").end().build();
        }

        Where where = select.where();
        int size = parameters.size();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String column = "chr_" + entry.getKey();

            if (entry.getKey().startsWith("cat_")) {
                column = entry.getKey();
            }

            if(entry.getKey().startsWith("itm_")) {
                column = entry.getKey();
            }

            if (entry.getKey().equals("amount")) {
                column = "itm_" + entry.getKey();
            }

            String value = entry.getValue();

            if (value.matches("0|[1-9]\\d*")) {
                where.equals(column, value);
            } else if (value.matches("(\\d+)-(\\d+)-(\\d+)")) {
                where.equalDate(column, value);
            } else {
                where.ilike(column, value);
            }

            if (--size> 0) {
                where.and();
            }
        }

        return select.endingOptions().groupBy("chr_id, cat_name")
                .having().sumHigherThan("itm_amount", "0").end().build();
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public PageInfo<ChairAvailableProjection> findAndPaginateForProjection(Map<String, String> parameters, PageOptions pageOptions) {
        QueryResult sql = projectionQueryMapper.parseParameters(parameters, pageOptions);

        List<ChairAvailableProjection> paginatedProjections = jdbcTemplate.query(sql.query(), sql.parameters(), (rs) -> {
            Set<ChairAvailableProjection> projections = new HashSet<>();
            while (rs.next()) {
                projections.add(ChairAvailableProjection
                        .builder()
                        .id(rs.getLong("chr_id"))
                        .name(rs.getString("chr_name"))
                        .price(rs.getDouble("chr_sell_price"))
                        .totalResults(rs.getInt("total_count"))
                        .build()
                );
            }
            return projections.stream().toList();
        });

        int total = paginatedProjections == null ||
                paginatedProjections.isEmpty() ? 0 : paginatedProjections.getFirst().getTotalResults();
        return new PageInfo<>(total, paginatedProjections);
    }
}
