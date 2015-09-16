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

public class TestTextBuilder {

	private void test(byte mask, int expectedCount) {
		final TextBuilder tb = new TextBuilder();
		int count = 0;
		for(char i = 0; i < 255; i++) {
			tb.recycle();
			tb.append(i);
			if(tb.consumeAscii(mask)) {
				count++;
			}
		}
		assertEquals(expectedCount, count);
	}

	private void test(int c, byte mask, boolean expected) {
		final TextBuilder tb = new TextBuilder();
		tb.append((char)c);
		assertTrue(expected == tb.consumeAscii(mask));
	}

	private void doValueTest(boolean expectedResult, String toTest) {
		doValueTest(expectedResult, toTest, toTest);
	}

	private void doValueTest(boolean expectedResult, Object expectedObject, String toTest) {
		final TextBuilder tb = new TextBuilder();
		tb.append(toTest);
		final int start = tb.cursor();
		if(tb.consumeString()) {
			assertTrue(expectedResult);
			assertEquals(expectedObject, tb.getString(start));
		} else {
			assertFalse(expectedResult);
		}
	}

	@Test
	public void testAsciiTable() {
		test(TextBuilder.ASCII_0_9, 10);
		test('0' - 1, TextBuilder.ASCII_0_9, false);
		test('0', TextBuilder.ASCII_0_9, true);
		test('9', TextBuilder.ASCII_0_9, true);
		test('9' + 1, TextBuilder.ASCII_0_9, false);

		test(TextBuilder.ASCII_1_9, 9);
		test('1' - 1, TextBuilder.ASCII_1_9, false);
		test('1', TextBuilder.ASCII_1_9, true);
		test('9', TextBuilder.ASCII_1_9, true);
		test('9' + 1, TextBuilder.ASCII_1_9, false);

		test(TextBuilder.ASCII_A_Z, 26);
		test('A' - 1, TextBuilder.ASCII_A_Z, false);
		test('A', TextBuilder.ASCII_A_Z, true);
		test('Z', TextBuilder.ASCII_A_Z, true);
		test('Z' + 1, TextBuilder.ASCII_A_Z, false);

		test(TextBuilder.ASCII_a_z, 26);
		test('a' - 1, TextBuilder.ASCII_a_z, false);
		test('a', TextBuilder.ASCII_a_z, true);
		test('z', TextBuilder.ASCII_a_z, true);
		test('z' + 1, TextBuilder.ASCII_a_z, false);

		test(TextBuilder.ASCII_UNDERLINE, 1);
		test('_' - 1, TextBuilder.ASCII_UNDERLINE, false);
		test('_', TextBuilder.ASCII_UNDERLINE, true);
		test('_' + 1, TextBuilder.ASCII_UNDERLINE, false);

		test(TextBuilder.ASCII_0_1, 2);
		test('0', TextBuilder.ASCII_0_1, true);
		test('1', TextBuilder.ASCII_0_1, true);

		test(TextBuilder.ASCII_0_F, 22);
		test('0' - 1, TextBuilder.ASCII_0_F, false);
		test('0', TextBuilder.ASCII_0_F, true);
		test('9', TextBuilder.ASCII_0_F, true);
		test('9' + 1, TextBuilder.ASCII_0_F, false);
		test('a' - 1, TextBuilder.ASCII_0_F, false);
		test('a', TextBuilder.ASCII_0_F, true);
		test('f', TextBuilder.ASCII_0_F, true);
		test('f' + 1, TextBuilder.ASCII_0_F, false);
		test('A' - 1, TextBuilder.ASCII_0_F, false);
		test('A', TextBuilder.ASCII_0_F, true);
		test('F', TextBuilder.ASCII_0_F, true);
		test('F' + 1, TextBuilder.ASCII_0_F, false);
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
}
