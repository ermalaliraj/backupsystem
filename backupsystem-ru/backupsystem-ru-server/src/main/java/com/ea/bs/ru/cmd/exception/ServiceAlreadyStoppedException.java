package com.ea.bs.ru.cmd.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class ServiceAlreadyStoppedException extends Exception {

	private static final long serialVersionUID = -7917056666715393733L;

	public ServiceAlreadyStoppedException() {
		super();
	}

	public ServiceAlreadyStoppedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceAlreadyStoppedException(String message) {
		super(message);
	}

	public ServiceAlreadyStoppedException(Throwable cause) {
		super(cause);
	}

}
