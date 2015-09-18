/*
 * Copyright (C) 2015 Processwide AG. All Rights Reserved. DO NOT ALTER OR
 * REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is provided as-is without warranty of merchantability or fitness for a
 * particular purpose.
 *
 * See http://www.inexas.com/license for license details.
 */

package com.inexas.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestText {

	private void test(byte mask, int expectedCount) {
		final Text t = new Text();
		int count = 0;
		for(char i = 0; i < 255; i++) {
			t.recycle();
			t.append(i);
			if(t.consumeAscii(mask)) {
				count++;
			}
		}
		assertEquals(expectedCount, count);
	}

	private void test(int c, byte mask, boolean expected) {
		final Text t = new Text();
		t.append((char)c);
		assertTrue(expected == t.consumeAscii(mask));
	}

	private void doValueTest(boolean expectedResult, String toTest) {
		doValueTest(expectedResult, toTest, toTest);
	}

	private void doValueTest(boolean expectedResult, Object expectedObject, String toTest) {
		final Text t = new Text();
		t.append(toTest);
		final int start = t.cursor();
		if(t.consumeString()) {
			assertTrue(expectedResult);
			assertEquals(expectedObject, t.getString(start));
		} else {
			assertFalse(expectedResult);
		}
	}

	private void doConsumeAsciiTest(String expected, String source, byte bitmap, int... constraints) {
		final Text t = new Text();
		t.append(source);
		if(t.consumeAscii(bitmap, constraints)) {
			final String actual = t.getConsumed();
			assertEquals(expected, actual);
		} else {
			assertNull(expected);
		}
	}

	@Test
	public void testAsciiTable() {
		test(Text.ASCII_0_9, 10);
		test('0' - 1, Text.ASCII_0_9, false);
		test('0', Text.ASCII_0_9, true);
		test('9', Text.ASCII_0_9, true);
		test('9' + 1, Text.ASCII_0_9, false);

		test(Text.ASCII_1_9, 9);
		test('1' - 1, Text.ASCII_1_9, false);
		test('1', Text.ASCII_1_9, true);
		test('9', Text.ASCII_1_9, true);
		test('9' + 1, Text.ASCII_1_9, false);

		test(Text.ASCII_A_Z, 26);
		test('A' - 1, Text.ASCII_A_Z, false);
		test('A', Text.ASCII_A_Z, true);
		test('Z', Text.ASCII_A_Z, true);
		test('Z' + 1, Text.ASCII_A_Z, false);

		test(Text.ASCII_a_z, 26);
		test('a' - 1, Text.ASCII_a_z, false);
		test('a', Text.ASCII_a_z, true);
		test('z', Text.ASCII_a_z, true);
		test('z' + 1, Text.ASCII_a_z, false);

		test(Text.ASCII_UNDERLINE, 1);
		test('_' - 1, Text.ASCII_UNDERLINE, false);
		test('_', Text.ASCII_UNDERLINE, true);
		test('_' + 1, Text.ASCII_UNDERLINE, false);

		test(Text.ASCII_0_1, 2);
		test('0', Text.ASCII_0_1, true);
		test('1', Text.ASCII_0_1, true);

		test(Text.ASCII_0_F, 22);
		test('0' - 1, Text.ASCII_0_F, false);
		test('0', Text.ASCII_0_F, true);
		test('9', Text.ASCII_0_F, true);
		test('9' + 1, Text.ASCII_0_F, false);
		test('a' - 1, Text.ASCII_0_F, false);
		test('a', Text.ASCII_0_F, true);
		test('f', Text.ASCII_0_F, true);
		test('f' + 1, Text.ASCII_0_F, false);
		test('A' - 1, Text.ASCII_0_F, false);
		test('A', Text.ASCII_0_F, true);
		test('F', Text.ASCII_0_F, true);
		test('F' + 1, Text.ASCII_0_F, false);
	}

	@Test
	public void testValue() {
		// Simple strings...
		doValueTest(true, "\"\"");
		doValueTest(true, "\"a\"");
		doValueTest(true, "\"abc\t\r\"");

		// Escapes...
		doValueTest(true, "\"\\\"\"");

		// Apostrophes
		doValueTest(true, "''");
		doValueTest(true, "'abc'");
		doValueTest(true, "'a \\' b'");

		// Back-apostrophes
		doValueTest(true, "``");
		doValueTest(true, "`abc`");

		// Fails
		doValueTest(false, ""); // No opening quote
		doValueTest(false, "'`"); // No closing quote
		doValueTest(false, "'\n'"); // Newline before close
		doValueTest(false, "'\\"); // Escape at end
		doValueTest(false, "'\\'"); // Escaped close
	}

	@Test
	public void testConsumeAscii() {
		doConsumeAsciiTest("123", "123a", Text.ASCII_0_9);
		doConsumeAsciiTest("12", "123a", Text.ASCII_0_9, 2);
		doConsumeAsciiTest("123", "123a", Text.ASCII_0_9, 2, 3);
		doConsumeAsciiTest(null, "123a", Text.ASCII_a_z);
	}

}
