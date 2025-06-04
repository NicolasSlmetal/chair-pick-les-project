package com.chairpick.ecommerce.utils.query.mappers;

import com.chairpick.ecommerce.model.enums.OrderStatus;
import com.chairpick.ecommerce.projections.OrderReportByCategory;
import com.chairpick.ecommerce.utils.query.QueryResult;
import com.chairpick.ecommerce.utils.query.SelectTable;
import com.chairpick.ecommerce.utils.query.SqlQueryBuilder;
import com.chairpick.ecommerce.utils.query.Where;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.ObjectQueryMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
public class OrderReportByCategoryQueryMapper implements ObjectQueryMapper<OrderReportByCategory> {

    @Override
    public QueryResult parseParameters(Map<String, String> parameters) {
        SelectTable selectTable = SqlQueryBuilder.create()
                .selectingColumnsFromTable("tb_order", "ord_created_date", "SUM(ord_total_value) AS total_value"
                        , "cat_name").join("tb_order_item")
                .innerJoinOn( "ord_id", "ori_order_id")
                .joinDifferentTables("tb_order_item", "tb_item")
                .innerJoinOn("ori_item_id", "itm_id")
                .joinDifferentTables("tb_item", "tb_chair")
                .innerJoinOn("itm_chair_id", "chr_id")
                .joinDifferentTables("tb_chair", "tb_chair_category")
                .innerJoinOn("chr_id", "chc_chair_id")
                .joinDifferentTables("tb_chair_category", "tb_category")
                .innerJoinOn("chc_category_id", "cat_id");

        Where where = selectTable.where();

        String startDate;
        String endDate;
        if (parameters.containsKey("start_date") && parameters.containsKey("end_date")) {
            startDate = parameters.get("start_date");
            endDate = parameters.get("end_date");

        } else {
            startDate = LocalDate.now().minusMonths(1).toString();
            endDate = LocalDate.now().toString();
        }
        where.betweenDate("ord_created_date", startDate, endDate);

        where.and().notEqualsString("ord_status", OrderStatus.PENDING.name());
        where.and().notEqualsString("ord_status", OrderStatus.REPROVED.name());


        return where.end().endingOptions().groupBy("cat_name", "ord_created_date")

                .build();
    }
}
