package com.chairpick.ecommerce.utils.query.mappers;

import com.chairpick.ecommerce.model.Category;
import com.chairpick.ecommerce.utils.query.QueryResult;
import com.chairpick.ecommerce.utils.query.SelectTable;
import com.chairpick.ecommerce.utils.query.SqlQueryBuilder;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.ObjectQueryMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CategoryQueryMapper implements ObjectQueryMapper<Category> {


    @Override
    public QueryResult parseParameters(Map<String, String> parameters) {

        SelectTable selectTable = SqlQueryBuilder.create()
                .selectingAllFromTable("tb_category");

        if (parameters.containsKey("chairId")) {
            String chairId = parameters.get("chairId");
            selectTable.join("tb_chair_category")
                    .innerJoinOn("cat_id", "chc_category_id");
            return selectTable.where().equals("chc_chair_id", chairId)
                    .end().endingOptions().build();
        }

        if (parameters.containsKey("ids")) {
            String[] ids = parameters.get("ids").split(",");
            return selectTable.where().integerIn("cat_id", ids)
                    .end().endingOptions().build();
        }

        return selectTable.endingOptions().build();
    }
}
