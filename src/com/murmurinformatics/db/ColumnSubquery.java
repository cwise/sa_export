package com.murmurinformatics.db;

public class ColumnSubquery {
	private String aliasName;
	private String columnDefinition;
	private String fromTable;
	private String parentColumn;
	private String childColumn;

	public ColumnSubquery(String aliasName, String columnDefinition, String fromTable, String parentColumn, String childColumn) {
		this.aliasName = aliasName;
		this.columnDefinition = columnDefinition;
		this.fromTable = fromTable;
		this.parentColumn = parentColumn;
		this.childColumn = childColumn;
	}

	public String getSubqueryColumn() {
		String subqueryColumn = "";

		subqueryColumn = "(SELECT " + columnDefinition + " " + "FROM " + fromTable + " subquery_alias " + "WHERE subquery_alias." + childColumn + " = " + parentColumn + ")" + aliasName;

		return subqueryColumn;
	}
}
