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
        String sql = """
                INSERT INTO tb_chair (chr_name, chr_description, chr_width, chr_height, chr_length, chr_weight, chr_average_rating, chr_sell_price, chr_active, chr_pricing_group_id)
                VALUES (:name, :description, :width, :height, :length, :weight, :averageRating, :sellPrice, :isActive, :pricingGroupId)
                RETURNING chr_id;
                """;
        Map<String, Object> parameters = getParameters(entity);

        Long id = jdbcTemplate.queryForObject(sql, parameters, Long.class);
        if (id != null) {
            entity.setId(id);
            entity.getCategories()
                    .forEach(category -> {
                        String sqlCategory = """
                                INSERT INTO tb_chair_category (chc_chair_id, chc_category_id)
                                VALUES (:chairId, :categoryId);
                                """;
                        Map<String, Object> categoryParameters = new HashMap<>();

                        categoryParameters.put("chairId", entity.getId());
                        categoryParameters.put("categoryId", category.getId());
                        jdbcTemplate.update(sqlCategory, categoryParameters);
                    });
        }
        return entity;
    }

    private static Map<String, Object> getParameters(Chair entity) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", entity.getName());
        parameters.put("description", entity.getDescription());
        parameters.put("width", entity.getWidth());
        parameters.put("height", entity.getHeight());
        parameters.put("length", entity.getLength());
        parameters.put("weight", entity.getWeight());
        parameters.put("averageRating", entity.getAverageRating());
        parameters.put("sellPrice", entity.getSellPrice());
        parameters.put("isActive", entity.isActive() ? 1 : 0);
        parameters.put("pricingGroupId", entity.getPricingGroup().getId());
        return parameters;
    }

    @Override
    public Chair update(Chair entity) {
        String sql = """
                UPDATE tb_chair SET chr_name = :name, chr_description = :description, chr_width = :width,
                chr_height = :height, chr_length = :length, chr_weight = :weight, chr_average_rating = :averageRating,
                chr_sell_price = :sellPrice, chr_active = :isActive, chr_pricing_group_id = :pricingGroupId
                WHERE chr_id = :id;
                """;
        Map<String, Object> parameters = getParameters(entity);
        parameters.put("id", entity.getId());
        jdbcTemplate.update(sql, parameters);
        return entity;
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

                rs.getLong("itm_id");
                if (!rs.wasNull()) {
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
                } else {
                    chairs.add(chair);
                }

            }

            return chairs.stream().toList();
        });
    }

    @Override
    public Optional<Chair> findById(Long id) {
        String sql = """
                SELECT * FROM tb_chair LEFT JOIN tb_item ON chr_id = itm_chair_id
                 INNER JOIN tb_pricing_group pg ON chr_pricing_group_id = pg.pgr_id WHERE chr_id = :id;
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
        String sql = "UPDATE tb_chair SET chr_active = 0 WHERE chr_id = :id;";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);

        jdbcTemplate.update(sql, parameters);
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
