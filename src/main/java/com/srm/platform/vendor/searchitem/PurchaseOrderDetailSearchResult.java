package com.srm.platform.vendor.searchitem;

import java.util.Date;

public class PurchaseOrderDetailSearchResult {

	Long id;

	Integer rowno;

	String code;

	Float quantity;

	Float shipped_quantity;

	String inventoryname;

	String inventorycode;

	String vendorname;

	String vendorcode;

	String specs;

	String unitname;

	Date arrivedate;

	Date confirmdate;

	String arrivenote;

	String confirmnote;

	Float remain_quantity;

	Date lastshipdate;

	public PurchaseOrderDetailSearchResult(Long id, Integer rowno, String code, Float quantity, Float shipped_quantity,
			String inventoryname, String inventorycode, String vendorname, String vendorcode, String specs,
			String unitname, Date arrivedate, Date lastshipdate, Date confirmdate, String arrivenote,
			String confirmnote, Float remain_quantity) {

		this.id = id;
		this.rowno = rowno;
		this.code = code;
		this.quantity = quantity;
		this.shipped_quantity = shipped_quantity;
		this.inventorycode = inventorycode;
		this.inventoryname = inventoryname;
		this.vendorcode = vendorcode;
		this.vendorname = vendorname;
		this.specs = specs;
		this.unitname = unitname;
		this.arrivedate = arrivedate;
		this.lastshipdate = lastshipdate;
		this.confirmdate = confirmdate;
		this.arrivenote = arrivenote;
		this.confirmnote = confirmnote;
		this.remain_quantity = remain_quantity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getLastshipdate() {
		return lastshipdate;
	}

	public void setLastshipdate(Date lastshipdate) {
		this.lastshipdate = lastshipdate;
	}

	public Integer getRowno() {
		return rowno;
	}

	public void setRowno(Integer rowno) {
		this.rowno = rowno;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Float getQuantity() {
		return quantity;
	}

	public void setQuantity(Float quantity) {
		this.quantity = quantity;
	}

	public Float getShipped_quantity() {
		return shipped_quantity;
	}

	public void setShipped_quantity(Float shipped_quantity) {
		this.shipped_quantity = shipped_quantity;
	}

	public String getInventoryname() {
		return inventoryname;
	}

	public void setInventoryname(String inventoryname) {
		this.inventoryname = inventoryname;
	}

	public String getInventorycode() {
		return inventorycode;
	}

	public void setInventorycode(String inventorycode) {
		this.inventorycode = inventorycode;
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

	public String getSpecs() {
		return specs;
	}

	public void setSpecs(String specs) {
		this.specs = specs;
	}

	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

	public Date getArrivedate() {
		return arrivedate;
	}

	public void setArrivedate(Date arrivedate) {
		this.arrivedate = arrivedate;
	}

	public Date getConfirmdate() {
		return confirmdate;
	}

	public void setConfirmdate(Date confirmdate) {
		this.confirmdate = confirmdate;
	}

	public String getArrivenote() {
		return arrivenote;
	}

	public void setArrivenote(String arrivenote) {
		this.arrivenote = arrivenote;
	}

	public String getConfirmnote() {
		return confirmnote;
	}

	public void setConfirmnote(String confirmnote) {
		this.confirmnote = confirmnote;
	}

	public Float getRemain_quantity() {
		return remain_quantity;
	}

	public void setRemain_quantity(Float remain_quantity) {
		this.remain_quantity = remain_quantity;
	}

}
