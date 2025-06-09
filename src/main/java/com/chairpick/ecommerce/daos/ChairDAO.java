package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.PaginatedProjectionDAO;
import com.chairpick.ecommerce.model.Category;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.Item;
import com.chairpick.ecommerce.model.PricingGroup;
import com.chairpick.ecommerce.projections.ChairAvailableProjection;
import com.chairpick.ecommerce.utils.pagination.PageInfo;
import com.chairpick.ecommerce.utils.pagination.PageOptions;
import com.chairpick.ecommerce.utils.query.QueryResult;
import com.chairpick.ecommerce.utils.query.SelectTable;
import com.chairpick.ecommerce.utils.query.SqlQueryBuilder;
import com.chairpick.ecommerce.utils.query.Where;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.GeneralObjectQueryMapper;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.ObjectQueryMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;

public class ChairDAO implements PaginatedProjectionDAO<Chair, ChairAvailableProjection> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ResultSetExtractor<List<Chair>> extractor;
    private final ObjectQueryMapper<Chair> chairQueryMapper;
    private final GeneralObjectQueryMapper<ChairAvailableProjection> projectionQueryMapper;

    public ChairDAO(NamedParameterJdbcTemplate jdbcTemplate, ResultSetExtractor<List<Chair>> extractor, ObjectQueryMapper<Chair> chairQueryMapper, GeneralObjectQueryMapper<ChairAvailableProjection> projectionQueryMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.extractor = extractor;
        this.chairQueryMapper = chairQueryMapper;
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
        QueryResult sql = chairQueryMapper.parseParameters(Collections.emptyMap());
        return jdbcTemplate.query(sql.query(), (rs) -> {
            Set<Chair> chairs = new HashSet<>();
            Set<PricingGroup> pricingGroups = new HashSet<>();
            while (rs.next()) {
                PricingGroup pricingGroup = PricingGroup.builder()
                        .id(rs.getLong("pgr_id"))
                        .name(rs.getString("pgr_name"))
                        .percentageValue(rs.getDouble("pgr_percent_value"))
                        .build();
                if (!pricingGroups.contains(pricingGroup)) {
                    pricingGroups.add(pricingGroup);
                } else {
                    PricingGroup finalPricingGroup = pricingGroup;
                    pricingGroup = pricingGroups.stream()
                            .filter(pg -> pg.equals(finalPricingGroup))
                            .findFirst()
                            .orElseThrow();
                }

                Chair chair = Chair.builder()
                        .id(rs.getLong("chr_id"))
                        .name(rs.getString("chr_name"))
                        .description(rs.getString("chr_description"))
                        .width(rs.getDouble("chr_width"))
                        .isActive(rs.getBoolean("chr_active"))
                        .height(rs.getDouble("chr_height"))
                        .length(rs.getDouble("chr_length"))
                        .weight(rs.getDouble("chr_weight"))
                        .averageRating(rs.getDouble("chr_average_rating"))
                        .sellPrice(rs.getDouble("chr_sell_price"))
                        .pricingGroup(pricingGroup)
                        .build();
                chair.setItems(new ArrayList<>());

                Item item = Item.builder()
                        .id(rs.getLong("itm_id"))
                        .entryDate(rs.getDate("itm_entry_date").toLocalDate())
                        .unitCost(rs.getDouble("itm_unit_cost"))
                        .version(rs.getInt("itm_version"))
                        .reservedAmount(rs.getInt("itm_reserved"))
                        .amount(rs.getInt("itm_amount"))
                        .chair(chair)
                        .build();

                if (!chairs.contains(chair)) {
                    chair.getItems().add(item);
                    chairs.add(chair);
                } else {
                    chairs.stream()
                            .filter(c -> c.equals(chair))
                            .forEach(c -> c.getItems().add(item));
                }


            }

            return chairs.stream().toList();
        });
    }

    @Override
    public Optional<Chair> findById(Long id) {
        String sql = """
                SELECT * FROM tb_chair INNER JOIN tb_item ON chr_id = itm_chair_id WHERE chr_id = :id;
                """;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);
        List<Chair> chairs = jdbcTemplate.query(sql, parameters, extractor);
        return chairs == null || chairs.isEmpty() ? Optional.empty() : Optional.of(chairs.getFirst());
    }

    @Override
    public List<Chair> findBy(Map<String, String> parameters) {
        QueryResult sql = parseParameters(parameters);
        return jdbcTemplate.query(sql.query(), sql.parameters(), extractor);
    }

    @Override
    public List<ChairAvailableProjection> findAndMapForProjection(Map<String, String> parameters) {
        QueryResult sql = projectionQueryMapper.parseParameters(parameters);

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
                        .description(rs.getString("chr_description"))
                        .width(rs.getDouble("chr_width"))
                        .height(rs.getDouble("chr_height"))
                        .length(rs.getDouble("chr_length"))
                        .weight(rs.getDouble("chr_weight"))
                        .averageRating(rs.getDouble("chr_average_rating"))
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
