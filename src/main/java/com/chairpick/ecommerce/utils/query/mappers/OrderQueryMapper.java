package com.chairpick.ecommerce.utils.query.mappers;

import com.chairpick.ecommerce.model.Order;
import com.chairpick.ecommerce.utils.query.*;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.ObjectQueryMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderQueryMapper implements ObjectQueryMapper<Order> {

    @Override
    public QueryResult parseParameters(Map<String, String> parameters) {
        SelectTable selectTable = SqlQueryBuilder
                .create()
                .selectingAllFromTable("tb_order");
        selectTable.join("tb_order_item")
                .innerJoinOn("ord_id", "ori_order_id")
                .joinDifferentTables("tb_order_item", "tb_item")
                .innerJoinOn("ori_item_id", "itm_id")
                .joinDifferentTables("tb_item", "tb_chair")
                .innerJoinOn("itm_chair_id", "chr_id");
        if (parameters.isEmpty()) {
            return selectTable.endingOptions()
                    .orderByDescending("ord_id")
                    .build();
        }
        Where where = selectTable.where();
        int size = parameters.size();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String key = "ord_"+entry.getKey();
            String value = entry.getValue();

            if (key.equals("ord_status")) {
                where.equalString("ord_status", value);
            }

            if (key.equals("ord_customer_id")) {
                where.equals("ord_customer_id", value);
            }

            if (--size > 0) {
                where = where.and();
            }
        }

        EndingOptions endingOptions = where.end().endingOptions().orderByDescending("ord_id");
        return endingOptions.build();
    }
}
