package com.inexas.util;

/**
 * Parser provides a set of methods that support simple parsing of strings. A
 * cursor is set to the place where parsing is to start, typically this will be
 * at the start of the string. Then a number of methods: parseXxx() attempt to
 * advance the cursor reading be reading whatever is expected; for example
 * parseInt() will attempt to parse an int.
 *
 * For each type there is a imperative version and a 'best attempt' version int
 * parseInt() must parse an int and returns primitive and throws a
 * ParseException if one cannot be parsed whereas parseInteger tries to parse an
 * integer and returns an Integer or null of one cannot be parsed.
 */
public class Parser implements CharSequence {
	public static final char EOF = (char)-1;
	private final char[] ca;
	private final int length;
	private int start;

	public Parser(String string) {
		ca = string.toCharArray();
		length = ca.length;
	}

	@Override
	public final String toString() {
		return new String(ca);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int length() {
		return length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public char charAt(int offset) {
		if(offset < 0 || offset >= length) {
			throw new StringIndexOutOfBoundsException(length);
		}
		return ca[offset];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharSequence subSequence(int from, int to) {
		return getString(from, to);
	}

	/**
	 * @param from
	 *            Offset of first character to return.
	 * @return Return a string starting at 'start' and ending at the cursor.
	 */
	public String getString(int from) {
		return getString(from, cursor);
	}

	public String getString(int from, int to) {
		if(from < 0) {
			throw new StringIndexOutOfBoundsException(from);
		}
		if(to > length) {
			throw new StringIndexOutOfBoundsException(to);
		}
		if(from > to) {
			throw new StringIndexOutOfBoundsException(to - from);
		}
		return new String(ca, from, to - from);
	}

	public int lastIndexOf(char c) {
		int result = length - 1;
		while(result >= 0 && ca[result] != c) {
			result--;
		}
		return result;
	}

	public class ParseException extends RuntimeException {
		private static final long serialVersionUID = 1956489519812071218L;

		ParseException(String message) {
			super("\"" + Parser.this.toString() + "\":" + cursor + ' ' + message);
		}
	}

	/**
	 * A Checker can be used with parse(Checker) to check for anything the
	 * caller wants that is not supported directly by Parser.
	 */
	public static interface Checker {
		boolean isValid(int offset, char c);
	}

	/**
	 * ASCII type lookup.
	 */
	// @formatter:off
	private final static byte[] asciiTypeBits = {
		//	    00          01          02          03          04          05          06          07           08          09          0A          0B          0C          0D          0E          0F
		0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00,  0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00,  // 00
		0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00,  0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00,  // 10
		0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00,  0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00,  // 20
		0b00_11_00_10, 0b00_11_00_11, 0b00_10_00_11, 0b00_10_00_11, 0b00_10_00_11, 0b00_10_00_11, 0b00_10_00_11, 0b00_10_00_11,  0b00_10_00_11, 0b00_10_00_11, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00,  // 30 0-9
		0b00_00_00_00, 0b00_10_01_00, 0b00_10_01_00, 0b00_10_01_00, 0b00_10_01_00, 0b00_10_01_00, 0b00_10_01_00, 0b00_00_01_00,  0b00_00_01_00, 0b00_00_01_00, 0b00_00_01_00, 0b00_00_01_00, 0b00_00_01_00, 0b00_00_01_00, 0b00_00_01_00, 0b00_00_01_00,  // 40 A-O
		0b00_00_01_00, 0b00_00_01_00, 0b00_00_01_00, 0b00_00_01_00, 0b00_00_01_00, 0b00_00_01_00, 0b00_00_01_00, 0b00_00_01_00,  0b00_00_01_00, 0b00_00_01_00, 0b00_00_01_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b01_00_00_00,  // 50 P-Z, _
		0b00_00_00_00, 0b00_10_10_00, 0b00_10_10_00, 0b00_10_10_00, 0b00_10_10_00, 0b00_10_10_00, 0b00_10_10_00, 0b00_00_10_00,  0b00_00_10_00, 0b00_00_10_00, 0b00_00_10_00, 0b00_00_10_00, 0b00_00_10_00, 0b00_00_10_00, 0b00_00_10_00, 0b00_00_10_00,  // 60 a-o
		0b00_00_10_00, 0b00_00_10_00, 0b00_00_10_00, 0b00_00_10_00, 0b00_00_10_00, 0b00_00_10_00, 0b00_00_10_00, 0b00_00_10_00,  0b00_00_10_00, 0b00_00_10_00, 0b00_00_10_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00, 0b00_00_00_00   // 70 p-z
	};
	public final static byte ASCII_1_9			= 0b00_00_00_00_01;
	public final static byte ASCII_0_9			= 0b00_00_00_00_10;
	public final static byte ASCII_A_Z			= 0b00_00_00_01_00;
	public final static byte ASCII_a_z			= 0b00_00_00_10_00;
	public final static byte ASCII_0_1			= 0b00_00_01_00_00;
	public final static byte ASCII_0_F			= 0b00_00_10_00_00;
	public final static byte ASCII_UNDERLINE	= 0b00_01_00_00_00;
	// @formatter:on
	private final static int MAX_ASCII = asciiTypeBits.length - 1;
	private int cursor;

	/**
	 * Consume at least 'from' and at most 'to characters of type 'bitmap'
	 * greedily
	 *
	 * @return Return true if at least one character was consumed.
	 *
	 * @param bitmap
	 *            The bitmap to match, e.g. ASCII_1_9 | ASCII_UNDERLINE.
	 * @param constraints
	 *            Either 0, 1 or 2 integers. 0 means consume at least one
	 *            character, 1 means consume exactly x characters, 2 means
	 *            consume at least x and at most y characters, For example
	 *            consumeAscii(ASCII_0_9, 1, 3) means consume between 1 and
	 *            three digits.
	 * @return Return true if at least one and the required matching characters
	 *         were consumed.
	 */
	public boolean consumeAscii(byte bitmap, int... constraints) {
		final boolean result;

		assert bitmap != 0 : "Missing bit map";

		int from, to;
		final int constraintsLength = constraints.length;
		if(constraintsLength == 0) {
			from = 0;
			to = Integer.MAX_VALUE;
		} else if(constraintsLength == 1) {
			from = to = constraints[0];
		} else if(constraintsLength == 2) {
			from = constraints[0];
			to = constraints[1];
		} else {
			throw new RuntimeException("Too many constraints: " + constraintsLength);
		}

		assert from >= 0 && from <= to : "Invalid size constraints";

		start = cursor;
		int count = 0;
		while((cursor + count) < length && count < to) {
			final char c = ca[cursor + count];
			if(c <= MAX_ASCII && (asciiTypeBits[c] & bitmap) != 0) {
				count++;
			} else {
				break;
			}
		}

		if(count >= from && count <= to && count > 0) {
			result = true;
			cursor += count;
		} else {
			result = false;
		}

		return result;
	}

	/**
	 * Reset parsing to being at offset 0.
	 */
	public void parseReset() {
		cursor = 0;
	}

	/**
	 * Parse any character and advance the cursor.
	 *
	 * @return The char at the current cursor location.
	 */
	public char parseChar() {
		if(cursor >= length) {
			throw new ParseException("Buffer overrun");
		}
		return ca[cursor++];
	}

	public long parseLong() {
		int result = 0;
		final boolean negative = consume('-');
		int count = 0;

		while(true) {
			final char c = ca[cursor];
			if(c >= '0' && c <= '9') {
				result = result * 10 + (c - '0');
				count++;
			} else {
				break;
			}
		}

		// ?todo This will accept "012" as an integer, hmmm...

		if(count == 0) {
			throw new ParseException("Integer not found");
		}

		if(negative) {
			result = -result;
		}

		return result;
	}

	/**
	 * @param c
	 *            The character to consume.
	 * @throws ParseException
	 *             Thrown if end of buffer or the character was not at the
	 *             cursor.
	 */
	public void consumeOrThrow(char c) {
		if(cursor >= length) {
			throw new ParseException("Buffer overrun");
		}
		if(ca[cursor] != c) {
			throw new ParseException("Character not found at cursor: " + c);
		}
	}

	/**
	 * @param c
	 *            The character to consume.
	 * @return Return true if the character was at the cursor.
	 */
	public boolean consume(char c) {
		final boolean result;

		if(cursor >= length) {
			result = false;
		} else {
			if(ca[cursor] == c) {
				cursor++;
				result = true;
			} else {
				result = false;
			}
		}

		return result;
	}

	/**
	 * @param string
	 *            The string to consume.
	 * @return Return true if the string was at the cursor.
	 */
	public boolean consume(String string) {
		boolean result;

		assert string != null;

		final int stringLength = string.length();
		if(cursor + stringLength <= length) {
			final char[] stringCa = string.toCharArray();
			result = true;
			for(int i = 0; i < stringLength; i++) {
				if(cursor + i >= length || ca[cursor + i] != stringCa[i]) {
					result = false;
					break;
				}
			}
		} else {
			result = false;
		}

		if(result) {
			cursor += stringLength;
		}

		return result;
	}

	/**
	 * Advance the cursor until either the end of input or the stop character
	 * was found. Stop characters are not consumed.
	 *
	 * @param stop
	 *            Stop character.
	 * @return At least one character was consumed.
	 */
	public boolean consumeUntil(char stop) {
		start = cursor;
		while(cursor < length && ca[cursor] != stop) {
			cursor++;
		}
		return start != cursor;
	}

	public String parseUntil(char stop) {
		start = cursor;
		consumeUntil(stop);
		return getString(start);
	}

	/**
	 * @return Return true if there is no more input to consume.
	 */
	public boolean isEof() {
		return cursor == length;
	}

	/**
	 * @return The character at the cursor but don't advance the cursor.
	 * @throws ParseException
	 *             Thrown if the cursor is at the end of input.
	 */
	public char peek() {
		return cursor == length ? EOF : ca[cursor];
	}

	/**
	 * @param c
	 *            The character to search for.
	 * @return Return true if the character is at the cursor.
	 */
	public boolean peek(char c) {
		return cursor + 1 < length && ca[cursor] == c;
	}

	/**
	 * @param string
	 *            The string to search for.
	 * @return Return true if the string is at the cursor.
	 */
	public boolean peek(String string) {
		boolean result;

		final int stringLength = string.length();
		if(cursor + stringLength >= stringLength) {
			result = false;
		} else {
			result = true;
			for(int i = 0; i < stringLength; i++) {
				if(ca[cursor + i] != string.charAt(i)) {
					result = false;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * @param c
	 *            The character to count.
	 * @return The count of c characters in the buffer.
	 */
	public int count(char c) {
		int result = 0;
		for(int i = 0; i < length; i++) {
			if(ca[i] == c) {
				result++;
			}
		}
		return result;
	}

	public int cursor() {
		return cursor;
	}

	public String parse(Checker checker) {

		start = cursor;
		while(cursor < length) {
			final char c = ca[cursor];
			if(!checker.isValid(cursor - start, c)) {
				break;
			}
			cursor++;
		}

		return cursor == start ? null : new String(ca, start, cursor - start);
	}

	/**
	 * @param cursor
	 *            Set the cursor to this location.
	 */
	public void setCursor(int cursor) {
		assert cursor >= 0 && cursor <= length;
		this.cursor = cursor;
	}

	/**
	 * Consume spaces, tabs, newlines
	 *
	 * @return Always returns true.
	 */
	public boolean ws() {
		while(cursor < length) {
			final char c = ca[cursor];
			if(c != ' ' && c != '\n' && c != '\t') {
				break;
			}
			cursor++;
		}
		return true;
	}

	/**
	 * The cursor should point at the leading quote character: either ', " or `.
	 * Line feeds are considered end of line and will cause the consume to fail
	 * as will EOF. The '\' character will escape any following character.
	 *
	 * @return Return true if a quoted String was found and the cursor advanced.
	 *         Otherwise false is returned. Open ended strings are not trapped.
	 */
	public boolean consumeString() {
		start = cursor;
		if(cursor != length) {
			// Pick up the quote character...
			final char quote = ca[cursor];
			assert quote == '\'' || quote == '"' || quote == '`' : "Invalid quote: " + quote;

			// Advance cursor until the an end condition is found...
			while(true) {
				cursor++;

				if(cursor == length) {
					// EOF
					cursor = start;
					break;
				}

				final char c = ca[cursor];
				if(c == quote) {
					// Found terminating quote
					cursor++; // To next character to parse
					break;
				}

				if(c == '\n') {
					// End of line
					cursor = start;
					break;
				}

				if(c == '\\') {
					// Escape: Assume anything can be escaped...
					cursor++;
					if(cursor == length) {
						// EOF
						cursor = start;
						break;
					}
				}
			}
		}

		return cursor != start;
	}

	/**
	 * '0' | ( '-'? [1-9] [1-9]* )
	 *
	 * @return True if a +ve or -ve integer has been consumed.
	 */
	public boolean consumeInt() {
		final boolean result;

		start = cursor;
		if(consume('0')) {
			result = true;
		} else {
			consume('-');
			result = consumeAscii(ASCII_1_9);
			consumeAscii(ASCII_0_9);
		}

		if(!result) {
			cursor = start;
		}

		return result;
	}

	/**
	 * No, not a pint! A positive integer. '0' | [1-9] [1-9]*
	 *
	 * @return True if a positive integer is found and the cursor and the cursor
	 *         advanced.
	 */
	public boolean consumePint() {
		final boolean result;

		if(cursor == length) {
			result = false;
		} else {
			start = cursor;
			final char first = ca[cursor];
			consumeAscii(ASCII_0_9);
			if(first == '0') {
				if(cursor - start > 1) {
					// Something like 01212...
					throw new ParseException("Invalid integer: " + getString(start));
				}
			}
			result = true;
		}

		return result;
	}

	/**
	 * This is equivalent to the following code:
	 *
	 * <pre>
	 * final int save = t.getCursor();
	 * t.consumePint();
	 * final String consumed = t.getConsumed();
	 * </pre>
	 *
	 * @return The last characters consumed. This will be an empty string if
	 *         nothing was consumed.
	 */
	public String getConsumed() {
		return new String(ca, start, cursor - start);
	}

	/**
	 * @return A character array copy of the currently buffered Text
	 */
	public char[] toCharArray() {
		final char[] result = new char[length];
		System.arraycopy(ca, 0, result, 0, length);
		return result;
	}

}
