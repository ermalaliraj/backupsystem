package com.ea.bs.api.web.cmd.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class CannotStopServiceException extends Exception {

	private static final long serialVersionUID = 3724975898266540884L;

	public CannotStopServiceException(String msgError) {
		super(msgError);
	}

}
