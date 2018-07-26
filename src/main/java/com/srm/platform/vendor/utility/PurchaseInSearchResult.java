package com.srm.platform.vendor.utility;

import java.io.Serializable;
import java.util.Date;

public class PurchaseInSearchResult implements Serializable {

	private static final long serialVersionUID = -3508008704427595672L;

	String code;

	String warehouse_name;

	Date date;

	String type;

	String memo;

	Integer bredvouch;

	String vendorname;

	String vendor_code;

	public PurchaseInSearchResult(String code, String warehouse_name, Date date, String type, String memo,
			Integer bredvouch, String vendorname, String vendorcode) {

		this.code = code;
		this.warehouse_name = warehouse_name;
		this.date = date;
		this.type = type;
		this.memo = memo;
		this.bredvouch = bredvouch;
		this.vendorname = vendorname;
		this.vendor_code = vendorcode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getWarehousename() {
		return warehouse_name;
	}

	public void setWarehousename(String warehouse_name) {
		this.warehouse_name = warehouse_name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getBredvouch() {
		return bredvouch;
	}

	public void setBredvouch(Integer bredvouch) {
		this.bredvouch = bredvouch;
	}

	public String getVendorname() {
		return vendorname;
	}

	public void setVendorname(String vendorname) {
		this.vendorname = vendorname;
	}

	public String getVendorcode() {
		return vendor_code;
	}

	public void setVendorcode(String vendorcode) {
		this.vendor_code = vendorcode;
	}

}
