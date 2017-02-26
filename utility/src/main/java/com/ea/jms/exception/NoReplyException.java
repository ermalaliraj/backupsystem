package com.ea.jms.exception;

/**
 * Exception throws when reply is missing in case of SYNCH communication
 */
public class NoReplyException extends MessageException {

	private static final long serialVersionUID = -2987633124572739822L;

	public NoReplyException() {
		super();
	}

	public NoReplyException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoReplyException(String message) {
		super(message);
	}

	public NoReplyException(Throwable cause) {
		super(cause);
	}

}
