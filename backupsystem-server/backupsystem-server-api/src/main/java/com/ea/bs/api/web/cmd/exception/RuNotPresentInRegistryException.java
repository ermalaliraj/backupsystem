package com.ea.bs.api.web.cmd.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class RuNotPresentInRegistryException extends Exception {

	private static final long serialVersionUID = -2536286497116415092L;

	public RuNotPresentInRegistryException(String msgError) {
		super(msgError);
	}
	
}
