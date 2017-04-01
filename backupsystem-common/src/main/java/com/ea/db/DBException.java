package com.ea.db;

/**
 * Wrappers for DB exceptions
 */
public class DBException extends Exception {

	private static final long serialVersionUID = 87394014351658820L;

	public DBException() {
		super();
	}

	public DBException(String message, Throwable cause) {
		super(message, cause);
	}

	public DBException(String message) {
		super(message);
	}

	public DBException(Throwable cause) {
		super(cause);
	}
}
