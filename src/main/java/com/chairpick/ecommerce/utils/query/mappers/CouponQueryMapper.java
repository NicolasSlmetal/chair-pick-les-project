package com.chairpick.ecommerce.utils.query.mappers;

import com.chairpick.ecommerce.model.Coupon;
import com.chairpick.ecommerce.utils.query.QueryResult;
import com.chairpick.ecommerce.utils.query.SelectTable;
import com.chairpick.ecommerce.utils.query.SqlQueryBuilder;
import com.chairpick.ecommerce.utils.query.Where;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.ObjectQueryMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CouponQueryMapper implements ObjectQueryMapper<Coupon> {
    @Override
    public QueryResult parseParameters(Map<String, String> parameters) {
        SelectTable selectTable = SqlQueryBuilder
                .create()
                .selectingColumnsFromTable("tb_coupon", "cpn_id", "cpn_type", "cpn_value", "cpn_customer_id");
        selectTable.join("tb_order_coupon")
                .leftJoinOn("cpn_id", "ocp_coupon_id");

        if (parameters.isEmpty()) {
            return selectTable.endingOptions().build();
        }
        Where where = selectTable.where();
        int size = parameters.size();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equals("customer_id")) {
                where.equals("cpn_customer_id", value);
            }

            if (key.equals("coupon_id")) {
                where.equals("cpn_id", value);
            }

            if (--size > 0) {
                where.and();
            }
        }
        where.and().isNull("ocp_coupon_id");
        return where.end().endingOptions().build();
    }
}
