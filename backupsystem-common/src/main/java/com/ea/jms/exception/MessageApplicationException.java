package com.ea.jms.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=false)
public class MessageApplicationException extends MessageException {

	private static final long serialVersionUID = -7172116769155723808L;

	public MessageApplicationException() {
		super();
	}

	public MessageApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessageApplicationException(String message) {
		super(message);
	}

	public MessageApplicationException(Throwable cause) {
		super(cause);
	}

}
