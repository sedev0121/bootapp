package com.srm.platform.vendor.saveform;

import java.util.List;
import java.util.Map;

public class PurchaseOrderSaveForm {

	private String id;
	private String code;
	private String content;
	private Integer state;
	
	private Long store;
	private String contract_code;
	private Double base_price;
	private Integer price_from;
	
	private List<Map<String, String>> table;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public Integer getPrice_from() {
		return price_from;
	}

	public void setPrice_from(Integer price_from) {
		this.price_from = price_from;
	}

	public String getContract_code() {
		return contract_code;
	}

	public void setContract_code(String contract_code) {
		this.contract_code = contract_code;
	}

	public Double getBase_price() {
		return base_price;
	}

	public void setBase_price(Double base_price) {
		this.base_price = base_price;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
