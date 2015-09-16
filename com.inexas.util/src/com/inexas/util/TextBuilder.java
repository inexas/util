package com.inexas.util;

import java.util.Arrays;

/**
 * Very (expletive deleted) annoyingly StringBuilder is final so we can't extend
 * it to add the 'pretty' functionality.
 */
public class TextBuilder implements CharSequence {
	private final static int maxLineLength = 132;
	public static final char EOF = (char)-1;
	public final boolean pretty;
	private final TextBuilder indent;
	private char[] buffer = new char[16];
	private int bufferCapacity;
	private int index;
	private int lastNewline;
	private char compactDelimiter = ',';
	private String prettyDelimiter = ", ";
	private boolean delimit;

	public TextBuilder(String string) {
		this(true);
		append(string);
	}

	/**
	 * Construct a pretty TextBuilder
	 *
	 * @see #TextBuilder(boolean)
	 */
	public TextBuilder() {
		this(true);
	}

	public TextBuilder(boolean pretty) {
		this.pretty = pretty;
		bufferCapacity = buffer.length;
		indent = pretty ? new TextBuilder(false) : null;
	}

	@Override
	public final String toString() {
		return new String(buffer, 0, index);
	}

	public void newline() {
		if(pretty || index - lastNewline > maxLineLength) {
			newlinePrettyOrNot();
		}
	}

	public void newlinePrettyOrNot() {
		if(index >= bufferCapacity) {
			expand(bufferCapacity + 1);
		}
		buffer[index++] = '\n';
		lastNewline = index;
	}

	public void space() {
		if(pretty) {
			if(index == bufferCapacity) {
				expand(bufferCapacity + 1);
			}
			buffer[index++] = ' ';
		}
	}

	public void indent() {
		if(pretty) {
			append(indent);
		}
	}

	public void indentMore() {
		if(pretty) {
			indent.append('\t');
		}
	}

	public void indentLess() {
		if(pretty) {
			indent.index--;
			if(indent.index < 0) {
				throw new ArrayIndexOutOfBoundsException();
			}
		}
	}

	public void append(char c) {
		if(index == bufferCapacity) {
			expand(bufferCapacity + 1);
		}
		buffer[index++] = c;
	}

	public void append(CharSequence sequence) {
		final int length = sequence.length();
		final int newLength = index + length;
		if(newLength > bufferCapacity) {
			expand(newLength);
		}
		for(int i = 0; i < length; i++) {
			final char c = sequence.charAt(i);
			buffer[index++] = c;
			if(c == '\n') {
				lastNewline = index;
			}
		}
	}

	public void append(TextBuilder toAppend) {
		final int newLength = index + toAppend.index;
		if(newLength > bufferCapacity) {
			expand(index + toAppend.index);
		}
		System.arraycopy(toAppend.buffer, 0, buffer, index, toAppend.index);
		index = newLength;
	}

	public void append(int i) {
		append(Integer.toString(i));
	}

	public void append(Integer i) {
		append(i.toString());
	}

	public void append(long l) {
		append(Long.toString(l));
	}

	public void append(Long l) {
		append(l.toString());
	}

	/**
	 * Short hand for indent, append, newline.
	 *
	 * @param c
	 *            Character to write.
	 */
	public void writeline(char c) {
		indent();
		append(c);
		newline();
	}

	/**
	 * This is the same as indent(), append(text), newline();
	 *
	 * @param text
	 *            Text to write.
	 */
	public void writeline(String text) {
		indent();
		append(text);
		newline();
	}

	public void setLength(int length) {
		// todo This is a bit dangerous, consider push state?
		assert length > 0 && length < index;
		index = length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int length() {
		return index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public char charAt(int offset) {
		if(offset < 0 || offset >= index) {
			throw new StringIndexOutOfBoundsException(index);
		}
		return buffer[offset];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharSequence subSequence(int start, int end) {
		return getString(start, end);
	}

	/**
	 * @param start
	 *            Offset of first character to return.
	 * @return Return a string starting at 'start' and ending at the cursor.
	 */
	public String getString(int start) {
		return getString(start, cursor);
	}

	public String getString(int start, int end) {
		if(start < 0) {
			throw new StringIndexOutOfBoundsException(start);
		}
		if(end > index) {
			throw new StringIndexOutOfBoundsException(end);
		}
		if(start > end) {
			throw new StringIndexOutOfBoundsException(end - start);
		}
		return new String(buffer, start, end - start);
	}

	/**
	 * Set the delimiters to be used.
	 *
	 * @param compactDelimiter
	 *            By default this is ','. A value of (char)0 will force the
	 *            String delimiter to be used
	 * @param prettyDelimiter
	 *            By default this is ", "
	 */
	public void setDelimiters(char compactDelimiter, String prettyDelimiter) {
		this.compactDelimiter = compactDelimiter;
		this.prettyDelimiter = prettyDelimiter;
		delimit = false;
	}

	public void restartDelimiting() {
		delimit = false;
	}

	public void delimit() {
		if(delimit) {
			if(pretty || compactDelimiter == 0) {
				append(prettyDelimiter);
			} else {
				append(compactDelimiter);
			}
		} else {
			delimit = true;
		}
	}

	public int lastIndexOf(char c) {
		int result = index - 1;
		while(result >= 0 && buffer[result] != c) {
			result--;
		}
		return result;
	}

	/**
	 * The does the same as creating a new TextBuilder without the construction
	 * cost.
	 */
	public void recycle() {
		lastNewline = 0;
		index = 0;
		if(indent != null) {
			indent.recycle();
		}
		delimit = false;
		cursor = 0;
	}

	// todo Should the object methods be here?

	/**
	 * Do indent objectName space '{' newline indentMore
	 *
	 * @param objectName
	 *            The object name.
	 */
	public void beginObject(String objectName) {
		indent();
		append(objectName);
		space();
		append('{');
		newline();
		indentMore();
	}

	/**
	 * Do indentLess indent '}' newline
	 */
	public void endObject() {
		indentLess();
		indent();
		append('}');
		newline();
	}

	/**
	 * Do indent "key" ':' ' ' value ';' nl
	 *
	 * @param keyName
	 *            The name of the key.
	 * @param value
	 *            The value to write.
	 */
	public void writeProperty(String keyName, String value) {
		indent();
		append(keyName);
		append(':');
		space();
		append(value);
		append(';');
		newline();
	}

	public static interface ToString {
		void toString(TextBuilder result);
	}

	/**
	 * Do indent "key" ':' ' ' value.toString(tb) ';' nl
	 *
	 * @param keyName
	 *            The name of the key.
	 * @param value
	 *            The value to write.
	 */
	public void writeProperty(String keyName, ToString value) {
		indent();
		append(keyName);
		append(':');
		space();
		value.toString(this);
		append(';');
		newline();
	}

	/*
	 * Parsing support
	 *
	 * TextBuilder provides a set of methods that support simple parsing of
	 * strings. A cursor is set to the place where parsing is to start,
	 * typically this will be at the start of the string. Then a number of
	 * methods: parseXxx() attempt to advance the cursor reading be reading
	 * whatever is expected; for example parseInt() will attempt to parse an
	 * int.
	 *
	 * For each type there is a imperative version and a 'best attempt' version
	 * int parseInt() must parse an int and returns primitive and throws a
	 * ParseException if one cannot be parsed whereas parseInteger tries to
	 * parse an integer and returns an Integer or null of one cannot be parsed.
	 */

	public class ParseException extends RuntimeException {
		private static final long serialVersionUID = 1956489519812071218L;

		ParseException(String message) {
			super("\"" + TextBuilder.this.toString() + "\":" + cursor + ' ' + message);
		}
	}

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
	 * Consume ASCII characters matching a given bitmap (see ASCII_*), e.g.
	 * tb.consume(ASCII_1_9 | ASCII_UNDERLINE) will advance the cursor while the
	 * character at the cursor is either a number or an underline.
	 *
	 * @param bitmap
	 *            The bitmap to match, .
	 * @return Return true if at least one character was consumed.
	 */
	public boolean consumeAscii(byte bitmap) {
		boolean result = false;
		while(cursor < index) {
			final char c = buffer[cursor];
			if(c > MAX_ASCII || ((asciiTypeBits[c] & bitmap) == 0)) {
				break;
			}
			result = true;
			cursor++;
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
		if(cursor >= index) {
			throw new ParseException("Buffer overrun");
		}
		return buffer[cursor++];
	}

	public long parseLong() {
		int result = 0;
		final boolean negative = consume('-');
		int count = 0;

		while(true) {
			final char c = buffer[cursor];
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
		if(cursor >= index) {
			throw new ParseException("Buffer overrun");
		}
		if(buffer[cursor] != c) {
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

		if(cursor >= index) {
			result = false;
		} else {
			if(buffer[cursor] == c) {
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

		final int length = string.length();
		if(cursor + length <= index) {
			final char[] ca = string.toCharArray();
			result = true;
			for(int i = 0; i < length; i++) {
				if(buffer[cursor + i] != ca[i]) {
					result = false;
					break;
				}
			}
		} else {
			result = false;
		}

		if(result) {
			cursor += length;
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
		final int start = cursor;
		while(cursor < index && buffer[cursor] != stop) {
			cursor++;
		}
		return start != cursor;
	}

	public String parseUntil(char stop) {
		final int start = cursor;
		consumeUntil(stop);
		return getString(start);
	}

	/**
	 * @return Return true if there is no more input to consume.
	 */
	public boolean isEof() {
		return cursor == index;
	}

	/**
	 * @return The character at the cursor but don't advance the cursor.
	 * @throws ParseException
	 *             Thrown if the cursor is at the end of input.
	 */
	public char peek() {
		return cursor == index ? EOF : buffer[cursor];
	}

	/**
	 * @param c
	 *            The character to search for.
	 * @return Return true if the character is at the cursor.
	 */
	public boolean peek(char c) {
		return cursor + 1 < index && buffer[cursor] == c;
	}

	/**
	 * @param string
	 *            The string to search for.
	 * @return Return true if the string is at the cursor.
	 */
	public boolean peek(String string) {
		boolean result;

		final int length = string.length();
		if(cursor + length >= index) {
			result = false;
		} else {
			result = true;
			for(int i = 0; i < length; i++) {
				if(buffer[cursor + i] != string.charAt(i)) {
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
		for(int i = 0; i < index; i++) {
			if(buffer[i] == c) {
				result++;
			}
		}
		return result;
	}

	public int cursor() {
		return cursor;
	}

	public String parse(Checker checker) {

		final int start = cursor;
		while(cursor < index) {
			final char c = buffer[cursor];
			if(!checker.isValid(cursor - start, c)) {
				break;
			}
			cursor++;
		}

		return cursor == start ? null : new String(buffer, start, cursor - start);
	}

	/**
	 * @param cursor
	 *            Set the cursor to this location.
	 */
	public void setCursor(int cursor) {
		assert cursor >= 0 && cursor <= index;
		this.cursor = cursor;
	}

	private void expand(int minimumCapacity) {
		// There's no check for buffer overflow
		bufferCapacity = bufferCapacity * 2;
		if(minimumCapacity > bufferCapacity) {
			bufferCapacity = minimumCapacity;
		}
		buffer = Arrays.copyOf(buffer, bufferCapacity);
	}

	/**
	 * Consume spaces, tabs, newlines
	 *
	 * @return Always returns true.
	 */
	public boolean ws() {
		while(cursor < index) {
			final char c = buffer[cursor];
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
	 */
	public boolean consumeString() {
		final int save = cursor;
		if(cursor != index) {
			// Pick up the quote character...
			final char quote = buffer[cursor];
			assert quote == '\'' || quote == '"' || quote == '`' : "Invalid quote: " + quote;

			// Advance cursor until the an end condition is found...
			while(true) {
				cursor++;

				if(cursor == index) {
					// EOF
					cursor = save;
					break;
				}

				final char c = buffer[cursor];
				if(c == quote) {
					// Found terminating quote
					cursor++; // To next character to parse
					break;
				}

				if(c == '\n') {
					// End of line
					cursor = save;
					break;
				}

				if(c == '\\') {
					// Escape: Assume anything can be escaped...
					cursor++;
					if(cursor == index) {
						// EOF
						cursor = save;
						break;
					}
				}
			}
		}

		return cursor != save;
	}

	/**
	 * '0' | ( '-'? [1-9] [1-9]* )
	 *
	 * @return True if a +ve or -ve integer has been consumed.
	 */
	public boolean consumeInt() {
		final boolean result;

		final int save = cursor;
		if(consume('0')) {
			result = true;
		} else {
			consume('-');
			result = consumeAscii(ASCII_1_9);
			consumeAscii(ASCII_0_9);
		}

		if(!result) {
			cursor = save;
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

		if(cursor == index) {
			result = false;
		} else {
			final int start = cursor;
			final char first = buffer[cursor];
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
}
