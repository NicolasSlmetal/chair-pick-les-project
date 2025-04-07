package com.chairpick.ecommerce.utils.query;

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

    public Where like(String column, String value) {
        selectTable.append(column)
                .append(" LIKE '%")
                .append(String.format(":%s", column))
                .append("%'");
        return this;
    }

    public Where ilike(String column, String value) {
        selectTable.append(column)
                .append(" ILIKE '%")
                .append(String.format(":%s", column))
                .append("%'");
        return this;
    }

    public Where notEquals(String column, String value) {
        selectTable.append(column)
                .append(" != ")
                .append(String.format(":%s", column))
                .append("");
        return this;
    }

    public Where notEqualsString(String column, String value) {
        selectTable.append(column)
                .append(" != '")
                .append(String.format(":%s", column))
                .append("'");
        selectTable.appendValue(column, value);
        return this;
    }

    public Where notLike(String column, String value) {
        selectTable.append(column)
                .append(" NOT LIKE '%")
                .append(String.format(":%s", column))
                .append("%'");
        return this;
    }

    public Where greaterThan(String column, String value) {
        String sanitizedColumn = column.replaceAll("[^A-Za-z]", "");
        selectTable.append(column)
                .append(" > ")
                .append(" CAST(")
                .append(String.format(":%s", sanitizedColumn))
                .append(" AS INTEGER)")
                .append("");
        selectTable.appendValue(sanitizedColumn, value);
        return this;
    }

    public Where greaterThanOrEquals(String column, String value) {
        selectTable.append(column)
                .append(" >= ")
                .append(" CAST(")
                .append(String.format(":%s", column))
                .append(" AS INTEGER)")
                .append("");
        selectTable.appendValue(column, value);
        return this;
    }

    public Where lessThan(String column, String value) {
        selectTable.append(column)
                .append(" < ")
                .append(" CAST(")
                .append(String.format(":%s", column))
                .append(" AS INTEGER)")
                .append("");
        selectTable.appendValue(column, value);
        return this;
    }

    public Where lessThanOrEquals(String column, String value) {
        selectTable.append(column)
                .append(" <= ")
                .append(" CAST(")
                .append(String.format(":%s", column))
                .append(" AS INTEGER)")
                .append("");
        selectTable.appendValue(column, value);
        return this;
    }

    public Where in(String column, String value) {
        selectTable.append(column)
                .append(" IN (")
                .append(String.format(":%s", column))
                .append(")");
        selectTable.appendValue(column, value);
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
        selectTable.append(column)
                .append(" = ")
                .append("TO_DATE('")
                .append(String.format(":%s", column))
                .append("', 'YYYY-MM-DD')");

        selectTable.appendValue(column, value);
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
