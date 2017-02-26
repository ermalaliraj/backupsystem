package com.ea.bs.ru.cmd.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class RuException extends Exception {

	private static final long serialVersionUID = 9037738170307014689L;

	public RuException() {
		super();
	}

	public RuException(String message, Throwable cause) {
		super(message, cause);
	}

	public RuException(String message) {
		super(message);
	}

	public RuException(Throwable cause) {
		super(cause);
	}

}
