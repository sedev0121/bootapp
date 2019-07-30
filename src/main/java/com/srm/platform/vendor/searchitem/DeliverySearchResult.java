
package com.srm.platform.vendor.searchitem;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeliverySearchResult implements Serializable {

	private static final long serialVersionUID = -7476789702299949678L;
	
	private Long id;

	String code;

	@JsonProperty("company_name")
	String companyName;
	
	@JsonProperty("vendor_name")
	String vendorName;
	
	@JsonProperty("store_name")
	String storeName;

	@JsonProperty("store_address")
	String storeAddress;
	
	@JsonProperty("estimated_arrival_date")
	Date estimatedArriavlDate;

	@JsonProperty("create_date")
	Date createDate;
	
	String contact;
	Integer state;

	public DeliverySearchResult(Long id, String code, String companyName, String vendorName, String storeName, String storeAddress,
			String contact, Date estimatedArriavlDate, Date createDate, Integer state) {
		this.id = id;
		this.code = code;
		this.companyName = companyName;
		this.vendorName = vendorName;
		this.storeName = storeName;
		this.storeAddress = storeAddress;
		this.estimatedArriavlDate = estimatedArriavlDate;
		this.createDate = createDate;
		this.state = state;
		this.contact = contact;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStoreAddress() {
		return storeAddress;
	}

	public void setStoreAddress(String storeAddress) {
		this.storeAddress = storeAddress;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public Date getEstimatedArriavlDate() {
		return estimatedArriavlDate;
	}

	public void setEstimatedArriavlDate(Date estimatedArriavlDate) {
		this.estimatedArriavlDate = estimatedArriavlDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
	
}
