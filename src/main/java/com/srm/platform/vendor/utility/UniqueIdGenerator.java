package com.srm.platform.vendor.utility;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UniqueIdGenerator {
	public static String generateId() {
		String id = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		id = dateFormat.format(new Date());

		int rand = (int) (Math.random() * 1000);
		id += String.format("%3d", rand);

		return id;
	}
}
