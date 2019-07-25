package com.srm.platform.vendor.searchitem;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskLogSearchResult implements Serializable {

	private static final long serialVersionUID = 3692876780746931969L;

	private Long id;
	
	@JsonProperty("vendor_code")
	private String vendorCode;
	
	@JsonProperty("vendor_name")
	private String vendorName;
	

	@JsonProperty("company_name")
	private String companyName;
	
	private Integer state;
	private Integer type;
	
	@JsonProperty("failed_reason")
	private String failedReason;

	@JsonProperty("create_date")
	private Date createDate;
	
	public TaskLogSearchResult(Long id, String vendorCode, String vendorName, String companyName, Integer type, Integer state, String failedReason, Date createDate) {
		this.id = id;
		this.vendorCode = vendorCode;
		this.companyName = companyName;
		this.vendorName = vendorName;
		this.type = type;
		this.state = state;
		this.failedReason = failedReason;	
		this.createDate = createDate;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}


	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}


	public String getVendorCode() {
		return vendorCode;
	}


	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}


	public String getVendorName() {
		return vendorName;
	}


	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}


	public Integer getState() {
		return state;
	}


	public void setState(Integer state) {
		this.state = state;
	}


	public String getFailedReason() {
		return failedReason;
	}


	public void setFailedReason(String failedReason) {
		this.failedReason = failedReason;
	}


	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


}
