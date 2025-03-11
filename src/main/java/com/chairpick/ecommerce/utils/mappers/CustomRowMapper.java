package com.chairpick.ecommerce.utils.mappers;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class CustomRowMapper {

    private final String tableTrigram;

    public CustomRowMapper(String tableTrigram) {
        this.tableTrigram = tableTrigram;
    }

    public String getColumn(String column) {
        List<String> splitCompleteColumnName = new ArrayList<>(List.of(tableTrigram));
        splitCompleteColumnName.addAll(Arrays.asList(column.split(" ")));
        return String.join("_", splitCompleteColumnName);
    }

    public String getRelatedTableColumn(String column, String tableTrigram) {
        List<String> splitCompleteColumnName = new ArrayList<>(List.of(tableTrigram));
        splitCompleteColumnName.addAll(Arrays.asList(column.split(" ")));
        return String.join("_", splitCompleteColumnName);
    }
}
