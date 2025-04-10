package com.chairpick.ecommerce.utils.query;

public class With {

    private final SqlQueryBuilder sqlQueryBuilder;

    public With(SqlQueryBuilder sqlQueryBuilder) {
        this.sqlQueryBuilder = sqlQueryBuilder;
    }

    public With with(String alias, QueryResult queryResult) {
        sqlQueryBuilder
                .append(" WITH ")
                .append(alias)
                .append(" AS (")
                .append(queryResult.query().replace(";", ""))
                .append(") ");
        for (String column : queryResult.parameters().keySet()) {
            sqlQueryBuilder.appendWithValue(column, queryResult.parameters().get(column));
        }
        return this;
    }

    public SqlQueryBuilder end() {
        return sqlQueryBuilder;
    }
}
