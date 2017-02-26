package com.ea.bs.api.web.cmd.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class CannotStartServiceException extends Exception {

	private static final long serialVersionUID = 9034435103326004398L;

	public CannotStartServiceException(String msgError) {
		super(msgError);
	}
}
