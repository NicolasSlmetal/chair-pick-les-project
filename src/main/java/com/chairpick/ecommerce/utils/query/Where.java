package com.chairpick.ecommerce.utils.query;

import java.util.HashMap;
import java.util.Map;

public class Where {
    private final SelectTable selectTable;

    public Where(SelectTable selectTable) {
        this.selectTable = selectTable;
    }

    public Where equals(String column, String value) {
        selectTable.append(column)
                .append(" = ")
                .append(" CAST(")
                .append(String.format(":%s", column))
                .append(" AS INTEGER)");
        selectTable.appendValue(column, value);
        return this;
    }

    public Where equalString(String column, String value) {
        selectTable.append(column)
                .append(" = ")
                .append(String.format(":%s", column))
                .append("");
        selectTable.appendValue(column, value);
        return this;
    }

    public Where like(String column, String value) {
        selectTable.append(column)
                .append(" LIKE ")
                .append(String.format(":%s", column))
                .append(" ");
        selectTable.appendValue(column, "%"+value + "%");
        return this;
    }

    public Where ilike(String column, String value) {
        selectTable.append(column)
                .append(" ILIKE ")
                .append(String.format(":%s", column))
                .append(" ");
        selectTable.appendValue(column, "%"+value + "%");
        return this;
    }

    public Where notEquals(String column, String value) {
        long occurrences = selectTable.getSqlQueryBuilder().countParameterOccurrences(column);
        selectTable.append(column)
                .append(" != ")
                .append(String.format("CAST(:%s", column + occurrences))
                .append(" AS INTEGER)")
                .append("");
        selectTable.appendValue(column + occurrences, value);
        return this;
    }

    public Where notEquals(String column, String value, String customType) {
        long occurrences = selectTable.getSqlQueryBuilder().countParameterOccurrences(column);
        selectTable.append(column)
                .append(" != ")
                .append(String.format("CAST(:%s", column + occurrences))
                .append(" AS ")
                .append(customType)
                .append(")")
                .append("");
        selectTable.appendValue(column + occurrences, value);
        return this;
    }

    public Where notEqualsString(String column, String value) {
        long occurrences = selectTable.getSqlQueryBuilder().countParameterOccurrences(column);
        selectTable.append(column)
                .append(" != '")
                .append(String.format(":%s", column + occurrences))
                .append("'");
        selectTable.appendValue(column + occurrences, value);
        return this;
    }

    public Where notLike(String column, String value) {
        long occurrences = selectTable.getSqlQueryBuilder().countParameterOccurrences(column);
        selectTable.append(column)
                .append(" NOT LIKE '%")
                .append(String.format(":%s", column + occurrences))
                .append("%'");
        selectTable.appendValue(column + occurrences, value);
        return this;
    }

    public Where greaterThan(String column, String value) {
        String sanitizedColumn = column.replaceAll("[^A-Za-z]", "");
        long occurrences = selectTable.getSqlQueryBuilder().countParameterOccurrences(sanitizedColumn);
        selectTable.append(column)
                .append(" > ")
                .append(" CAST(")
                .append(String.format(":%s", sanitizedColumn + occurrences))
                .append(" AS INTEGER)")
                .append("");
        selectTable.appendValue(sanitizedColumn + occurrences, value);
        return this;
    }

    public Where greaterThan(String column, String value, String customType) {
        String sanitizedColumn = column.replaceAll("[^A-Za-z]", "");
        long occurrences = selectTable.getSqlQueryBuilder().countParameterOccurrences(sanitizedColumn);
        selectTable.append(column)
                .append(" > ")
                .append(" CAST(")
                .append(String.format(":%s", sanitizedColumn + occurrences))
                .append(" AS ")
                .append(customType)
                .append(")")
                .append("");
        selectTable.appendValue(sanitizedColumn + occurrences, value);
        return this;
    }

    public Where greaterThanOrEquals(String column, String value) {
        long occurrences = selectTable.getSqlQueryBuilder().countParameterOccurrences(column);
        selectTable.append(column)
                .append(" >= ")
                .append(" CAST(")
                .append(String.format(":%s", column + occurrences))
                .append(" AS INTEGER)")
                .append("");
        selectTable.appendValue(column + occurrences, value);
        return this;
    }

    public Where greaterThanOrEquals(String column, String value, String customType) {
        long occurrences = selectTable.getSqlQueryBuilder().countParameterOccurrences(column);
        selectTable.append(column)
                .append(" >= ")
                .append(" CAST(")
                .append(String.format(":%s", column + occurrences))
                .append(" AS ")
                .append(customType)
                .append(")")
                .append("");
        selectTable.appendValue(column + occurrences, value);
        return this;
    }

    public Where lessThan(String column, String value) {
        long occurrences = selectTable.getSqlQueryBuilder().countParameterOccurrences(column);
        selectTable.append(column)
                .append(" < ")
                .append(" CAST(")
                .append(String.format(":%s", column + occurrences))
                .append(" AS INTEGER)")
                .append("");
        selectTable.appendValue(column + occurrences, value);
        return this;
    }

    public Where lessThan(String column, String value, String customType) {
        long occurrences = selectTable.getSqlQueryBuilder().countParameterOccurrences(column);
        selectTable.append(column)
                .append(" < ")
                .append(" CAST(")
                .append(String.format(":%s", column + occurrences))
                .append(" AS ")
                .append(customType)
                .append(")")
                .append("");
        selectTable.appendValue(column + occurrences, value);
        return this;
    }

    public Where lessThanOrEquals(String column, String value) {
        long occurrences = selectTable.getSqlQueryBuilder().countParameterOccurrences(column);
        selectTable.append(column)
                .append(" <= ")
                .append(" CAST(")
                .append(String.format(":%s", column + occurrences))
                .append(" AS INTEGER)")
                .append("");
        selectTable.appendValue(column + occurrences, value);
        return this;
    }

    public Where lessThanOrEquals(String column, String value, String customType) {
        long occurrences = selectTable.getSqlQueryBuilder().countParameterOccurrences(column);
        selectTable.append(column)
                .append(" <= ")
                .append(" CAST(")
                .append(String.format(":%s", column + occurrences))
                .append(" AS ")
                .append(customType)
                .append(")")
                .append("");
        selectTable.appendValue(column + occurrences, value);
        return this;
    }

    public Where in(String column, String... value) {
        selectTable.append(column)
                .append(" IN (");
        StringBuilder subQueryBuilder = new StringBuilder();
        int length = value.length;
        for (String val : value) {
            String identifierValue = column.concat(String.valueOf(length--));
            subQueryBuilder.append(":")
                    .append(column)
                    .append(identifierValue);
            selectTable.appendValue(identifierValue, val);
            if (length > 0) {
                subQueryBuilder.append(", ");
            }
        }
        selectTable.append(subQueryBuilder.toString());
        selectTable.append(")");
        return this;
    }

    public Where notIn(String column, String value) {
        selectTable.append(column)
                .append(" NOT IN (")
                .append(String.format(":%s", column))
                .append(")");
        selectTable.appendValue(column, value);
        return this;
    }

    public Where equalDate(String column, String value) {
        long occurrences = selectTable.getSqlQueryBuilder().countParameterOccurrences(column);
        selectTable.append(column)
                .append(" = ")
                .append("TO_DATE('")
                .append(String.format(":%s", column + occurrences))
                .append("', 'YYYY-MM-DD')");

        selectTable.appendValue(column + occurrences, value);
        return this;
    }

    public Where isNull(String column) {
        selectTable.append(column)
                .append(" IS NULL");
        return this;
    }

    public Where isNotNull(String column) {
        selectTable.append(column)
                .append(" IS NOT NULL");
        return this;
    }

    public Where and() {
        selectTable.append(" AND ");
        return this;
    }

    public Where or() {
        selectTable.append(" OR ");
        return this;
    }

    public SelectTable end() {
        return selectTable;
    }

}
