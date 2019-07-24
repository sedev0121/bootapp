package com.srm.platform.vendor.utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.srm.platform.vendor.model.DeliveryDetail;
import com.srm.platform.vendor.model.DeliveryMain;
import com.srm.platform.vendor.model.PurchaseOrderDetail;
import com.srm.platform.vendor.u8api.RestApiClient;
import com.srm.platform.vendor.u8api.RestApiResponse;

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

	public static boolean isEmpty(String str) {
		if (str != null && !str.isEmpty() && !str.equals("null")) {
			return false;
		} else {
			return true;
		}
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
	
	public static Date getStatementDate(String dateStr) {
		Date today = new Date();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
		String todayDate = dateFormat.format(today);
		String statementDateStr;
		if (Integer.parseInt(todayDate) > Integer.parseInt(dateStr)) {
			dateFormat = new SimpleDateFormat("yyyy-MM-");
			String yearMonth = dateFormat.format(today);
			statementDateStr = yearMonth + dateStr;
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(today);
			cal.add(Calendar.MONTH, -1);
			Date prevMonthDate = cal.getTime();			
			dateFormat = new SimpleDateFormat("yyyy-MM-");
			String yearMonth = dateFormat.format(prevMonthDate);
			statementDateStr = yearMonth + dateStr;
		}
		
		return parseDate(statementDateStr);
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

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
		id = dateFormat.format(new Date());

		int rand = (int) (Math.random() * 100);
		id += String.format("%02d", rand);

		return id;
	}
	
	public static List<String> generateStaticBoxCode(String classCode, String serialNumberStr, Integer count) {
		Long serialNumber = Long.parseLong(serialNumberStr);
		List<String> codeList = new ArrayList<String>();
		String temp;
		for (int i= 0; i< count; i++) {		
			if (classCode.startsWith("T")) {
				temp = String.format("%s%05d", classCode, serialNumber + i + 1);
			} else {
				temp = String.format("%s%06d", classCode, serialNumber + i + 1);	
			}
			
			codeList.add(temp);
		}
		return codeList;
	}
	
	public static String generateDeliveryNumber(String vendorCode) {
		String id = "";

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		id = dateFormat.format(new Date());

		id += vendorCode;
		return id;
	}
	
	public static String generateTaskCode() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String code = dateFormat.format(new Date());
		return code;
	}

	public static String generateResetPassword() {
		int max = 1000000;
		int min = 100000;
		Random rand = new Random();

		int n = rand.nextInt(max - min + 1) + min;

		return String.valueOf(n);

	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	public static double priceRound(double value) {
		return Double.parseDouble(String.format("%.6f", value));
	}

	public static double costRound(double value) {
		double result = Double.parseDouble(String.format("%.2f", value));
		return result;
	}
	
	public static RestApiResponse postForArrivalVouch(DeliveryMain deliveryMain,List<DeliveryDetail> details, RestApiClient apiClient) {
		
		Map<String, Object> postData = new HashMap<>();
		postData.put("ccode", deliveryMain.getCode());
		postData.put("ddate", Utils.formatDateTime(new Date()));
		postData.put("cvencode", deliveryMain.getVendor().getCode());
		postData.put("itaxrate", "0.0");
		postData.put("cmemo", "");
		postData.put("cpocode", "");
		postData.put("cbustype", deliveryMain.getType());
		
		
		List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
		
		for(DeliveryDetail detail : details) {
			Map<String, Object> detailData = new HashMap<>();
			PurchaseOrderDetail orderDetail = detail.getPurchaseOrderDetail();
			detailData.put("cwhcode", detail.getMain().getStore().getCode());
			detailData.put("cinvcode", orderDetail.getInventory().getCode());
			detailData.put("qty", detail.getDeliveredQuantity());
			detailData.put("inum", detail.getDeliveredPackageQuantity());
			detailData.put("itaxrate", orderDetail.getTaxRate());
			detailData.put("iposid", orderDetail.getOriginalId());
			detailData.put("cpocode", orderDetail.getMain().getCode());
			detailData.put("ivouchrowno", detail.getRowNo());
			detailData.put("fprice", orderDetail.getPrice());
			detailData.put("famount", round(orderDetail.getPrice() * detail.getDeliveredQuantity(), 2));
			detailData.put("ftaxprice", orderDetail.getTaxPrice());
			detailData.put("ftaxamount", round(orderDetail.getTaxPrice() * detail.getDeliveredQuantity(), 2));
			
			detailList.add(detailData);
		}	
		
		
		postData.put("detail", detailList);
		
		RestApiResponse response = apiClient.postForArrivalVouch(postData);
		
		return response;
	}
}
