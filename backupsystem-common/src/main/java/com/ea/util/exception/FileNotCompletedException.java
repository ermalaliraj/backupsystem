package com.ea.util.exception;

/**
 * 
 */
public class FileNotCompletedException extends Exception {

	private static final long serialVersionUID = -1152066485321235528L;

	public FileNotCompletedException() {
		super();
	}

	public FileNotCompletedException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileNotCompletedException(String message) {
		super(message);
	}

	public FileNotCompletedException(Throwable cause) {
		super(cause);
	}
}
