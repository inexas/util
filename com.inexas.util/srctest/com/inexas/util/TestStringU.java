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
			final TextBuilder sb = new TextBuilder();
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
}
