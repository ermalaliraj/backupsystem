package com.ea.bs.api.web.cmd.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class ServiceAlreadyStoppedException extends Exception {

	private static final long serialVersionUID = 4382471711643786543L;

	public ServiceAlreadyStoppedException(String msgError) {
		super(msgError);
	}
	
}
