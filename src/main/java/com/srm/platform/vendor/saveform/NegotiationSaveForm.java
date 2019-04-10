package com.srm.platform.vendor.saveform;

import java.util.List;
import java.util.Map;

public class NegotiationSaveForm {

	private String code;
	private String order_code;
	private Double tax_rate;
	private Integer state;
	

	private List<Map<String, String>> table;


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getOrder_code() {
		return order_code;
	}


	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}


	public Double getTax_rate() {
		return tax_rate;
	}


	public void setTax_rate(Double tax_rate) {
		this.tax_rate = tax_rate;
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
