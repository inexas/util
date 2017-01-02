package com.inexas.util;

import java.util.Arrays;

/**
 * Similar to StringBuilder and StringBuffer but with added features. Not thread
 * safe, fast.
 *
 * Added features
 * <ul>
 * <li>Pretty printing.</li>
 * <li>Indent handling</li>
 * <li>Delimiter handling</li>
 * </ul>
 */
public class Text implements CharSequence {
	private final static int maxLineLength = 132;
	public static final char EOF = (char)-1;
	public final boolean pretty;
	private final Text indent;
	private char[] buffer = new char[16];
	private int bufferCapacity;
	private int index;
	private int lastNewline;
	private char compactDelimiter = ',';
	private String prettyDelimiter = ", ";
	private boolean delimit;

	public Text(String string) {
		this(true);
		append(string);
	}

	public static interface ToString {
		void toString(Text result);
	}

	/**
	 * Construct a pretty Text
	 *
	 * @see #Text(boolean)
	 */
	public Text() {
		this(true);
	}

	public Text(boolean pretty) {
		this.pretty = pretty;
		bufferCapacity = buffer.length;
		indent = pretty ? new Text(false) : null;
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
	public CharSequence subSequence(int from, int to) {
		return getString(from, to);
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
		ensureSpaceFor(1);
		buffer[index++] = '\n';
		lastNewline = index;
	}

	public void space() {
		if(pretty) {
			ensureSpaceFor(1);
			buffer[index++] = ' ';
		}
	}

	public void spacePrettyOrNot() {
		ensureSpaceFor(1);
		buffer[index++] = ' ';
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
		ensureSpaceFor(1);
		buffer[index++] = c;
	}

	public void append(CharSequence sequence) {
		if(sequence == null) {
			append("<null>");
		} else {
			final int length = sequence.length();
			ensureSpaceFor(length);
			for(int i = 0; i < length; i++) {
				final char c = sequence.charAt(i);
				buffer[index++] = c;
				if(c == '\n') {
					lastNewline = index;
				}
			}
		}
	}

	public void append(Object object) {
		append(object == null ? "null" : object.toString());
	}

	public void append(Text toAppend) {
		final int length = index + toAppend.index;
		ensureSpaceFor(length);
		System.arraycopy(toAppend.buffer, 0, buffer, index, toAppend.index);
		index = length;
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

	public void append(double d) {
		append(Double.toString(d));
	}

	public void append(Double d) {
		append(d.toString());
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
	 * This is the same as indent(), append(text)..., newline();
	 *
	 * @param strings
	 *            List of Strings write.
	 */
	public void writeline(String... strings) {
		indent();
		for(final String string : strings) {
			append(string);
		}
		newline();
	}

	public void setLength(int length) {
		// todo This is a bit dangerous, consider push state?
		assert length >= 0 && length <= index;
		index = length;
	}

	public String getString(int from, int to) {
		if(from < 0) {
			throw new StringIndexOutOfBoundsException(from);
		}
		if(to > index) {
			throw new StringIndexOutOfBoundsException(to);
		}
		if(from > to) {
			throw new StringIndexOutOfBoundsException(to - from);
		}
		return new String(buffer, from, to - from);
	}

	/**
	 * Set the delimiter to be used. This is the equivalent of calling
	 * setDelimiters(x, "" + x + " ")
	 *
	 * @param delimiter
	 *            The character to use when delimiting
	 */
	public void setDelimiter(char delimiter) {
		this.compactDelimiter = delimiter;
		this.prettyDelimiter = "" + delimiter + " ";
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
	 * The does the same as creating a new Text without the construction cost.
	 */
	public void recycle() {
		lastNewline = 0;
		index = 0;
		if(indent != null) {
			indent.recycle();
		}
		delimit = false;
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

	/**
	 * Do indent "key" ':' ' ' value.toString(t) ';' nl
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

	/**
	 * @return A character array copy of the currently buffered Text
	 */
	public char[] toCharArray() {
		final char[] result = new char[index];
		System.arraycopy(buffer, 0, result, 0, index);
		return result;
	}

	/**
	 * Append characters escaping special characters in a Java like fashion so
	 * that the text's contents are human readable, e.g.
	 * "Newline\ntab\t\nWeird\u0001"
	 *
	 * @param c
	 *            Character to append
	 * @param escapeQuotes
	 *            true if you need quotes to be \"escaped\"
	 */
	public void appendEscaped(char c, boolean escapeQuotes) {
		if(c < 32 || c == '\\' || (c == '"' && escapeQuotes)) {
			switch(c) {
			case '\b':
				append("\\b");
				break;

			case '\n':
				append("\\n");
				break;

			case '\t':
				append("\\t");
				break;

			case '\f':
				append("\\f");
				break;

			case '\r':
				// Discard
				break;

			case '"':
				append("\\\"");
				break;

			case '\\':
				append("\\\\");
				break;

			default:
				append("\\u");
				final String hex = Integer.toHexString(c);
				append("0000".substring(hex.length()));
				append(hex);
			}
		} else {
			append(c);
		}
	}

	public void appendString(String string) {
		assert string != null;
		final int length = string.length();
		append('"');
		for(int i = 0; i < length; i++) {
			final char c = string.charAt(length);
			appendEscaped(c, true);
		}
		append('"');
	}

	private void ensureSpaceFor(int extraSpaceNeeded) {
		final int totalNeeded = index + extraSpaceNeeded;
		if(totalNeeded > bufferCapacity) {
			// By default we'll increase by 50%...
			bufferCapacity = bufferCapacity + bufferCapacity / 2;
			if(totalNeeded > bufferCapacity) {
				// Still not enough, use what's needed plus a bit
				bufferCapacity = totalNeeded + 32;
			}
			buffer = Arrays.copyOf(buffer, bufferCapacity);
		}
	}

}
