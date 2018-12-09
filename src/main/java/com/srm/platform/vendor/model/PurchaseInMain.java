package com.srm.platform.vendor.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;


@Entity

@Table(name = "purchase_in_main")
public class PurchaseInMain {
	@Id
	private String code;

	private String type;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "vendor_code", referencedColumnName = "code")
	Vendor vendor;

	private Integer bredvouch;
	private String warehouse_code;
	private String warehouse_name;
	private String memo;

	private Date date;
	private Date verifyDate;

	public Date getVerifyDate() {
		return verifyDate;
	}

	public void setVerifyDate(Date verifyDate) {
		this.verifyDate = verifyDate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public Integer getBredvouch() {
		return bredvouch;
	}

	public void setBredvouch(Integer bredvouch) {
		this.bredvouch = bredvouch;
	}

	public String getWarehouse_code() {
		return warehouse_code;
	}

	public void setWarehouse_code(String warehouse_code) {
		this.warehouse_code = warehouse_code;
	}

	public String getWarehouse_name() {
		return warehouse_name;
	}

	public void setWarehouse_name(String warehouse_name) {
		this.warehouse_name = warehouse_name;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
