package com.inexas.util;

import java.text.*;
import java.util.Date;
import com.inexas.exception.InexasRuntimeException;

public class DateU {
	private static final DateFormat compactDateTime = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final DateFormat airlineDateTime = new SimpleDateFormat("ddMMMyy HH:mm:ss");
	private final static DateFormat standardDateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private final static DateFormat standardDate = new SimpleDateFormat("yyyy/MM/dd");
	private final static DateFormat standardTime = new SimpleDateFormat("HH:mm:ss");
	private final static DateFormat sqlDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static DateFormat sqlDate = new SimpleDateFormat("yyyy-MM-dd");
	private final static DateFormat sqlTime = standardTime;
	private final static String MESSAGE =
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
			throw new InexasRuntimeException("Error parsing date: " + date, e);
		}
	}

	/**
	 * @return yyyy/MM/dd HH:mm:ss
	 */
	public static String formatDateTimeStandard(Date date) {
		return standardDateTime.format(date);
	}

	/**
	 * @param date
	 *            yyyy/MM/dd HH:mm:ss
	 */
	public static Date parseDateTimeStandard(String date) {
		try {
			return standardDateTime.parse(date);
		} catch(final ParseException e) {
			throw new InexasRuntimeException("Error parsing date: " + date, e);
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
			throw new InexasRuntimeException("Error parsing date: " + date, e);
		}
	}

	/**
	 * 'Intelligent' date/time parser. does best guess as the input format
	 *
	 * @param string
	 * @return
	 */
	public static Date toDate(String string) {
		int whichFormatter = 0;
		if(string.indexOf('/') > 0) {
			whichFormatter |= 0x01;
		}
		if(string.indexOf(':') > 0) {
			whichFormatter |= 0x02;
		}
		final DateFormat formatter;
		switch(whichFormatter) {
		case 1:
			formatter = standardDate;
			break;
		case 2:
			formatter = standardTime;
			break;
		case 3:
			formatter = standardDateTime;
			break;
		default:
			throw new RuntimeException(MESSAGE + string);
		}
		try {
			final Date result = formatter.parse(string);
			return result;
		} catch(final ParseException e) {
			throw new RuntimeException(MESSAGE + string, e);
		}
	}

	public static String toDateString(Date date) {
		return standardDate.format(date);
	}

	public static String toTimeString(Date date) {
		return standardTime.format(date);
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

	public static String formatTimeSql(Date value) {
		return sqlTime.format(value);
	}

}
