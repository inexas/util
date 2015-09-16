package com.inexas.exception;

public class NotImplementedException extends RuntimeException {
	private static final long serialVersionUID = -2548865605162082171L;

	public NotImplementedException() {
		super("Not implemented");
	}

	public NotImplementedException(String message) {
		super(message);
	}

}
