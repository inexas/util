package com.inexas.exception;

public class ShouldNotBeCalledException extends RuntimeException {
	private static final long serialVersionUID = 4157984141609529672L;

	public ShouldNotBeCalledException() {
		super("This method should never be called");
	}

	public ShouldNotBeCalledException(String note) {
		super("This method should never be called" + note);
	}
}
