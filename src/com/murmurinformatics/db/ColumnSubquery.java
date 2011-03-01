package com.murmurinformatics.db;

public class ColumnSubquery {
	private String aliasName;
	private String columnDefinition;
	private String childTable;
	private String childColumn;
	private String parentTable;
	private String parentColumn;

	public ColumnSubquery(String aliasName, String columnDefinition, String childTable, String childColumn, String parentTable, String parentColumn) {
		this.aliasName = aliasName;
		this.columnDefinition = columnDefinition;
		this.childTable = childTable;
		this.parentColumn = parentColumn;
		this.childColumn = childColumn;
		this.parentTable = parentTable;
	}

	public String getSubqueryColumn() {
		String subqueryColumn = "";

		subqueryColumn = "(SELECT " + columnDefinition + " " + "FROM " + childTable + " WHERE " + childTable + "." + childColumn + " = " + parentTable + "." + parentColumn + " LIMIT 1) " + aliasName;

		return subqueryColumn;
	}
}
