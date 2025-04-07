package com.chairpick.ecommerce.utils.query;

public class Join {

    private final SelectTable leftTable;
    private final SelectTable rightTable;

    public Join(SelectTable leftTable, SelectTable rightTable) {
        this.leftTable = leftTable;
        this.rightTable = rightTable;
    }

    public SelectTable innerJoinOn(String leftColumn, String rightColumn) {
        leftTable.append(" INNER JOIN ")
                .append(rightTable.getTable())
                .append(" ON ")
                .append(leftTable.getTable())
                .append(".")
                .append(leftColumn)
                .append(" = ")
                .append(rightTable.getTable())
                .append(".")
                .append(rightColumn);
        return leftTable;
    }

    public SelectTable leftJoinOn(String leftColumn, String rightColumn) {
        leftTable
                .append(" LEFT JOIN ")
                .append(rightTable.getTable())
                .append(" ON ")
                .append(leftTable.getTable())
                .append(".")
                .append(leftColumn)
                .append(" = ")
                .append(rightTable.getTable())
                .append(".")
                .append(rightColumn);
        return leftTable;
    }

    public SelectTable rightJoinOn(String leftColumn, String rightColumn) {
        leftTable.append(" RIGHT JOIN ")
                .append(rightTable.getTable())
                .append(" ON ")
                .append(leftTable.getTable())
                .append(".")
                .append(leftColumn)
                .append(" = ")
                .append(rightTable.getTable())
                .append(".")
                .append(rightColumn);
        return leftTable;
    }

    public SelectTable outerJoinOn(String leftColumn, String rightColumn) {
        leftTable.append(" OUTER JOIN ")
                .append(rightTable.getTable())
                .append(" ON ")
                .append(leftTable.getTable())
                .append(".")
                .append(leftColumn)
                .append(" = ")
                .append(rightTable.getTable())
                .append(".")
                .append(rightColumn);
        return leftTable;
    }

    public SelectTable crossJoinOn(String leftColumn, String rightColumn) {
        leftTable.append(" CROSS JOIN ")
                .append(rightTable.getTable())
                .append(" ON ")
                .append(leftTable.getTable())
                .append(".")
                .append(leftColumn)
                .append(" = ")
                .append(rightTable.getTable())
                .append(".")
                .append(rightColumn);
        return leftTable;
    }
}
