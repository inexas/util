package com.inexas.exception;

import java.util.logging.Logger;

/**
 * All exceptions in Inexas should subclass InexasException or
 * InexasRuntimeException. They automatically log that an exception has been
 * thrown.
 */
public class InexasRuntimeException extends RuntimeException {
	// todo This class was, and should be abstract, the ctors should be
	// protected
	private static final long serialVersionUID = -9040680745290318050L;
	private final Logger log = Logger.getLogger(InexasException.class.getSimpleName());

	public InexasRuntimeException(String message, Throwable e) {
		super(message, e);
		log.severe(message);
		printStackTrace();
	}

	public InexasRuntimeException(String message) {
		super(message);
		log.severe(message);
		printStackTrace();
	}

}
