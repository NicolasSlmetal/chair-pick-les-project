package com.chairpick.ecommerce.utils.query;

public class Having {

    private final EndingOptions endingOptions;

    public Having(EndingOptions endingOptions) {
        this.endingOptions = endingOptions;
    }

    public Having and() {
        endingOptions.append(" AND ");
        return this;
    }

    public Having or() {
        endingOptions.append(" OR ");
        return this;
    }

    public Having sumHigherThan(String column, String value) {
        endingOptions.append("SUM(")
                .append(column)
                .append(") > ")
                .append("CAST(")
                .append(String.format(":%s", column))
                .append(" AS INTEGER)");
        endingOptions.appendValue(column, value);
        return this;
    }

    public Having sumHigherThanOtherColumn(String column, String otherColumn) {
        endingOptions.append("SUM(")
                .append(column)
                .append(") > ")
                .append(otherColumn);
        return this;
    }

    public Having sumLowerThan(String column, String value) {
        endingOptions.append("SUM(")
                .append(column)
                .append(") < ")
                .append("CAST(")
                .append(String.format(":%s", column))
                .append(" AS INTEGER)");
        endingOptions.appendValue(column, value);
        return this;
    }

    public Having sumEquals(String column, String value) {
        endingOptions.append("SUM(")
                .append(column)
                .append(") = ")
                .append("CAST(")
                .append(String.format(":%s", column))
                .append(" AS INTEGER)");
        endingOptions.appendValue(column, value);
        return this;
    }

    public EndingOptions end() {
        return endingOptions;
    }

}
