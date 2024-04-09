package com.okstatelibrary.redbud.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateUtil {

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private static SimpleDateFormat longDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public static String get9MonthsAfterTodayDate() {

		return LocalDate.now().plusMonths(9).toString();
	}

	public static long getCurretTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	public static String get2YearAfterTodayDate() {

		return LocalDate.now().plusYears(2).toString();
	}

	public static Date getYesterdayDate() {

		return new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);

	}

	public static String getYesterdayDate(boolean isMorning) {

		String date = LocalDate.now().plusDays(-1).toString();
		if (isMorning) {
			return date + "T00:00:00.000Z";
		}
		return date + "T23:59:59.000Z";
	}

	public static String getTodayDateAndTime() {
		// LocalDateTime now = ;
		return LocalDateTime.now().toString();

	}

	public static Date getTodayDate() {
		long millis = System.currentTimeMillis();

		return new java.sql.Date(millis);
	}

	public static int getCurrentHour() {

		Calendar rightNow = Calendar.getInstance();
		return rightNow.get(Calendar.HOUR_OF_DAY);

	}

	public static DateFormat getDateFormat() {
		return dateFormat;
	}

	public static LocalDate getLocaleDate(String date) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		formatter = formatter.withLocale(Locale.US);

		return LocalDate.parse(date, formatter);
	}

	public static String getCurrentDateAndTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()).toString();
	}

	public static java.util.Date getPreviousDate(java.util.Date date) {

		try {
			String dateString = date.toString();

			java.util.Date myDate = (java.util.Date) dateFormat.parse(dateString);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(myDate);
			calendar.add(Calendar.DAY_OF_YEAR, -1);

			return (java.util.Date) calendar.getTime();
		} catch (Error er) {
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
			return date;
		}

	}

	public static int compareDates(Date date1) {
		return dateFormat.format(date1).compareTo(dateFormat.format(getTodayDate()));
	}

	public static int compareDates(Date date1, Date date2) {
		return dateFormat.format(date1).compareTo(dateFormat.format(date2));
	}

	public static int compareDates(String date1, String date2) throws ParseException {

		Date start = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH).parse(date1);
		Date end = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH).parse(date2);

		return start.compareTo(end);
	}

	public static Date getLongDate(String date) {
		try {
			return longDateFormat.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Date getShortDate2(String date) {
		try {
			return dateFormat.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String getShortDate(Date date) {
		// SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
		return dateFormat.format(date);
	}

	public static String getShortDate(String dateString) throws ParseException {
		// SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
		return dateFormat.format(dateFormat.parse(dateString));
	}

}
