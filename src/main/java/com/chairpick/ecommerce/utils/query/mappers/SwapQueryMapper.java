package com.chairpick.ecommerce.utils.query.mappers;

import com.chairpick.ecommerce.model.Swap;
import com.chairpick.ecommerce.utils.query.QueryResult;
import com.chairpick.ecommerce.utils.query.SelectTable;
import com.chairpick.ecommerce.utils.query.SqlQueryBuilder;
import com.chairpick.ecommerce.utils.query.Where;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.ObjectQueryMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SwapQueryMapper implements ObjectQueryMapper<Swap> {

    @Override
    public QueryResult parseParameters(Map<String, String> parameters) {
        SelectTable selectTable = SqlQueryBuilder.create()
                .selectingAllFromTable("tb_item_swap")
                .join("tb_order_item")
                .innerJoinOn("its_order_item_id", "ori_id");
        if (parameters.isEmpty()) {
            return selectTable.endingOptions().build();
        }

        Where where = selectTable.where();
        int size = parameters.size();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equals("order_id")) {
                where.equals("ori_order_id", value);
            }

            if (key.equals("order_item_id")) {
                where.equals("its_order_item_id", value);
            }

            if (key.equals("status")) {
                where.equals("its_status", value);
            }

            if (--size > 0) {
                where.and();
            }

        }

        return selectTable.endingOptions().build();
    }
}
