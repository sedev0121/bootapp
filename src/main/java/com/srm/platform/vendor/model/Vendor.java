package com.srm.platform.vendor.model;

import java.time.Instant;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "vendor")
public class Vendor {
	@Id
	private String code;

	private String name;
	private String abbrname;

	@Column(name = "sort_code")
	private String sortCode;
	private String industry;
	private String address;
	private String phone;

	@Column(name = "bank_open")
	private String bankOpen;

	@Column(name = "bank_acc_number")
	private String bankAccNumber;
	private String fax;
	private String email;

	public Vendor() {

	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbbrname() {
		return abbrname;
	}

	public void setAbbrname(String abbrname) {
		this.abbrname = abbrname;
	}

	@JsonProperty("sort_code")
	public String getSortCode() {
		return sortCode;
	}

	public void setSortCode(String sortCode) {
		this.sortCode = sortCode;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@JsonProperty("bank_open")
	public String getBankOpen() {
		return bankOpen;
	}

	public void setBankOpen(String bankOpen) {
		this.bankOpen = bankOpen;
	}

	@JsonProperty("bank_acc_number")
	public String getBankAccNumber() {
		return bankAccNumber;
	}

	public void setBankAccNumber(String bankAccNumber) {
		this.bankAccNumber = bankAccNumber;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getReceiveSite() {
		return receiveSite;
	}

	@JsonProperty("receive_site")
	public void setReceiveSite(String receiveSite) {
		this.receiveSite = receiveSite;
	}

	@JsonProperty("end_date")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	private String contact;
	private String mobile;

	@Column(name = "receive_site")
	private String receiveSite;

	@Column(name = "end_date")
	private Date endDate;
	private Instant timestamp;
	private String memo;

}
