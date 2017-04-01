package com.ea.jms.exception;


/**
 * Wrapper for exceptions occurred while exchanging JMS messages.
 */
public class MessageException extends Exception {

	private static final long serialVersionUID = -7172116769155723808L;

	public MessageException() {
		super();
	}

	public MessageException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessageException(String message) {
		super(message);
	}

	public MessageException(Throwable cause) {
		super(cause);
	}

}
