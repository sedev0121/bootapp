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
	
	private Integer state;
	
	@JsonProperty("failed_reason")
	private String failedReason;

	@JsonProperty("create_date")
	private Date createDate;
	
	public TaskLogSearchResult(Long id, String vendorCode, String vendorName, Integer state, String failedReason, Date createDate) {
		this.id = id;
		this.vendorCode = vendorCode;
		this.vendorName = vendorName;
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
