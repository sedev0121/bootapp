package com.srm.platform.vendor.u8api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestApiResponse {

	private String status;
	private String errmsg;
	private List<LinkedHashMap<String, String>> data;
	private Map<String, Object> originalMap;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public List<LinkedHashMap<String, String>> getData() {
		return data;
	}

	public void setData(List<LinkedHashMap<String, String>> data) {
		this.data = data;
	}

	public Map<String, Object> getOriginalMap() {
		return originalMap;
	}

	public void setOriginalMap(Map<String, Object> originalMap) {
		this.originalMap = originalMap;
	}

	public String getValue(String key) {
		return String.valueOf(this.originalMap.get(key));
	}
	
	public boolean isSuccess() {
		return "ok".equals(status);
	}

}
