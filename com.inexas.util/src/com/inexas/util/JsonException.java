package com.inexas.util;

import com.inexas.exception.*;

public class JsonException extends InexasException {
	private static final long serialVersionUID = 8563056577914055006L;

	public JsonException(String message, Throwable e) {
		super(message, e);
	}

	public JsonException(String message) {
		super(message);
	}

}
