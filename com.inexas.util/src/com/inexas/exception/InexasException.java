package com.inexas.exception;

import java.util.logging.Logger;

/**
 * All exceptions in Inexas should subclass InexasException or
 * InexasRuntimeException. They automatically log that an exception has been
 * thrown.
 */
public class InexasException extends Exception {
	private static final long serialVersionUID = -9040680745290318050L;
	private final Logger log = Logger.getLogger(InexasException.class.getCanonicalName());

	public InexasException(String message, Throwable e) {
		super(message, e);
		log.severe(message);
	}

	public InexasException(String message) {
		super(message);
		log.severe(message);
	}

}
