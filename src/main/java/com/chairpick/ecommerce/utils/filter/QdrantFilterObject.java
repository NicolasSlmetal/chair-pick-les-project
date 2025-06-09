package com.chairpick.ecommerce.utils.filter;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;


public class QdrantFilterObject extends FilterObject {

    Map<String, Function<ValueFilter, QdrantFilterParam.QdrantFilterRange>> filterRangeMap = Map.of(
            "=", filter -> QdrantFilterParam.QdrantFilterRange.builder()
                    .eq(Double.parseDouble(filter.getValue().toString()))
                    .build(),
            ">=", filter -> QdrantFilterParam.QdrantFilterRange.builder()
                    .gte(Double.parseDouble(filter.getValue().toString()))
                    .build(),
            "<=", filter -> QdrantFilterParam.QdrantFilterRange.builder()
                    .lte(Double.parseDouble(filter.getValue().toString()))
                    .build(),
            ">", filter -> QdrantFilterParam.QdrantFilterRange.builder()
                    .gt(Double.parseDouble(filter.getValue().toString()))
                    .build(),
            "<", filter -> QdrantFilterParam.QdrantFilterRange.builder()
                    .lt(Double.parseDouble(filter.getValue().toString()))
                    .build(),
            "between", filter -> {
                String[] values = filter.getValue().toString().split(",");
                return QdrantFilterParam.QdrantFilterRange.builder()
                        .gte(Double.parseDouble(values[0].trim()))
                        .lte(Double.parseDouble(values[1].trim()))
                        .build();
            }
    );

    @Builder
    @Getter
    public static class QdrantFilterParam {

        @Builder
        @Getter
        public static class QdrantFilterRange {
            private Double gte;
            private Double lte;
            private Double gt;
            private Double lt;
            private Double eq;
        }

        private String key;
        private QdrantFilterRange range;
    }

    public QdrantFilterObject(List<ValueFilter> filters) {
        super(filters);
    }

    @Override
    public List<QdrantFilterParam> toObjectFilter() {

        return getFilters().stream()
                .map(
                        filter -> {
                            QdrantFilterParam.QdrantFilterRange range = filterRangeMap.get(filter.getOperator()).apply(filter);
                            return QdrantFilterParam.builder()
                                    .key(filter.getField())
                                    .range(range)
                                    .build();
                        }
                )
                .toList();
    }

    @Override
    public boolean hasUndefinedFilters() {
        return getFilters().stream()
                .anyMatch(filter ->
                        filter.getField() == null
                        || filter.getOperator() == null
                        || filter.getValue() == null);
    }
}
