package com.murmurinformatics.exceptions;

public class ReflectionException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2380562047282326165L;
	private Exception exception;

	public String getClassName() {
		return className;
	}

	public String getProcName() {
		return procName;
	}

	private String className;
	private String procName;

	public ReflectionException(Exception exception, String className, String procName) {
		this.exception = exception;
		this.className = className;
		this.procName = procName;
	}

	public String getReflectionError() {
		return exception.getClass() + ":" + exception.getMessage();
	}
}
