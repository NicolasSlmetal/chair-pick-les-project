package com.chairpick.ecommerce.utils.query.mappers;

import com.chairpick.ecommerce.projections.ChairAvailableProjection;
import com.chairpick.ecommerce.utils.pagination.PageOptions;
import com.chairpick.ecommerce.utils.query.*;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.GeneralObjectQueryMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ChairAvailableProjectionQueryMapper implements GeneralObjectQueryMapper<ChairAvailableProjection> {

    @Override
    public QueryResult parseParameters(Map<String, String> parameters) {
        SqlQueryBuilder sqlQueryBuilder = SqlQueryBuilder.create();
        SelectTable selectTable = sqlQueryBuilder
                .selectingColumnsFromTable("tb_chair", "chr_id",
                        "chr_name",
                        "chr_sell_price",
                        "chr_description",
                        "chr_width",
                        "chr_length",
                        "chr_height",
                        "chr_weight",
                        "chr_average_rating",
                        "cat_name",
                        "COUNT(*) AS total_count",
                        "SUM(itm_amount) as total_amount");
        selectTable
                .join("tb_item")
                .innerJoinOn("chr_id", "itm_chair_id")
                .join("tb_chair_category")
                .innerJoinOn("chr_id", "chc_chair_id")
                .joinDifferentTables("tb_chair_category", "tb_category")
                .innerJoinOn("chc_category_id", "cat_id");


        QueryResult with = null;
        if (parameters.isEmpty()) {
            with = selectTable.endingOptions()
                    .groupBy("chr_id", "cat_name")
                    .having()
                    .sumHigherThan("itm_amount", "0")
                    .and()
                    .sumHigherThanOtherColumn("itm_amount", "SUM(itm_reserved)")
                    .end().build();
        }
        Where where = selectTable.where();
        int size = parameters.size();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {

            String value = entry.getValue();
            if (entry.getKey().equals("ids")) {
                String[] ids = entry.getValue().split(",");
                where.integerIn("chr_id", ids);
            }

            if (entry.getKey().equals("active")) {
                where.equals("chr_active", value.equals("true") ? "1" : "0");
            }

            if (--size > 0) {
                where.and();
            }
        }
        if (with == null) {
            with = selectTable
                    .endingOptions()
                    .groupBy("chr_id", "cat_name")
                    .having()
                    .sumHigherThan("itm_amount", "0")
                    .and()
                    .sumHigherThanOtherColumn("itm_amount", "SUM(itm_reserved)")
                    .end().build();
        }


        SqlQueryBuilder nextQueryBuilder = SqlQueryBuilder.create();
        nextQueryBuilder = nextQueryBuilder.withClause().with("sub_query", with).end();
        SelectTable selectWithTable = nextQueryBuilder
                .selectingColumnsFromTable("sub_query",
                        "chr_id",
                        "chr_name",
                        "chr_sell_price",
                        "cat_name",
                        "chr_description",
                        "chr_width",
                        "chr_length",
                        "chr_height",
                        "chr_average_rating",
                        "chr_weight",
                        "total_count",
                        "total_amount");
        selectWithTable.where().greaterThan("total_amount", "0");
        return selectWithTable.endingOptions().build();
    }

    @Override
    public QueryResult parseParameters(Map<String, String> parameters, PageOptions pageOptions) {
        SqlQueryBuilder sqlQueryBuilder = SqlQueryBuilder.create();
        SelectTable selectTable = sqlQueryBuilder
                .selectingColumnsFromTable("tb_chair", "chr_id",
                        "chr_name",
                        "chr_sell_price",
                        "COUNT(*) OVER() AS total_count",
                        "SUM(itm_amount) as total_amount");
        selectTable.join("tb_item")
                .innerJoinOn("chr_id", "itm_chair_id")
                .join("tb_chair_category")
                .innerJoinOn("chr_id", "chc_chair_id")
                .joinDifferentTables("tb_chair_category", "tb_category")
                .innerJoinOn("chc_category_id", "cat_id");
        QueryResult with = null;
        if (parameters.isEmpty()) {
            with = selectTable.endingOptions()
                    .groupBy("chr_id")
                    .having()
                    .sumHigherThan("itm_amount", "0")
                    .and()
                    .sumHigherThanOtherColumn("itm_amount", "SUM(itm_reserved)")
                    .end().build();
        }
        Where where = selectTable.where();
        int size = parameters.size();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (with != null) break;
            String value = entry.getValue();

            if (entry.getKey().equals("name")) {
                where.ilike("chr_name", value);
            }

            if (entry.getKey().equals("min_price")) {
                where.greaterThanOrEquals("chr_sell_price", value, "NUMERIC(7, 2)");
            }

            if (entry.getKey().equals("max_price")) {
                where.lessThanOrEquals("chr_sell_price", value, "NUMERIC(7, 2)");
            }

            if (entry.getKey().equals("min_width")) {
                where.greaterThanOrEquals("chr_width", value, "NUMERIC(7, 2)");
            }

            if (entry.getKey().equals("max_width")) {
                where.lessThanOrEquals("chr_width", value, "NUMERIC(7, 2)");
            }

            if (entry.getKey().equals("min_length")) {
                where.greaterThanOrEquals("chr_length", value, "NUMERIC(7, 2)");
            }

            if (entry.getKey().equals("max_length")) {
                where.lessThanOrEquals("chr_length", value, "NUMERIC(7, 2)");
            }

            if (entry.getKey().equals("min_height")) {
                where.greaterThanOrEquals("chr_height", value, "NUMERIC(7, 2)");
            }

            if (entry.getKey().equals("max_height")) {
                where.lessThanOrEquals("chr_height", value, "NUMERIC(7, 2)");
            }

            if (entry.getKey().equals("min_rating")) {
                where.greaterThanOrEquals("chr_average_rating", value, "NUMERIC(7, 2)");
            }

            if (entry.getKey().equals("max_rating")) {
                where.lessThanOrEquals("chr_average_rating", value, "NUMERIC(7, 2)");
            }

            if (entry.getKey().equals("categories")) {
                String[] categories = value.split(",");
                where.in("cat_name", categories);
            }

            if (entry.getKey().equals("active")) {
                where.equals("chr_active", value.equals("true") ? "1" : "0");
            }

            if (--size > 0) {
                where.and();
            }
        }
        selectTable = where.end();
        if (with == null) {
            Having having = selectTable
                    .endingOptions()
                    .groupBy("chr_id")
                    .having()
                    .sumHigherThan("itm_amount", "0")
                    .and()
                    .sumHigherThanOtherColumn("itm_amount", "SUM(itm_reserved)");
            if (parameters.containsKey("categories")) {
                String[] joinedCategories = parameters.get("categories").split(",");
                having
                .and()
                .countEqual("DISTINCT chc_category_id", String.valueOf(joinedCategories.length));

            }
            with = having.end().build();
        }
        SqlQueryBuilder nextQueryBuilder = SqlQueryBuilder.create();
        SelectTable nextTable = nextQueryBuilder.withClause().with("sub_query", with).end()
                .selectingAllFromTable("sub_query");
        nextTable = nextTable.where().greaterThan("total_amount", "0").end();

        return nextTable
                .endingOptions()
                .groupBy("chr_id", "chr_name", "chr_sell_price", "total_amount", "total_count")
                .limit(pageOptions.getSize())
                .offset(pageOptions)
                .build();
    }
}
