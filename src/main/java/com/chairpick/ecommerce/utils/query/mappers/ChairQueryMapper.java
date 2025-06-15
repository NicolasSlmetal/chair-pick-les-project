package com.chairpick.ecommerce.utils.query.mappers;

import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.utils.query.QueryResult;
import com.chairpick.ecommerce.utils.query.SelectTable;
import com.chairpick.ecommerce.utils.query.SqlQueryBuilder;
import com.chairpick.ecommerce.utils.query.mappers.interfaces.ObjectQueryMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ChairQueryMapper implements ObjectQueryMapper<Chair> {

    @Override
    public QueryResult parseParameters(Map<String, String> parameters) {
        SelectTable selectTable = SqlQueryBuilder.create()
                .selectingAllFromTable("tb_chair")
                .join("tb_item")
                .leftJoinOn("chr_id", "itm_chair_id")
                .join("tb_pricing_group")
                .innerJoinOn("chr_pricing_group_id", "pgr_id");
        return selectTable.endingOptions().orderBy("chr_active").build();
    }
}
