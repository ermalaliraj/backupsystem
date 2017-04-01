package com.ea.jms.exception;

import javax.ejb.ApplicationException;


@ApplicationException(rollback=true)
public class MessageSystemException extends MessageException {

	private static final long serialVersionUID = -7172116769155723808L;

	public MessageSystemException() {
		super();
	}

	public MessageSystemException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessageSystemException(String message) {
		super(message);
	}

	public MessageSystemException(Throwable cause) {
		super(cause);
	}

}
