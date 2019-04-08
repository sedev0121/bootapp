package com.srm.platform.vendor.saveform;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.srm.platform.vendor.utility.Utils;

public class DeliverySaveForm {

	private Long id;
	private String code;
	private String vendor;
	private Integer state;
	private Long company;
	private Long store;
	private String estimated_arrival_date;

	private List<Map<String, String>> table;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getEstimated_arrival_date() {
		return estimated_arrival_date;
	}

	public void setEstimated_arrival_date(String estimated_arrival_date) {
		this.estimated_arrival_date = estimated_arrival_date;
	}

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}

	public Long getStore() {
		return store;
	}

	public void setStore(Long store) {
		this.store = store;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
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
