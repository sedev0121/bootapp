package com.srm.platform.vendor.searchitem;

import java.util.Date;

public class PurchaseOrderDetailSearchResult {

	Long id;

	Integer rowno;

	String code;

	Double quantity;

	Double shipped_quantity;

	String inventory_name;

	String inventory_code;

	String vendorname;

	String vendorcode;

	String specs;

	String unit_name;

	Date arrivedate;

	Date confirmdate;

	String arrivenote;

	String confirmnote;

	Double remain_quantity;

	Date lastshipdate;


	public PurchaseOrderDetailSearchResult(Long id, Integer rowno, String code, Double quantity, Double shipped_quantity,
			String inventory_name, String inventory_code, String vendorname, String vendorcode, String specs,
			String unit_name, Date arrivedate, Date lastshipdate, Date confirmdate, String arrivenote,
			String confirmnote, Double remain_quantity) {

		this.id = id;
		this.rowno = rowno;
		this.code = code;
		this.quantity = quantity;
		this.shipped_quantity = shipped_quantity;
		this.inventory_code = inventory_code;
		this.inventory_name = inventory_name;
		this.vendorcode = vendorcode;
		this.vendorname = vendorname;
		this.specs = specs;
		this.unit_name = unit_name;
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

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getShipped_quantity() {
		return shipped_quantity;
	}

	public void setShipped_quantity(Double shipped_quantity) {
		this.shipped_quantity = shipped_quantity;
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

	public String getInventory_name() {
		return inventory_name;
	}

	public void setInventory_name(String inventory_name) {
		this.inventory_name = inventory_name;
	}

	public String getInventory_code() {
		return inventory_code;
	}

	public void setInventory_code(String inventory_code) {
		this.inventory_code = inventory_code;
	}

	public String getUnit_name() {
		return unit_name;
	}

	public void setUnit_name(String unit_name) {
		this.unit_name = unit_name;
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

	public Double getRemain_quantity() {
		return remain_quantity;
	}

	public void setRemain_quantity(Double remain_quantity) {
		this.remain_quantity = remain_quantity;
	}

}
