package com.inexas.util;

import static org.junit.Assert.assertEquals;
import java.time.*;
import org.junit.Test;

public class TestDateU {

	private void doStandardTimeTest(String expected, String toTest) {
		final LocalTime time = DateU.parseStandardTime(toTest);
		final String actual = DateU.formatStandardTime(time);
		assertEquals(expected, actual);
	}

	private void doStandardDateTest(String expected, String toTest) {
		final LocalDate date = DateU.parseStandardDate(toTest);
		final String actual = DateU.formatStandardDate(date);
		assertEquals(expected, actual);
	}

	private void doStandardDatetimeTest(String expected, String toTest) {
		final LocalDateTime datetime = DateU.parseStandardDatetime(toTest);
		final String actual = DateU.formatStandardDatetime(datetime);
		assertEquals(expected, actual);
	}

	@Test
	public void testStandardTime() {
		doStandardTimeTest("12:34:56.700", "12:34:56.7");
		doStandardTimeTest("12:34:56.007", "12:34:56.007"); // Bond, James Bond
		doStandardTimeTest("12:34:56", "12:34:56");
		doStandardTimeTest("12:34", "12:34");
		doStandardTimeTest("12:34", "12:34:00.000");
	}

	@Test
	public void testStandardDate() {
		doStandardDateTest("1957/04/30", "1957/04/30");
		doStandardDateTest("1957/04/07", "1957/4/7");
	}

	@Test
	public void testStandardDatetime() {
		doStandardDatetimeTest("1957/04/30 12:34:56.007", "1957/04/30 12:34:56.007");
		doStandardDatetimeTest("1957/04/07 12:34", "1957/4/7 12:34");
	}
}
