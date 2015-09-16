package com.inexas.exception;

public class InvalidCallException extends RuntimeException {
	private static final long serialVersionUID = 1545342949997750093L;

	public InvalidCallException() {
		super("Invalid call");
	}

	public InvalidCallException(String note) {
		super("Invalid call: " + note);
	}
}
