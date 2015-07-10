package com.inexas.util;

import java.util.Arrays;

/**
 * Very (expletive deleted) annoyingly StringBuilder is final so we can't extend
 * it to add the 'pretty' functionality.
 */
public class TextBuilder implements CharSequence {
	private final static int maxLineLength = 132;
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

	public void reset() {
		bufferCapacity = 0;
		lastNewline = 0;
		index = 0;
		indent.reset();
		delimit = false;
	}

	@Override
	public int length() {
		return index;
	}

	@Override
	public char charAt(int offset) {
		if(offset < 0 || offset >= index) {
			throw new StringIndexOutOfBoundsException(index);
		}
		return buffer[offset];
	}

	@Override
	public CharSequence subSequence(int start, int end) {
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

	private void expand(int minimumCapacity) {
		// There's no check for buffer overflow
		bufferCapacity = bufferCapacity * 2;
		if(minimumCapacity > bufferCapacity) {
			bufferCapacity = minimumCapacity;
		}
		buffer = Arrays.copyOf(buffer, bufferCapacity);
	}

	public void setLength(int length) {
		index = length;
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
}
