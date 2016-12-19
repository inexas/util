package com.inexas.exception;

public class ImplementMeException extends RuntimeException {
	private static final long serialVersionUID = 5755020430853368107L;

	public ImplementMeException() {
		super("How about implementing me!");
	}

	public ImplementMeException(String message) {
		super("How about implementing me!: " + message);
	}
}
