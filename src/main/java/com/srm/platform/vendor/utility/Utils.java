package com.srm.platform.vendor.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Utils {

	private static SimpleDateFormat getDateFormat() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat;
	}

	private static SimpleDateFormat getDateTimeFormat() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat;
	}

	private static SimpleDateFormat getDateTimeFormat2() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return dateFormat;
	}

	public static Date parseDate(String dateStr) {
		Date date = null;

		try {
			if (dateStr != null && !dateStr.isEmpty() && !dateStr.equals("null")) {
				date = getDateFormat().parse(dateStr);
			}				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public static Date parseDateTime(String dateStr) {
		Date date = null;

		try {
			if (dateStr != null && !dateStr.isEmpty() && !dateStr.equals("null")) {
				date = getDateTimeFormat().parse(dateStr);
			}				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return date;
	}

	public static Date parseDateTime2(String dateStr) {
		Date date = null;

		try {
			if (dateStr != null && !dateStr.isEmpty() && !dateStr.equals("null")) {
				date = getDateTimeFormat2().parse(dateStr);
			}				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return date;
	}

	public static Date getNextDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}
	
	public static Date getNegotiationEndDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, 3);
		return cal.getTime();
	}

	public static Date getStartSyncDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, -2);
		return cal.getTime();
	}
	
	public static Date getAlertDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, 3);
		return cal.getTime();
	}

	public static Date getNextDate(String dateStr) {
		Date date = parseDate(dateStr);
		if (date != null) {
			return getNextDate(date);
		} else {
			return null;
		}
	}

	public static String formatDate(Date date) {
		if (date == null)
			return "";
		return getDateFormat().format(date);
	}

	public static String formatDateTime(Date date) {
		if (date == null)
			return "";

		return getDateTimeFormat().format(date);
	}

	public static String formatDateZeroTime(Date date) {
		if (date == null)
			return "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		return dateFormat.format(date);
	}

	public static String generateId() {
		String id = "";

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		id = dateFormat.format(new Date());

		int rand = (int) (Math.random() * 1000);
		id += String.format("%03d", rand);

		return id;
	}

	public static String generateResetPassword() {
		int max = 1000000;
		int min = 100000;
		Random rand = new Random();

		int n = rand.nextInt(max - min + 1) + min;

		return String.valueOf(n);

	}

	public static float round(float value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (float) tmp / factor;
	}

	public static double priceRound(double value) {
		return Double.parseDouble(String.format("%.6f", value));
	}

	public static double costRound(double value) {
		double result = Double.parseDouble(String.format("%.2f", value));
		return result;
	}
}
