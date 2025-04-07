package com.chairpick.ecommerce.utils.query;

import lombok.Getter;

@Getter
public class SelectTable {
    private final SqlQueryBuilder sqlQueryBuilder;
    private final String table;
    
    public SelectTable(SqlQueryBuilder sqlQueryBuilder, String table) {
        this.sqlQueryBuilder = sqlQueryBuilder;
        this.table = table;
    }

    public Join joinDifferentTables(String leftTable, String rightTable) {
        SelectTable left = new SelectTable(sqlQueryBuilder, leftTable);
        SelectTable right = new SelectTable(sqlQueryBuilder, rightTable);
        return new Join(left, right);
    }

    public Join join(String rightTableName) {
        SelectTable rightTable = new SelectTable(sqlQueryBuilder, rightTableName);
        return new Join(this, rightTable);
    }

    public Where where() {
        sqlQueryBuilder.append(" WHERE ");
        return new Where(this);
    }

    public EndingOptions endingOptions() {
        return new EndingOptions(sqlQueryBuilder);
    }

    SqlQueryBuilder append(String value) {
        return sqlQueryBuilder.append(value);
    }

    void appendValue(String column, String value) {
        sqlQueryBuilder.appendWithValue(column, value);
    }

}
