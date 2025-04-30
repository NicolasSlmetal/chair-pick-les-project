package com.chairpick.ecommerce.utils.query.mappers;

import com.chairpick.ecommerce.model.OrderItem;
import com.chairpick.ecommerce.utils.pagination.PageOptions;
import com.chairpick.ecommerce.utils.query.QueryResult;
import com.chairpick.ecommerce.utils.query.SelectTable;
import com.chairpick.ecommerce.utils.query.SqlQueryBuilder;
import com.chairpick.ecommerce.utils.query.Where;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.GeneralObjectQueryMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderItemQueryMapper implements GeneralObjectQueryMapper<OrderItem> {

    @Override
    public QueryResult parseParameters(Map<String, String> parameters) {
        SelectTable selectTable = SqlQueryBuilder
                .create()
                .selectingAllFromTable("tb_order_item");
        selectTable.join("tb_item")
                .innerJoinOn("ori_item_id", "itm_id")
                .join("tb_item_swap")
                .leftJoinOn("ori_id", "its_order_item_id")
                .joinDifferentTables("tb_item", "tb_chair")
                .innerJoinOn("itm_chair_id", "chr_id");
        if (parameters.isEmpty()) {
            return selectTable.endingOptions()
                    .orderByDescending("ori_id")
                    .build();
        }
        Where where = selectTable.where();
        int size = parameters.size();

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String key = "ori_"+entry.getKey();
            String value = entry.getValue();
            System.out.println(entry);
            if (key.equals("ori_status")) {
                where.equalString("ori_status", value);
            }

            if (key.equals("ori_order_id")) {
                where.equals("ori_order_id", value);
            }

            if (--size > 0) {
                where.and();
            }
        }

        return where.end().endingOptions().build();
    }

    @Override
    public QueryResult parseParameters(Map<String, String> parameters, PageOptions pageOptions) {
        return null;
    }
}
