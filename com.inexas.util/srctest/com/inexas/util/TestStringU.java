package com.inexas.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestStringU {

	private void doTestToStringArray(String expected, String toTest) {
		final String[] strings = StringU.destringifyStringArray(toTest);
		final String got;
		if(strings == null) {
			got = null;
		} else {
			final Text sb = new Text();
			boolean delimit = false;
			for(final String string : strings) {
				if(delimit) {
					sb.append('/');
				} else {
					delimit = true;
				}
				sb.append(string == null ? "<null>" : string);
			}
			got = sb.toString();
			assertEquals(expected, got);
		}
		if(!expected.equals(got)) {
			System.out.println("exp: " + expected);
			System.out.println("got: " + got);

		}
	}

	private void doToAndFromStringArrayTest(String toTest) {
		final String[] strings = StringU.destringifyStringArray(toTest);
		final String got = StringU.stringify(strings);
		if(toTest == null) {
			assertNull(got);
		} else {
			if(!toTest.equals(got)) {
				System.out.println("exp: " + toTest);
				System.out.println("got: " + got);
			}
		}
	}

	private void doTestEscapeNewlinesAndQuotesTest(String toTest, String expectedResult) {
		final Text text = new Text(true);
		StringU.escapeNewlinesAndQuotes(toTest, text);
		assertEquals(expectedResult, text.toString());
	}

	@Test
	public void testToStringArray() {
		doTestToStringArray("a/<null>/b", "a,\\0,b");

		assertNull(StringU.destringifyStringArray(null));
		assertEquals(1, StringU.destringifyStringArray("").length);
		doTestToStringArray("", "");
		doTestToStringArray(" ", " ");
		doTestToStringArray(" a ", " a ");
		doTestToStringArray("a/b", "a,b");
		doTestToStringArray("a/ b", "a, b");
		doTestToStringArray("a//b", "a,,b");
		doTestToStringArray("a/<null>/b", "a,\\0,b");
	}

	@Test
	public void testToAndFromStringArray() {
		doToAndFromStringArrayTest(null);
		doToAndFromStringArrayTest("");
		doToAndFromStringArrayTest("a");
		doToAndFromStringArrayTest("a,b");
		doToAndFromStringArrayTest("a,b,c");
		doToAndFromStringArrayTest("\0");
		doToAndFromStringArrayTest("\0,\0");
		doToAndFromStringArrayTest("\0,\0,");
		doToAndFromStringArrayTest("a\\,b,c");
		doToAndFromStringArrayTest("a\\\\b,c");
	}

	@Test
	public void testToJavaString() {
		assertEquals("null", StringU.toJavaString(null));
		assertEquals("", StringU.toJavaString(""));
		assertEquals("asdf", StringU.toJavaString("asdf"));
		assertEquals("\\n\\t\\\"", StringU.toJavaString("\n\r\t\""));
	}

	@Test
	public void testEscapeNewlinesAndQuotes() {
		doTestEscapeNewlinesAndQuotesTest("\"abc\"\r\n", "\\\"abc\\\"\\n");
		doTestEscapeNewlinesAndQuotesTest("abc", "abc");
		doTestEscapeNewlinesAndQuotesTest("", "");
	}

	@Test
	public void testNames() {
		final String name64 = "a234"
				+ "0123456789" // 10
				+ "0123456789" // 20
				+ "0123456789" // 30
				+ "0123456789" // 40
				+ "0123456789" // 50
				+ "0123456789"; // 60
		assertTrue(StringU.isValidName("A"));
		assertTrue(StringU.isValidName("Z"));
		assertTrue(StringU.isValidName("a"));
		assertTrue(StringU.isValidName("p"));
		assertTrue(StringU.isValidName("z"));
		assertTrue(StringU.isValidName("_"));
		assertTrue(StringU.isValidName("ABC"));
		assertTrue(StringU.isValidName("a0_9_"));
		assertTrue(StringU.isValidName(name64));

		assertFalse(StringU.isValidName("0"));
		assertFalse(StringU.isValidName("9"));
		assertFalse(StringU.isValidName("."));
		assertFalse(StringU.isValidName(name64 + '1'));
	}

	@Test
	public void testPaths() {
		assertTrue(StringU.isValidAbsolutePath("/"));
		assertTrue(StringU.isValidAbsolutePath("/abc"));
		assertTrue(StringU.isValidAbsolutePath("/abc/d"));
		assertTrue(StringU.isValidAbsolutePath("/abc/d/e"));

		assertFalse(StringU.isValidAbsolutePath(""));
		assertFalse(StringU.isValidAbsolutePath("//"));
		assertFalse(StringU.isValidAbsolutePath("/a/"));
		assertFalse(StringU.isValidAbsolutePath("abc"));
		assertFalse(StringU.isValidAbsolutePath("/a//v"));
	}

	@Test
	public void testSummary() {
		assertEquals("123", StringU.summary("123", 4));
		assertEquals("123", StringU.summary("123", 3));
		assertEquals("12...", StringU.summary("123", 2));
		assertEquals("1\\n...", StringU.summary("1\n3", 2));
	}

	@Test
	public void testToUnicode() {
		assertEquals("\\u0001", StringU.toUnicode((char)1));
		assertEquals("\\u0041", StringU.toUnicode('A'));
	}

}
