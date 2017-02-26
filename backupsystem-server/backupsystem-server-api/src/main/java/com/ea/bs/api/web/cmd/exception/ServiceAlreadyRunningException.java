package com.ea.bs.api.web.cmd.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class ServiceAlreadyRunningException extends Exception {

	private static final long serialVersionUID = 4382471711643786543L;

	public ServiceAlreadyRunningException(String msgError) {
		super(msgError);
	}
	
}
