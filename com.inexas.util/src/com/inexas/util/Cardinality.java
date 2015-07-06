package com.inexas.util;

import com.inexas.exception.InexasRuntimeException;

/**
 * Cardinality
 */
public class Cardinality {
	public static final int MANY = Integer.MAX_VALUE;
	public static final Cardinality ZERO = new Cardinality(0, 0);
	public static final Cardinality ZERO_ONE = new Cardinality(0, 1);
	public static final Cardinality ZERO_MANY = new Cardinality(0, MANY);
	public static final Cardinality ONE_ONE = new Cardinality(1, 1);
	public static final Cardinality ONE_MANY = new Cardinality(1, MANY);
	public final int from;
	public final int to;
	public final String text;

	/**
	 * Either from &gt;= 0 and from &lt;= to or runtime exception... Take your
	 * pick. Using this factory method returns the reusable static objects
	 * defined in this class so you can "cardinality == Cardinality.ZERO_ONE"
	 * for example
	 *
	 * @param from
	 *            Minimum value (inclusive).
	 * @param to
	 *            Maximum value (inclusive).
	 * @return the returned value for ..0, 0..1, 0..*, 1..1, 1..* and * will
	 *         always be return the same physical Object so you can safely use
	 *         == to check for equality
	 */
	public static Cardinality newInstance(int from, int to) {
		final Cardinality result;
		if(from < 0 || from > to) {
			throw new InexasRuntimeException("Invalid cardinality: '" + from + ".." + to + '\'');
		}

		if(from == 0) {
			if(to == 0) {
				result = ZERO;
			} else if(to == 1) {
				result = ZERO_ONE;
			} else if(to == MANY) {
				result = ZERO_MANY;
			} else {
				result = new Cardinality(from, to);
			}
		} else if(from == 1) {
			if(to == 1) {
				result = ONE_ONE;
			} else if(to == MANY) {
				result = ONE_MANY;
			} else {
				result = new Cardinality(from, to);
			}
		} else {
			result = new Cardinality(from, to);
		}

		return result;
	}

	/**
	 * Parse a Cardinality from a given string.
	 *
	 * @param text
	 *            I need a string containing a valid integer &gt;= 0, ".."
	 *            followed by either a '*' or another valid integer &gt;= first
	 *            integer and no spaces otherwise I'll throw a runtime
	 *            exception... promise!
	 *
	 * @return the returned value for ..0, 0..1, 0..*, 1..1, 1..* and * will
	 *         always be return the same physical Object so you can safely use
	 *         == to check for equality
	 * @see #Cardinality(int,int)
	 */
	public static Cardinality newInstance(String text) {
		final int from, to;

		if(text == null) {
			throw new InexasRuntimeException("Invalid cardinality: null");
		}

		if("*".equals(text)) {
			from = 0;
			to = MANY;
		} else {
			final int dots = text.indexOf("..");
			if(dots < 1) {
				throw new InexasRuntimeException("Invalid cardinality: '" + text + '\'');
			}

			try {
				from = Integer.parseInt(text.substring(0, dots));

				final String toText = text.substring(dots + 2);
				to = "*".equals(toText) ? MANY : Integer.parseInt(toText);
			} catch(final NumberFormatException e) {
				throw new InexasRuntimeException("Invalid cardinality: '" + text + '\'', e);
			}
		}

		return newInstance(from, to);
	}

	private Cardinality(int from, int to) {
		this.from = from;
		this.to = to;
		text = Integer.toString(from) + ".." + (to == MANY ? "*" : Integer.toString(to));
	}

	@Override
	public boolean equals(Object rhsObject) {
		final boolean result;

		if(this == rhsObject) {
			result = true;
		} else if(rhsObject == null || !(rhsObject instanceof Cardinality)) {
			result = false;
		} else {
			final Cardinality rhs = (Cardinality)rhsObject;
			result = from == rhs.from && to == rhs.to;
		}

		return result;
	}

	@Override
	public int hashCode() {
		return from * 100 + to;
	}

	public boolean isFixed() {
		return from == to;
	}

	public boolean isValidCardinality(int candidate) {
		return candidate >= from && candidate <= to;
	}

	@Override
	public String toString() {
		return text;
	}

	public void toMarkup(TextBuilder result, String indent) {
		result.append(indent);
		result.append(text);
	}

}
