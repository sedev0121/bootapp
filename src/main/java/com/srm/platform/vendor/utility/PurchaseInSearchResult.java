package com.srm.platform.vendor.utility;

import java.io.Serializable;
import java.util.Date;

public class PurchaseInSearchResult implements Serializable {

	private static final long serialVersionUID = -3508008704427595672L;

	String code;

	Integer state;

	String warehousename;

	String maker;

	Date date;

	String receivename;

	String departmentname;

	String purchasetypename;

	Date auditdate;

	String memory;

	String handler;

	Integer bredvouch;

	String vendorname;

	String vendorcode;

	public PurchaseInSearchResult(String code, Integer state, String warehousename, String maker, Date date,
			String receivename, String departmentname, String purchasetypename, Date auditdate, String memory,
			String handler, Integer bredvouch, String vendorname, String vendorcode) {

		this.code = code;
		this.state = state;
		this.warehousename = warehousename;
		this.maker = maker;
		this.date = date;
		this.receivename = receivename;
		this.departmentname = departmentname;
		this.purchasetypename = purchasetypename;
		this.auditdate = auditdate;
		this.memory = memory;
		this.handler = handler;
		this.bredvouch = bredvouch;
		this.vendorname = vendorname;
		this.vendorcode = vendorcode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getWarehousename() {
		return warehousename;
	}

	public void setWarehousename(String warehousename) {
		this.warehousename = warehousename;
	}

	public String getMaker() {
		return maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getReceivename() {
		return receivename;
	}

	public void setReceivename(String receivename) {
		this.receivename = receivename;
	}

	public String getDepartmentname() {
		return departmentname;
	}

	public void setDepartmentname(String departmentname) {
		this.departmentname = departmentname;
	}

	public String getPurchasetypename() {
		return purchasetypename;
	}

	public void setPurchasetypename(String purchasetypename) {
		this.purchasetypename = purchasetypename;
	}

	public Date getAuditdate() {
		return auditdate;
	}

	public void setAuditdate(Date auditdate) {
		this.auditdate = auditdate;
	}

	public String getMemory() {
		return memory;
	}

	public void setMemory(String memory) {
		this.memory = memory;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
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
		return vendorcode;
	}

	public void setVendorcode(String vendorcode) {
		this.vendorcode = vendorcode;
	}

}
