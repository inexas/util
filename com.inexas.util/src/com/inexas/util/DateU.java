package com.inexas.util;

import java.text.*;
import java.time.*;
import java.util.Date;
import com.sun.istack.internal.Nullable;

public class DateU {
	private static final DateFormat compactDatetime = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final DateFormat airlineDatetime = new SimpleDateFormat("ddMMMyy HH:mm:ss");
	private final static DateFormat sqlDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static DateFormat sqlDate = new SimpleDateFormat("yyyy-MM-dd");
	public final static String MESSAGE =
			"Date should be in format 'yyyy/MM/dd HH:mm:ss', you may have " +
					"date only, time only or date and time all units in descending " +
					"order: ";

	/**
	 * Date time hours minutes seconds as compact as possible
	 *
	 * @param date
	 *            date to format
	 * @return Example: yyyyMMddHHmmss "20133004121621"
	 */
	public static String formatDatetimeCompact(Date date) {
		return compactDatetime.format(date);
	}

	/**
	 * @param date
	 *            date to parse, example: yyyyMMddHHmmss "20133004121621"
	 * @return parsed date
	 */
	public static Date parseDatetimeCompact(String date) {
		try {
			return compactDatetime.parse(date);
		} catch(final ParseException e) {
			throw new RuntimeException("Error parsing date: " + date, e);
		}
	}

	/**
	 * Format a date into the airline time
	 *
	 * @param date
	 *            date to format
	 * @return Example: ddMMMyy HH:mm:ss "30APR1957 14:21:16"
	 */
	public static String formatDatetimeAirline(Date date) {
		return airlineDatetime.format(date).toUpperCase();
	}

	/**
	 * Parse an airline type date time
	 *
	 * @param date
	 *            date to parse, example: ddMMMyy HH:mm:ss "30APR1957 14:21:16"
	 * @return parsed date
	 */
	public static Date parseDatetimeAirline(String date) {
		try {
			return airlineDatetime.parse(date);
		} catch(final ParseException e) {
			throw new RuntimeException("Error parsing date: " + date, e);
		}
	}

	public static Date subtract(long intervalMs) {
		return new Date(new Date().getTime() - intervalMs);
	}

	public static long intervalInSeconds(Date date) {
		return intervalInSeconds(new Date(), date);
	}

	public static long intervalInSeconds(Date from, Date to) {
		return Math.abs(from.getTime() - to.getTime()) / 1000;
	}

	public static String formatDatetimeSql(Date value) {
		return sqlDatetime.format(value);
	}

	public static String formatDateSql(Date value) {
		return sqlDate.format(value);
	}

	/*
	 * 'Standard' date time format
	 *
	 * After a lot of work and trying to figure ways around bugs in the Java
	 * library using Text is easier
	 *
	 *
	 * Bug: http://stackoverflow.com/questions/22588051/
	 */

	/**
	 * Parse a datetime in standard format.
	 *
	 * @param datetime
	 *            A datetime in the format "yyyy/m/d hh:mm(:ss(.nnn)?)?".
	 * @return The parsed date or null if one cannot be parsed.
	 */
	@Nullable
	public static LocalDateTime parseStandardDatetime(String datetime) {
		final LocalDateTime result;

		final Text t = new Text();
		t.append(datetime);
		LocalDate date;
		LocalTime time;
		if((date = date(t)) != null && t.consume(' ') && (time = time(t)) != null) {
			result = LocalDateTime.of(date, time);
		} else {
			result = null;
		}

		return result;
	}

	/**
	 * Parse a date in standard format.
	 *
	 * @param date
	 *            A date in the format "yyyy/m/d".
	 * @return The parsed date or null if one cannot be parsed.
	 */
	public static LocalDate parseStandardDate(String date) {
		final Text t = new Text();
		t.append(date);
		return date(t);
	}

	/**
	 * Parse a time in standard format.
	 *
	 * @param time
	 *            A time in the format "hh:mm(:ss(.nnn)?)?".
	 * @return The parsed time or null if one cannot be parsed.
	 */
	public static LocalTime parseStandardTime(String time) {
		final Text t = new Text();
		t.append(time);
		return time(t);
	}

	/*
	 * 'Standard' date time format
	 *
	 * After a lot of work and trying to figure ways around bugs in the Java
	 * library using Text is easier
	 *
	 *
	 * Bug: http://stackoverflow.com/questions/22588051/
	 */

	/**
	 * Format a given datetime in standard format.
	 *
	 * @param datetime
	 *            The datetime to format.
	 * @return yyyy/mm/dd hh:mm(:ss(.ms)?)?
	 */
	public static String formatStandardDatetime(LocalDateTime datetime) {
		final Text t = new Text();
		formatStandardDatetime(datetime, t);
		return t.toString();
	}

	/**
	 * Format a given datetime in standard format.
	 *
	 * @param datetime
	 *            The datetime to format.
	 * @param t
	 *            The recipient of the text: yyyy/mm/dd hh:mm(:ss(.ms)?)?
	 */
	public static void formatStandardDatetime(LocalDateTime datetime, Text t) {
		date(datetime.getYear(), datetime.getMonthValue(), datetime.getDayOfMonth(), t);
		t.append(' ');
		time(datetime.getHour(), datetime.getMinute(), datetime.getSecond(), datetime.getNano(), t);
	}

	/**
	 * Format a given date in standard format.
	 *
	 * @param date
	 *            The date to format.
	 * @return yyyy/mm/dd
	 */
	public static String formatStandardDate(LocalDate date) {
		final Text t = new Text();
		formatStandardDate(date, t);
		return t.toString();
	}

	public static void formatStandardDate(LocalDate date, Text t) {
		date(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), t);
	}

	/**
	 * Format a given time in standard format.
	 *
	 * @param time
	 *            The time to format.
	 * @return hh:mm(:ss(.ms)?)?
	 */
	public static String formatStandardTime(LocalTime time) {
		final Text t = new Text();
		formatStandardTime(time, t);
		return t.toString();
	}

	public static void formatStandardTime(LocalTime time, Text t) {
		time(time.getHour(), time.getMinute(), time.getSecond(), time.getNano(), t);
	}

	private static LocalDate date(Text t) {
		final LocalDate result;
		// digit '/' digit '/' digit

		final Integer year;
		final Integer month;
		final Integer day;

		final int save = t.cursor();
		if((year = digit(t, 4, 4)) != null
				&& t.consume('/') && (month = digit(t, 1, 2)) != null
				&& t.consume('/') && (day = digit(t, 1, 2)) != null) {
			result = LocalDate.of(
					year.intValue(),
					month.intValue(),
					day.intValue());
		} else {
			result = null;
			t.setCursor(save);
		}

		return result;
	}

	private static LocalTime time(Text t) {
		final LocalTime result;
		// digit{2} ':' digit{2} ( ':' digit{2} ( '.' digit{2} )? )?

		final Integer hour;
		final Integer minute;
		Integer second = null;
		int millisecond = 0;

		final int save = t.cursor();
		if((hour = digit(t, 2, 3)) != null && t.consume(':') && (minute = digit(t, 2, 2)) != null) {
			final int save1 = t.cursor();
			if(t.consume(':') && (second = digit(t, 2, 2)) != null) {
				final int save2 = t.cursor();
				if(t.consume('.') && t.consumeAscii(Text.ASCII_0_9, 1, 3)) {
					final String s = (t.getConsumed() + "00").substring(0, 3);
					for(int i = 0; i < 3; i++) {
						millisecond = millisecond * 10 + s.charAt(i) - '0';
					}
				} else {
					t.setCursor(save2);
				}
			} else {
				t.setCursor(save1);
			}

			result = LocalTime.of(
					hour.intValue(),
					minute.intValue(),
					second == null ? 0 : second.intValue(),
							millisecond * 1_000_000);
		} else {
			result = null;
			t.setCursor(save);
		}

		return result;
	}

	private static Integer digit(Text t, int n, int m) {
		final Integer result;

		final int start = t.cursor();
		if(t.consumeAscii(Text.ASCII_0_9, n, m)) {
			final int count = t.cursor() - start;
			int total = 0;
			for(int i = 0; i < count; i++) {
				total = total * 10 + t.charAt(start + i) - '0';
			}
			result = new Integer(total);
		} else {
			result = null;
		}

		return result;
	}

	private static void date(int year, int month, int day, Text t) {
		pad(year, t);
		t.append('/');
		pad(month, t);
		t.append('/');
		pad(day, t);
	}

	private static void pad(int i, Text t) {
		if(i < 10) {
			t.append('0');
		}
		t.append(i);
	}

	private static void time(int hour, int minute, int second, int nano, Text t) {
		pad(hour, t);
		t.append(':');
		pad(minute, t);
		if(second != 0 || nano != 0) {
			t.append(':');
			pad(second, t);
			final int ms = nano / 1_000_000;
			if(ms != 0) {
				t.append('.');
				if(ms < 100) {
					t.append('0');
				}
				if(ms < 10) {
					t.append('0');
				}
				t.append(ms);
			}
		}
	}

}
