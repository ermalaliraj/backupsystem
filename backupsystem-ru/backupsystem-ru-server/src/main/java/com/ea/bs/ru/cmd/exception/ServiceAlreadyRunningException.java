package com.ea.bs.ru.cmd.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class ServiceAlreadyRunningException extends Exception {

	private static final long serialVersionUID = 1625315673284560052L;

	public ServiceAlreadyRunningException() {
		super();
	}

	public ServiceAlreadyRunningException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceAlreadyRunningException(String message) {
		super(message);
	}

	public ServiceAlreadyRunningException(Throwable cause) {
		super(cause);
	}

}
