package com.chairpick.ecommerce.utils.query.mappers;

import com.chairpick.ecommerce.projections.CustomerRankProjection;
import com.chairpick.ecommerce.utils.query.QueryResult;
import com.chairpick.ecommerce.utils.query.SelectTable;
import com.chairpick.ecommerce.utils.query.SqlQueryBuilder;
import com.chairpick.ecommerce.utils.query.Where;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.ObjectQueryMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomerRankProjectionQueryMapper implements ObjectQueryMapper<CustomerRankProjection> {
    @Override
    public QueryResult parseParameters(Map<String, String> parameters) {
        SelectTable selectTable = SqlQueryBuilder.create()
                .selectingColumnsFromTable("tb_customer",
                        "cus_id",
                        "cus_name",
                        "usr_email",
                        "cus_phone",
                        "cus_phone_ddd",
                        "cus_genre",
                        "cus_active",
                        "cus_phone_type",
                        "usr_id",
                        "usr_password",
                        "usr_type",
                        "cus_cpf",
                        "cus_born_date",
                        "ROW_NUMBER() OVER (ORDER BY COUNT(ord_id) DESC) AS rank");
        selectTable.join("tb_user").innerJoinOn("cus_user_id", "usr_id")
                .join("tb_order")
                .leftJoinOn("cus_id", "ord_customer_id");

        QueryResult queryResult = selectTable.endingOptions().groupBy("cus_id", "usr_email", "usr_id").build();
        if (parameters.isEmpty()) {
            return queryResult;
        }
        SelectTable newSelect = SqlQueryBuilder.create().withClause().with("customer_rank", queryResult)
                .end().selectingAllFromTable("customer_rank");
        Where where = newSelect.where();
        int size = parameters.size();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String value = entry.getValue();

            if (entry.getKey().equals("name")) {
                where.ilike("cus_name", value);
            }

            if (entry.getKey().equals("email")) {
                where.like("usr_email", value);
            }

            if (entry.getKey().equals("phone_ddd")) {
                where.like("cus_phone_ddd", value);
            }

            if (entry.getKey().equals("phone")) {
                where.like("cus_phone", value);
            }

            if (entry.getKey().equals("genre")) {
                where.ilike("cus_genre", value);
            }

            if (entry.getKey().equals("active")) {
                where.equals("cus_active", value.equals("true") ? "1" : "0");
            }

            if (entry.getKey().equals("cpf")) {
                where.like("cus_cpf", value);
            }

            if (entry.getKey().equals("born_date")) {
                where.equalDate("cus_born_date", value);
            }

            if (entry.getKey().equals("id")) {
                where.equals("cus_id", value);
            }

            if (size-- > 1) {
                where.and();
            }
        }
        return where.end().endingOptions().build();
    }
}
