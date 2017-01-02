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

public class TestParser {

	private void doValueTest(boolean expectedResult, String toTest) {
		doValueTest(expectedResult, toTest, toTest);
	}

	private void doValueTest(boolean expectedResult, Object expectedObject, String toTest) {
		final Parser t = new Parser(toTest);
		final int start = t.cursor();
		if(t.consumeString()) {
			assertTrue(expectedResult);
			assertEquals(expectedObject, t.getString(start));
		} else {
			assertFalse(expectedResult);
		}
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
