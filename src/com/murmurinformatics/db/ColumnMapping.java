package com.murmurinformatics.db;

public class ColumnMapping {
	private String columnName;
	private String attributeName;
	private ColumnType columnType;
	private int columnModifier = 0;
	private String sequenceName = null;
	private ColumnSubquery columnSubquery = null;

	public ColumnMapping(String columnName, String attributeName, ColumnType columnType) {
		this.columnName = columnName;
		this.attributeName = attributeName;
		this.columnType = columnType;
	}

	public ColumnMapping(String columnName, String attributeName, ColumnType columnType, String sequenceName) {
		this.columnName = columnName;
		this.attributeName = attributeName;
		this.columnType = columnType;
		this.sequenceName = sequenceName;
	}

	public ColumnMapping(String columnName, String attributeName, ColumnType columnType, int columnModifier) {
		this.columnName = columnName;
		this.attributeName = attributeName;
		this.columnType = columnType;
		this.columnModifier = columnModifier;
	}

	public ColumnMapping(String columnName, String attributeName, ColumnType columnType, ColumnSubquery columnSubquery) {
		this.columnName = columnName;
		this.attributeName = attributeName;
		this.columnType = columnType;
		this.columnSubquery = columnSubquery;
	}

	public boolean isPrimaryKey() {
		return sequenceName != null;
	}

	public boolean isColumnSubquery() {
		return columnSubquery != null;
	}

	public boolean isInsertable() {
		if (isColumnSubquery())
			return false;
		if ((columnModifier & ColumnModifiers.NON_INSERTABLE) > 0)
			return false;
		return true;
	}

	public boolean isUpdateable() {
		if (isPrimaryKey())
			return false;
		if (isColumnSubquery())
			return false;
		if ((columnModifier & ColumnModifiers.NON_UPDATEABLE) > 0)
			return false;
		return true;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public ColumnType getColumnType() {
		return columnType;
	}

	public void setColumnType(ColumnType columnType) {
		this.columnType = columnType;
	}

	public String getInsertPlaceholder() {
		if (isPrimaryKey())
			return sequenceName + ".nextval";
		else
			return "?";
	}

	public String getSetMethodName() {
		String methodName = getAttributeName();

		methodName = "set" + new String(new Character(methodName.charAt(0)).toString()).toUpperCase() + methodName.substring(1);
		return methodName;
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public String getGetMethodName() {
		String methodName = getAttributeName();

		methodName = "get" + new String(new Character(methodName.charAt(0)).toString()).toUpperCase() + methodName.substring(1);
		return methodName;
	}

	public String getColumnSubquery() {
		return columnSubquery.getSubqueryColumn();
	}

	public boolean hasSequence() {
		return sequenceName != null;
	}
}
