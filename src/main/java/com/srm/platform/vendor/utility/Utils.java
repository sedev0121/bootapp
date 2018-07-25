package com.srm.platform.vendor.utility;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.thymeleaf.util.StringUtils;

import com.srm.platform.vendor.repository.PermissionGroupFunctionUnitRepository;

public class Utils {

	private static SimpleDateFormat getDateFormat() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat;
	}

	private static SimpleDateFormat getDateTimeFormat() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return dateFormat;
	}

	public static Date parseDate(String dateStr) {
		Date date = null;

		try {
			if (dateStr != null && !dateStr.isEmpty())
				date = getDateFormat().parse(dateStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public static Date parseDateTime(String dateStr) {
		Date date = null;

		try {
			if (dateStr != null && !dateStr.isEmpty())
				date = getDateTimeFormat().parse(dateStr);
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

	public static String generateId() {
		String id = "";

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		id = dateFormat.format(new Date());

		int rand = (int) (Math.random() * 1000);
		id += String.format("%03d", rand);

		return id;
	}

	public static List<String> getAllUnitsOfId(Long unitId,
			PermissionGroupFunctionUnitRepository permissionGroupFunctionUnitRepository) {
		String unitList = String.valueOf(unitId);
		unitList = StringUtils.append(unitList, "," + searchChildren(unitList, permissionGroupFunctionUnitRepository));

		return Arrays.asList(StringUtils.split(unitList, ","));
	}

	private static String searchChildren(String parentIdList,
			PermissionGroupFunctionUnitRepository permissionGroupFunctionUnitRepository) {
		String childList = "";
		List<PermissionUnit> unitList = permissionGroupFunctionUnitRepository
				.findChildrenByParentId(StringUtils.split(parentIdList, ","));
		for (PermissionUnit unit : unitList) {
			if (unit != null)
				childList = StringUtils.append(childList, "," + unit.getUnits());
		}

		if (childList.isEmpty()) {
			return childList;
		} else {
			childList = StringUtils.append(childList,
					"," + searchChildren(childList, permissionGroupFunctionUnitRepository));
			return childList;
		}
	}
}
