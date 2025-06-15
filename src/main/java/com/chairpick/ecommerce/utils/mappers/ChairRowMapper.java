package com.chairpick.ecommerce.utils.mappers;

import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.Item;
import com.chairpick.ecommerce.model.PricingGroup;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ChairRowMapper extends CustomRowMapper<Chair> implements ResultSetExtractor<List<Chair>> {

    public ChairRowMapper() {
        super("chr");
    }

    @Override
    public Chair mapRow(ResultSet rs, int rowNum) throws SQLException {
        return null;
    }

    @Override
    public List<Chair> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Set<Chair> uniqueChairs = new HashSet<>();
        while (rs.next()) {
            PricingGroup pricingGroup = PricingGroup.builder()
                    .id(rs.getLong("pgr_id"))
                    .name(rs.getString("pgr_name"))
                    .percentageValue(rs.getDouble("pgr_percent_value"))
                    .build();
            Chair chair = Chair.builder()
                    .averageRating(rs.getDouble("chr_average_rating"))
                    .name(rs.getString("chr_name"))
                    .sellPrice(rs.getDouble("chr_sell_price"))
                    .description(rs.getString("chr_description"))
                    .height(rs.getDouble("chr_height"))
                    .items(new ArrayList<>())
                    .isActive(rs.getBoolean("chr_active"))
                    .pricingGroup(pricingGroup)
                    .width(rs.getDouble("chr_width"))
                    .length(rs.getDouble("chr_length"))
                    .weight(rs.getDouble("chr_weight"))
                    .build();
            rs.getLong("itm_id");
            if (!rs.wasNull()) {
                Item item = Item.builder()
                        .id(rs.getLong("itm_id"))
                        .entryDate(rs.getDate("itm_entry_date").toLocalDate())
                        .unitCost(rs.getDouble("itm_unit_cost"))
                        .version(rs.getInt("itm_version"))
                        .reservedAmount(rs.getInt("itm_reserved"))
                        .amount(rs.getInt("itm_amount"))
                        .build();
                item.setId(rs.getLong("itm_id"));
                item.setChair(chair);
                if (uniqueChairs.contains(chair)) {
                    uniqueChairs.stream()
                            .filter(c -> c.equals(chair))
                            .forEach(c -> c.getItems().add(item));
                    continue;
                }
                chair.getItems().add(item);
            }


            chair.setId(rs.getLong("chr_id"));
            uniqueChairs.add(chair);

        }
        return uniqueChairs.stream().toList();
    }
}
