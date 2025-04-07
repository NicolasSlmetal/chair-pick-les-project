package com.chairpick.ecommerce.utils.query;

import com.chairpick.ecommerce.utils.pagination.PageOptions;

import java.util.Arrays;

public class EndingOptions {
    private final SqlQueryBuilder sqlQueryBuilder;

    public EndingOptions(SqlQueryBuilder sqlQueryBuilder) {
        this.sqlQueryBuilder = sqlQueryBuilder;
    }

    public EndingOptions orderBy(String column) {
        sqlQueryBuilder
                .append(" ORDER BY ")
                .append(column);
        return this;
    }

    public EndingOptions orderByDescending(String column) {
        sqlQueryBuilder
                .append(" ORDER BY ")
                .append(column)
                .append(" DESC");
        return this;
    }

    public EndingOptions limit(int limit) {
        sqlQueryBuilder
                .append(" LIMIT ")
                .append(Integer.toString(limit));
        return this;
    }

    public EndingOptions offset(PageOptions pageOptions) {
        sqlQueryBuilder
                .append(" OFFSET ")
                .append(Integer.toString(pageOptions.getOffset()));
        return this;
    }

    public EndingOptions groupBy(String... columns) {
        sqlQueryBuilder.append(" GROUP BY ");
        StringBuilder subQueryBuilder = new StringBuilder();
        Arrays.stream(columns).forEach(column -> subQueryBuilder.append(column).append(", "));
        subQueryBuilder
                .deleteCharAt(subQueryBuilder.length() - 1)
                .deleteCharAt(subQueryBuilder.length() - 1);
        sqlQueryBuilder.append(subQueryBuilder.toString());
        return this;
    }

    public Having having() {
        sqlQueryBuilder.append(" HAVING ");
        return new Having(this);
    }

    SqlQueryBuilder append(String value) {
        return sqlQueryBuilder.append(value);
    }

    SqlQueryBuilder appendValue(String column, String value) {
        sqlQueryBuilder.appendWithValue(column, value);
        return sqlQueryBuilder;
    }

    public QueryResult build() {
        return sqlQueryBuilder.build();
    }
}
