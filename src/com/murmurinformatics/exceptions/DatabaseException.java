package com.murmurinformatics.exceptions;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA. User: Chris Wise Date: 28-Mar-2007 Time: 6:22:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseException extends Exception {
	private SQLException sqlException;

	public String getClassName() {
		return className;
	}

	public String getProcName() {
		return procName;
	}

	private String className;
	private String procName;
	private String sql = "";

	public DatabaseException(SQLException sqlException, String className, String procName) {
		this.sqlException = sqlException;
		this.className = className;
		this.procName = procName;
	}

	public DatabaseException(SQLException sqlException, String className, String procName, String sql) {
		this.sqlException = sqlException;
		this.className = className;
		this.procName = procName;
		this.sql = sql;
	}

	public String getMessage() {
		return sqlException.getMessage();
	}

	public String getSql() {
		return sql;
	}
}
