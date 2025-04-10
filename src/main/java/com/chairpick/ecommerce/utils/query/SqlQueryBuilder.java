package com.chairpick.ecommerce.utils.query;

import java.util.HashMap;
import java.util.Map;

public class SqlQueryBuilder {

    private final StringBuilder query;
    private final Map<String, String> parameters;

    private SqlQueryBuilder() {
        query = new StringBuilder();
        parameters = new HashMap<>();
    }

    public static SqlQueryBuilder create() {
        return new SqlQueryBuilder();
    }

    public SelectTable selectingAllFromTable(String table) {
        query.append("SELECT * FROM ")
                .append(table);
        return new SelectTable(this, table);
    }

    public With withClause() {
        return new With(this);
    }

    public SelectTable selectingColumnsFromTable(String table, String... columns) {
        query.append("SELECT ");
        for (int i = 0; i < columns.length; i++) {
            query.append(columns[i]);
            if (i < columns.length - 1) {
                query.append(", ");
            }
        }
        query.append(" FROM ")
                .append(table);

        return new SelectTable(this, table);
    }

    SelectTable addParameter(String column, String value) {
        parameters.put(column, value);
        return new SelectTable(this, "");
    }

    QueryResult build() {
        query.append(";");
        return new QueryResult(query.toString(), parameters);
    }

    SqlQueryBuilder append(String value) {
        query.append(value);
        return this;
    }

    void appendWithValue(String column, String value) {
        parameters.put(column, value);
    }

    public long countParameterOccurrences(String parameter) {
        return parameters
                .keySet()
                .stream()
                .filter(key -> key.startsWith(parameter))
                .count();
    }
}
