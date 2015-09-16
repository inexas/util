package com.inexas.util;

import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.Date;

public class DateU {
	private static final DateFormat compactDateTime = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final DateFormat airlineDateTime = new SimpleDateFormat("ddMMMyy HH:mm:ss");
	private final static DateFormat sqlDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static DateFormat sqlDate = new SimpleDateFormat("yyyy-MM-dd");
	public final static String MESSAGE =
			"Date should be in format 'yyyy/MM/dd HH:mm:ss', you may have " +
					"date only, time only or date and time all units in descending " +
					"order: ";

	private final static DateTimeFormatter standardDateTime =
			DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	private final static DateTimeFormatter standardDate =
			DateTimeFormatter.ofPattern("yyyy/MM/dd");
	private final static DateTimeFormatter standardTime =
			DateTimeFormatter.ofPattern("HH:mm:ss");

	/**
	 * Format a given dateTime in standard format.
	 *
	 * @param dateTime
	 *            The dateTime to format.
	 * @return yyyy/MM/dd HH:mm:ss
	 * @throws DateTimeParseException
	 *             Thrown on parsing error.
	 */
	public static String formatStandardDateTime(LocalDateTime dateTime) {
		return standardDateTime.format(dateTime);
	}

	/**
	 * Format a given date in standard format.
	 *
	 * @param date
	 *            The date to format.
	 * @return yyyy/MM/dd HH:mm:ss
	 * @throws DateTimeParseException
	 *             Thrown on parsing error.
	 */
	public static String formatStandardDate(LocalDate date) {
		return standardDate.format(date);
	}

	/**
	 * Format a given time in standard format.
	 *
	 * @param time
	 *            The time to format.
	 * @return yyyy/MM/dd HH:mm:ss
	 * @throws DateTimeParseException
	 *             Thrown on parsing error.
	 */
	public static String formatStandardTime(LocalTime time) {
		return standardTime.format(time);
	}

	/**
	 * Parse a datetime in standard format.
	 *
	 * @param datetime
	 *            A datetime in the format "yyyy/MM/dd HH:mm:ss".
	 * @return The parsed date.
	 * @throws DateTimeParseException
	 *             Thrown on parsing error.
	 */
	public static LocalDateTime parseStandardDateTime(String datetime) {
		return LocalDateTime.parse(datetime, standardDateTime);
	}

	/**
	 * Parse a date in standard format.
	 *
	 * @param date
	 *            A date in the format "yyyy/MM/dd".
	 * @return The parsed date.
	 * @throws DateTimeParseException
	 *             Thrown on parsing error.
	 */
	public static LocalDate parseStandardDate(String date) {
		return LocalDate.parse(date, standardDate);
	}

	/**
	 * Parse a time in standard format.
	 *
	 * @param time
	 *            A time in the format "HH:mm:ss".
	 * @return The parsed time.
	 * @throws DateTimeParseException
	 *             Thrown on parsing error.
	 */
	public static LocalTime parseStandardTime(String time) {
		return LocalTime.parse(time, standardTime);
	}

	/**
	 * Date time hours minutes seconds as compact as possible
	 *
	 * @param date
	 *            date to format
	 * @return Example: yyyyMMddHHmmss "20133004121621"
	 */
	public static String formatDateTimeCompact(Date date) {
		return compactDateTime.format(date);
	}

	/**
	 * @param date
	 *            date to parse, example: yyyyMMddHHmmss "20133004121621"
	 * @return parsed date
	 */
	public static Date parseDateTimeCompact(String date) {
		try {
			return compactDateTime.parse(date);
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
	public static String formatDateTimeAirline(Date date) {
		return airlineDateTime.format(date).toUpperCase();
	}

	/**
	 * Parse an airline type date time
	 *
	 * @param date
	 *            date to parse, example: ddMMMyy HH:mm:ss "30APR1957 14:21:16"
	 * @return parsed date
	 */
	public static Date parseDateTimeAirline(String date) {
		try {
			return airlineDateTime.parse(date);
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

	public static String formatDateTimeSql(Date value) {
		return sqlDateTime.format(value);
	}

	public static String formatDateSql(Date value) {
		return sqlDate.format(value);
	}

	/**
	 * Format a given date in standard format.
	 *
	 * @param date
	 *            The date to format.
	 * @return A string formatted in the the pattern: "yyyy/MM/dd"
	 */
	public static String format(LocalDate date) {
		return standardDate.format(date);
	}

	/**
	 * Format a given time in standard format.
	 *
	 * @param time
	 *            The date to format.
	 * @return A string formatted in the the pattern: "HH:mm:ss"
	 */
	public static String format(LocalTime time) {
		return standardTime.format(time);
	}

	/**
	 * Format a given date/time in standard format.
	 *
	 * @param dateTime
	 *            The date/time to format.
	 * @return A string formatted in the the pattern: "yyyy/MM/dd HH:mm:ss"
	 */
	public static String format(LocalDateTime dateTime) {
		return standardDateTime.format(dateTime);
	}

}
