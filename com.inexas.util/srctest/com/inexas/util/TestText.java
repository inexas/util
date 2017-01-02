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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TestText {

	private void doAppendEscapedTest(String toTest, String expected, boolean escapeQuotes) {
		final Text t = new Text();
		for(int i = 0; i < toTest.length(); i++) {
			t.appendEscaped(toTest.charAt(i), escapeQuotes);
		}
		assertEquals(expected, t.toString());
	}

	@Test
	public void testAppendEscaped() {
		doAppendEscapedTest("", "", true);
		doAppendEscapedTest("abc", "abc", true);
		doAppendEscapedTest("\t\b\n\r\f\"", "\\t\\b\\n\\f\\\"", true);
		doAppendEscapedTest("\t\b\n\r\f\"", "\\t\\b\\n\\f\\\"", true);
		doAppendEscapedTest("\t\b\n\r\f\"", "\\t\\b\\n\\f\"", false);
		doAppendEscapedTest("\\", "\\\\", false);
		doAppendEscapedTest("\u0001", "\\u0001", true);
		doAppendEscapedTest("\u0014", "\\u0014", true);
	}
}
