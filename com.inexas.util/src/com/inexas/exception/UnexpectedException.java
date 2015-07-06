package com.inexas.exception;

/**
 * This exception is thrown when we reach a place in the code that we didn't
 * expect
 */
public class UnexpectedException extends InexasRuntimeException {
	private static final long serialVersionUID = 8637197045486771888L;

	/**
	 * @param message
	 *            A message describing the cause of the exception.
	 */
	public UnexpectedException(String message) {
		super(message);
	}

	/**
	 * @param message
	 *            A message describing the cause of the exception.
	 * @param chainedException
	 *            The exception that caused this exception to be thrown.
	 */
	public UnexpectedException(String message, Exception chainedException) {
		super(message, chainedException);
	}

}
