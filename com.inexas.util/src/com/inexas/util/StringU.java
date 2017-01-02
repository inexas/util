package com.inexas.util;

import java.math.BigInteger;
import java.security.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import com.google.gson.Gson;
import com.inexas.exception.UnexpectedException;
import com.inexas.util.ReflectionU.ReflectException;

public class StringU {
	public final static Pattern validMd5 = Pattern.compile("[0-9A-Fa-f]{64}");
	public final static Pattern validHex = Pattern.compile("[0-9A-Fa-f]+");

	private final static String nameRegExp = "[a-zA-Z_][0-9A-Za-z_]{0,63}";
	public final static Pattern validName = Pattern.compile(nameRegExp);

	// Path: '/' ( ( Name '/' )* Name )?
	private final static String absolutePathRegExp = "/((" + nameRegExp + "/)*" + nameRegExp + ")?";
	public final static Pattern validAbsolutePath = Pattern.compile(absolutePathRegExp);

	public static String stripQuotes(String string) {
		assert string != null : "Null string";
		final int length = string.length();
		assert length >= 2 : "String note quoted: " + string;
		assert string.charAt(0) == string.charAt(length - 1);
		assert string.charAt(0) == '\'' || string.charAt(0) == '\"' : "String not quoted: " + string;

		// All those asserts just for this!!
		return string.substring(1, length - 1);
	}

	public static boolean isEmpty(String string) {
		return string == null || string.trim().length() == 0;
	}

	public static String getGetterName(String name) {
		assert name != null && name.length() > 0;
		final Text sb = new Text();
		sb.append("get");
		sb.append(Character.toUpperCase(name.charAt(0)));
		sb.append(name.substring(1));
		return sb.toString();
	}

	public static int crc(String string) {
		int result = 0;
		final char[] ca = string.toCharArray();
		for(final char c : ca) {
			// Rotate left result 1 bit, xor with next c...
			if(result < 0) {
				// Top bit set, set bit 0 to 1
				result = ((result << 1) | 1) ^ c;
			} else {
				result = (result << 1) ^ c;
			}
		}
		return result;
	}

	public static String toCamelCase(String string) {
		final String[] parts = string.split("_");
		final Text sb = new Text();
		for(final String part : parts) {
			sb.append(toProperCase(part));
		}
		return sb.toString();
	}

	public static String toProperCase(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
	}

	/**
	 * Process a string so that it can be using in a SQL statement. That is turn
	 * null into "" and all apostrophes into double apostrophes.
	 *
	 * @param input
	 *            The string to escape.
	 * @return The escaped string.
	 */
	public static String escapeForSql(String input) {
		final String result;
		if(input == null) {
			result = "";
		} else {
			final char[] ca = input.toCharArray();
			int count = 0;
			for(final char c : ca) {
				if(c == '\'') {
					count++;
				}
			}
			if(count == 0) {
				result = input;
			} else {
				final char[] output = new char[ca.length + count];
				int index = 0;
				for(final char c : ca) {
					if(c == '\'') {
						output[index++] = '\'';
					}
					output[index++] = c;
				}
				result = new String(output);
			}
		}
		return result;
	}

	public static String getMd5(String plainText) {
		try {
			String result;
			final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(plainText.getBytes());
			final byte[] digest = messageDigest.digest();
			final BigInteger bigInt = new BigInteger(1, digest);
			result = bigInt.toString(16);

			// Zero pad to 32 characters....
			if(result.length() < 32) {
				result = "00000000000000000000000000000000".substring(result.length()) + result;
				assert result.length() == 32;
			}
			return result;
		} catch(final NoSuchAlgorithmException e) {
			throw new RuntimeException("Error making MD5", e);
		}
	}

	public static void subString(String source, int maxLength, Text result) {
		final char[] ca = source.toCharArray();
		final int count = ca.length < maxLength ? ca.length : maxLength;
		for(int i = 0; i < count; i++) {
			final char c = ca[i];
			switch(c) {
			case '\n':
				result.append("\n");
				break;
			case '\r':
				break;
			case '\t':
				result.append("\t");
				break;
			default:
				result.append(c);
			}
		}
	}

	public static String getPassword(int n) {
		final char[] pw = new char[n];
		int c = 'A';
		int r1 = 0;
		for(int i = 0; i < n; i++) {
			r1 = (int)(Math.random() * 3);
			switch(r1) {
			case 0:
				c = '0' + (int)(Math.random() * 10);
				break;
			case 1:
				c = 'a' + (int)(Math.random() * 26);
				break;
			case 2:
				c = 'A' + (int)(Math.random() * 26);
				break;
			default:
				throw new UnexpectedException("Should never be called");
			}
			pw[i] = (char)c;
		}
		return new String(pw);
	}

	/**
	 * Remove quotes from a string.
	 *
	 * @param string
	 *            The string to process. Cannot be null.
	 * @param quoteChars
	 *            Type of quotes to remove. Default is double quotes. You can
	 *            specify one several of ", ' or `. The type of quotes must be
	 *            the same.
	 * @return The unquoted string.
	 * @throws RuntimeException
	 *             if the string is null or has not quotes.
	 */
	public static String removeQuotes(String string, char... quoteChars) {
		if(string == null || hasQuotes(string, quoteChars)) {
			throw new RuntimeException("String has no quotes: " + string);
		}
		return string.substring(1, string.length() - 1);
	}

	/**
	 * @param string
	 *            String to check
	 * @param quoteChars
	 *            Possible quote characters
	 * @return Return true if the String has matching quotes
	 */
	public static boolean hasQuotes(String string, char[] quoteChars) {
		boolean result;

		assert string != null;

		final int length = string.length();
		if(length < 2) {
			result = false;
		} else {
			final char first = string.charAt(0);
			final char last = string.charAt(length - 1);
			if(first == last) {
				final int quotes = quoteChars.length;
				if(quotes == 0) {
					result = first == '"';
				} else {
					result = false;
					for(final char quote : quoteChars) {
						if(first == quote) {
							result = true;
							break;
						}
					}
				}
			} else {
				// ?todo Could also strip [], etc.
				result = false;
			}
		}

		return result;
	}

	private final static boolean[] validNameChars = validNameChars();

	private final static boolean[] validNameChars() {
		final boolean[] result = new boolean['z'];
		for(int c = 0; c < result.length; c++) {
			if(c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_') {
				result[c] = true;
			}
		}
		return result;
	}

	public static boolean isValidNameOrDots(String candidate) {
		return isValidName(candidate, true);
	}

	private static boolean isValidName(String candidate, boolean dots) {
		boolean result;
		if(candidate == null) {
			result = false;
		} else {
			final int length = candidate.length();
			if(length == 0 || length > 64) {
				result = false;
			} else {
				result = true;
				final char ca[] = candidate.toCharArray();
				for(final char c : ca) {
					if(c > 'z' || !validNameChars[c]) {
						result = false;
						break;
					}
				}
				if(!result && dots) {
					if(ca[0] == '.') {
						result = length == 1 ? true : length == 2 && ca[1] == '.';
					}
				}
				if(ca[0] <= '9') {
					result = false;
				}
			}
		}
		return result;
	}

	/**
	 * LFs are converted to "\n", quotes to "\"" and CRs are discarded
	 *
	 * @param string
	 *            The (not null) source String
	 * @param result
	 *            The processed text
	 */
	public static void escapeNewlinesAndQuotes(String string, Text result) {
		assert result != null;
		for(final char c : string.toCharArray()) {
			switch(c) {
			case '\\': // Escape non-escape slashes
				result.append("\\\\");
				break;

			case '\r': // Discard carriage returns
				break;

			case '\n':
				result.append("\\n");
				break;

			case '"':
				result.append("\\\"");
				break;

			default:
				result.append(c);
				break;
			}
		}
	}

	public static String toDelimitedString(Collection<? extends Object> items) {
		final Text result = new Text();
		boolean delimiter = false;
		for(final Object item : items) {
			if(delimiter) {
				result.append(", ");
			} else {
				delimiter = true;
			}
			result.append(item.toString());
		}
		return result.toString();
	}

	public static String toDelimitedString(Object[] items) {
		final Text result = new Text();
		boolean delimiter = false;
		for(final Object item : items) {
			if(delimiter) {
				result.append(", ");
			} else {
				delimiter = true;
			}
			result.append(item.toString());
		}
		return result.toString();
	}

	public static String stringify(Object[] items) {
		final String result;

		if(items == null) {
			result = null;
		} else {
			final Text t = new Text(false);
			for(final Object item : items) {
				t.delimit();
				if(item == null) {
					t.append("\\0");
				} else {
					final char[] ca = item.toString().toCharArray();
					for(final char c : ca) {
						switch(c) {
						case '\\':
							t.append("\\\\");
							break;
						case ',':
							t.append("\\,");
							break;
						default:
							t.append(c);
							break;
						}
					}
				}
			}
			result = t.toString();
		}

		return result;
	}

	/**
	 * See {@link #stringify(Collection, boolean)}
	 *
	 * @param collection
	 *            The collection to stringify
	 * @return A string representation of the collection.
	 */
	public static String stringify(Collection<?> collection) {
		return stringify(collection, true);
	}

	/**
	 * Stringify a collection of objects. The collection is turned into a comma
	 * separated list. Null entries are coded as "\0" otherwise the object's
	 * toString() method is used to convert the object to a string and then the
	 * string is escaped using '\' to escape '\' and ',' characters.
	 *
	 * @param collection
	 *            The collection to stringify
	 * @param pretty
	 *            Include spaces after the ,
	 * @return A string representation of the collection.
	 * @see
	 */
	public static String stringify(Collection<?> collection, boolean pretty) {
		final String result;

		if(collection == null) {
			result = null;
		} else {
			final Text sb = new Text(pretty);
			for(final Object item : collection) {
				sb.delimit();
				if(item == null) {
					sb.append("\\0");
				} else {
					final char[] ca = item.toString().toCharArray();
					for(final char c : ca) {
						switch(c) {
						case '\\':
							sb.append("\\\\");
							break;
						case ',':
							sb.append("\\,");
							break;
						default:
							sb.append(c);
							break;
						}
					}
				}
			}
			result = sb.toString();
		}

		return result;
	}

	public static String[] destringifyStringArray(String commaDelimitedStrings) {
		final String[] result;
		if(commaDelimitedStrings == null) {
			result = null;
		} else {
			final List<String> array = new ArrayList<>();
			final Text t = new Text();
			final char[] ca = commaDelimitedStrings.toCharArray();
			final int length = ca.length;
			for(int i = 0; i < length; i++) {
				final char c = ca[i];
				switch(c) {
				case '\\': // Escape
					if(i == length - 1) {
						// No next character
						throw new RuntimeException("Invalid: " + commaDelimitedStrings);
					}
					final char next = ca[++i];
					switch(next) {
					case '0': // \0 is a null
						if(t.length() != 0
								|| i == length - 1
								|| ca[++i] != ',') {
							throw new RuntimeException("Invalid: " + commaDelimitedStrings);
						}
						array.add(null);
						break;

					case ',':
					case '\\':
						t.append(next);
						break;

					default:
						throw new RuntimeException("Invalid: " + commaDelimitedStrings);
					}
					break;

				case ',':
					array.add(t.toString());
					t.recycle();
					break;

				default:
					t.append(c);
					break;
				}
			}
			array.add(t.toString());
			result = array.toArray(new String[array.size()]);
		}
		return result;
	}

	public static Integer parseOxHex(String text) {
		return Integer.valueOf(text.substring(2), 16);
	}

	/**
	 * Escape and optionally quote a string. Only the quote character and \ will
	 * be escaped.
	 *
	 * @param string
	 *            The String to process, may be null
	 * @param quoteChar
	 *            Typically ' or "
	 * @param quote
	 *            Add start and end quotes to the string: ab"cd because "ab\"cd"
	 * @return null if string was null otherwise escaped and quoted string
	 */
	public static String escape(String string, char quoteChar, boolean quote) {
		final String result;

		if(string == null) {
			result = null;
		} else {
			final Text t = new Text();
			if(quote) {
				t.append(quoteChar);
			}
			final char[] ca = string.toCharArray();
			for(final char c : ca) {
				if(c == quoteChar || c == '\\') {
					t.append('\\');
				}
				t.append(c);
			}
			if(quote) {
				t.append(quoteChar);
			}
			result = t.toString();
		}

		return result;
	}

	public static String unescape(String string, char quoteChar, boolean unquote) {
		final String result;

		if(string == null) {
			result = null;
		} else {
			final char[] ca = string.toCharArray();
			final int start, length;
			if(unquote) {
				start = 1;
				length = ca.length - 2;
				if(length < 0 || ca[0] != quoteChar || ca[length + 1] != quoteChar) {
					throw new RuntimeException("Invalid string: " + string);
				}
			} else {
				start = 0;
				length = ca.length;
			}
			final Text sb = new Text();
			for(int i = start; i < length; i++) {
				final char c = ca[i];
				if(c == quoteChar || c == '\\') {
					sb.append('\\');
				}
				sb.append(c);
			}
			result = sb.toString();
		}

		return result;
	}

	/**
	 * Convert to a map to "key:classname;key:classname1...". Used for
	 * persistence.
	 *
	 * @param map
	 *            the Map to process.
	 * @param <T>
	 *            The type of the Map to stringify.
	 * @return A stringified version of the map.
	 */
	public static <T> String stringify(Map<String, Class<T>> map) {
		final String result;
		if(map == null) {
			result = null;
		} else {
			final Text sb = new Text();
			boolean delimit = false;
			for(final Entry<String, Class<T>> entry : map.entrySet()) {
				if(delimit) {
					sb.append(';');
				} else {
					delimit = true;
				}
				sb.append(entry.getKey());
				sb.append(':');
				sb.append(entry.getValue().getName());
			}
			result = sb.toString();
		}
		return result;
	}

	public static <T> Map<String, Class<T>> destringify(String string, Class<T> type) {
		final Map<String, Class<T>> result;
		if(string == null) {
			result = null;
		} else {
			try {
				result = new HashMap<>();
				final Text key = new Text();
				final Text className = new Text();
				boolean buildKey = true;
				for(final char c : string.toCharArray()) {
					if(c == ':') {
						buildKey = false;
					} else if(c == ';') {
						final String name = className.toString();
						final Class<T> clazz = ReflectionU.getClass(name, type);
						result.put(key.toString(), clazz);
						buildKey = true;
					} else {
						if(buildKey) {
							key.append(c);
						} else {
							className.append(c);
						}
					}
				}
				final String name = className.toString();
				final Class<T> clazz = ReflectionU.getClass(name, type);
				result.put(key.toString(), clazz);
			} catch(final ReflectException e) {
				throw new RuntimeException("Error loading", e);
			}
		}
		return result;
	}

	public static String stringify(int[] array) {
		final String result;
		if(array == null) {
			result = null;
		} else {
			final Text t = new Text(false);
			for(final int i : array) {
				t.delimit();
				t.append(i);
			}
			result = t.toString();
		}
		return result;
	}

	public static String stringify(long[] array) {
		final String result;
		if(array == null) {
			result = null;
		} else {
			final Text t = new Text();
			for(final long l : array) {
				t.delimit();
				t.append(l);
			}
			result = t.toString();
		}
		return result;
	}

	public static int[] destringifyIntArray(String commaDelimitedInts) {
		final int[] result;

		if(commaDelimitedInts == null) {
			result = null;
		} else {
			final StringTokenizer st = new StringTokenizer(commaDelimitedInts, ",");
			final int count = st.countTokens();
			result = new int[count];
			int index = 0;
			while(st.hasMoreTokens()) {
				result[index++] = Integer.parseInt(st.nextToken());
			}
		}

		return result;
	}

	public static long[] destringifyLongArray(String commaDelimitedLongs) {
		final long[] result;

		if(commaDelimitedLongs == null) {
			result = null;
		} else {
			final StringTokenizer st = new StringTokenizer(commaDelimitedLongs, ",");
			final int count = st.countTokens();
			result = new long[count];
			int index = 0;
			while(st.hasMoreTokens()) {
				result[index++] = Long.parseLong(st.nextToken());
			}
		}

		return result;
	}

	public static String stringifyKeyedObject(String key, Object object) {
		final Gson gson = new Gson();
		final Text t = new Text(key);
		t.append(':');
		t.append(object.getClass().getName());
		t.append(':');
		final String json = gson.toJson(object);
		t.append(json);
		return t.toString();
	}

	public static <T> Pair<String, T> destringifyKeyedObject(String string) {
		try {
			final Gson gson = new Gson();

			// Key...
			final int colon = string.indexOf(':');
			final String key = string.substring(0, colon);

			// Class...
			final int secondColon = string.indexOf(':', colon + 1);
			final String className = string.substring(colon + 1, secondColon);
			final Class<?> clazz = Class.forName(className);

			// JSON...
			final String json = string.substring(secondColon + 1);
			@SuppressWarnings("unchecked")
			final T value = (T)gson.fromJson(json, clazz);

			return new Pair<>(key, value);
		} catch(final Exception e) {
			final String first100 = string.substring(0, Math.min(100, string.length()));
			throw new RuntimeException("Error deserializing JSON: " + first100, e);
		}
	}

	public static String[] splitPath(String path) {
		final String[] result;

		if(path == null) {
			result = null;
		} else {
			final StringTokenizer st = new StringTokenizer(path, "/");
			result = new String[st.countTokens()];
			int i = 0;
			while(st.hasMoreTokens()) {
				result[i++] = st.nextToken();
			}
		}

		return result;
	}

	/**
	 * Compares two version strings.
	 *
	 * Use this instead of String.compareTo() for a non-lexicographical
	 * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
	 *
	 * @param version1
	 *            a string of ordinal numbers separated by dots.
	 * @param version2
	 *            a string of ordinal numbers separated by dots.
	 * @return The result is a negative integer if version1 is numerically less
	 *         than version2. The result is a positive integer if version1 is
	 *         numerically greater than version2. The result is zero if the
	 *         strings are numerically equal.
	 */
	public static int compareVersions(String version1, String version2) {
		int result = 0;

		assert version1 != null && version2 != null;

		final String[] values0 = version1.split("\\.");
		final int length0 = values0.length;

		final String[] values1 = version2.split("\\.");
		final int length1 = values1.length;

		final int length = length0 > length1 ? length0 : length1;

		for(int i = 0; i < length; i++) {
			final int value0 = i >= length0 ? 0 : Integer.parseInt(values0[i]);
			final int value1 = i >= length1 ? 0 : Integer.parseInt(values0[i]);
			result = value1 - value0;
			if(result == 0) {
				continue;
			}

			result = result < 0 ? -1 : 1;
		}

		return result;
	}

	/**
	 * Process the string so that it can appear in Java code. The string should
	 * not include quotes so to process "a" string should have a length of 1.
	 *
	 * @param string
	 *            The string to process, may be null or empty string.
	 * @return For null return "null", for other strings convert "\n" to "\\n",
	 *         etc.
	 */
	public static String toJavaString(String string) {
		final String result;

		if(string == null) {
			result = "null";
		} else {
			final Text t = new Text();
			final char[] ca = string.toCharArray();
			for(final char c : ca) {
				switch(c) {
				case '\r':
					// Discard
					break;
				case '\n':
					t.append("\\n");
					break;
				case '\t':
					t.append("\\t");
					break;
				case '"':
					t.append("\\\"");
					break;
				default:
					t.append(c);
					break;
				}
			}
			result = t.toString();
		}

		return result;
	}

	public static boolean isValidName(String candidate) {
		return validName.matcher(candidate).matches();
	}

	public static boolean isValidAbsolutePath(String candidate) {
		return validAbsolutePath.matcher(candidate).matches();
	}

	public static boolean isValidHex(String candidate) {
		return validHex.matcher(candidate).matches();
	}

	public static String summary(String string, int maximumLength) {
		final Text result;

		assert maximumLength >= 1;

		if(string == null) {
			result = null;
		} else {
			result = new Text();
			final int processinglenth;
			final boolean elipses;
			final int stringLength = string.length();
			if(stringLength > maximumLength) {
				processinglenth = maximumLength;
				elipses = true;
			} else {
				processinglenth = stringLength;
				elipses = false;
			}
			for(int i = 0; i < processinglenth; i++) {
				final char c = string.charAt(i);
				result.appendEscaped(c, false);
			}
			if(elipses) {
				result.append("...");
			}
		}

		return result == null ? "<null>" : result.toString();
	}

	/**
	 * Convert a character to unicode string format, e.g. 'A' becomes "\u0041".
	 *
	 * @param c
	 *            The character to convert.
	 * @return The unicode string representing c.
	 */
	public static String toUnicode(char c) {
		final String hex = Integer.toHexString(c);
		return "\\u000".substring(0, 6 - hex.length()) + hex;
	}
}
