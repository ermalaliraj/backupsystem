package com.ea.bs.api.web.exception;

import javax.ejb.ApplicationException;

/**
 * Wraps exception happened in Central Server
 */
@ApplicationException(rollback = true)
public class ServerException extends RuntimeException {

	private static final long serialVersionUID = -1972201540461744391L;

	public ServerException(String message) {
		super(message);
	}

}