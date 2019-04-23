package com.srm.platform.vendor.saveform;

import java.util.List;
import java.util.Map;

public class PurchaseOrderSaveForm {

	private String code;

	private Integer state;
	
	private Long store;

	private List<Map<String, String>> table;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	
	public Long getStore() {
		return store;
	}

	public void setStore(Long store) {
		this.store = store;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public List<Map<String, String>> getTable() {
		return table;
	}

	public void setTable(List<Map<String, String>> table) {
		this.table = table;
	}

}
