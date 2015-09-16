package com.inexas.exception;

public class UnsupportedException extends RuntimeException {
	private static final long serialVersionUID = -1233687881878895274L;

	public UnsupportedException() {
		super("Unsupported operation");
	}

}
