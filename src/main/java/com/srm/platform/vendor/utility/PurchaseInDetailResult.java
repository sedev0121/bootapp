package com.srm.platform.vendor.utility;

import java.io.Serializable;

public class PurchaseInDetailResult implements Serializable {

	private static final long serialVersionUID = 2283867296635029703L;

	String id;

	String code;

	String date;

	Integer rowno;

	String inventoryname;

	String inventorycode;

	String specs;

	String unitname;

	String quantity;

	String price;

	String cost;

	String cmassunitname;

	String assitantunitname;

	String irate;

	String number;

	String state;

	String closed_quantity;

	public PurchaseInDetailResult(String id, String code, String date, Integer rowno, String inventoryname,
			String inventorycode, String specs, String unitname, String quantity, String price, String cost,
			String cmassunitname, String assitantunitname, String irate, String number, String state,
			String closed_quantity) {

		this.id = id;
		this.code = code;
		this.date = date;
		this.rowno = rowno;
		this.inventoryname = inventoryname;
		this.inventorycode = inventorycode;
		this.specs = specs;
		this.unitname = unitname;
		this.quantity = quantity;
		this.price = price;
		this.cost = cost;
		this.cmassunitname = cmassunitname;
		this.assitantunitname = assitantunitname;
		this.irate = irate;
		this.number = number;
		this.state = state;
		this.closed_quantity = closed_quantity;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Integer getRowno() {
		return rowno;
	}

	public void setRowno(Integer rowno) {
		this.rowno = rowno;
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

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getCmassunitname() {
		return cmassunitname;
	}

	public void setCmassunitname(String cmassunitname) {
		this.cmassunitname = cmassunitname;
	}

	public String getAssitantunitname() {
		return assitantunitname;
	}

	public void setAssitantunitname(String assitantunitname) {
		this.assitantunitname = assitantunitname;
	}

	public String getIrate() {
		return irate;
	}

	public void setIrate(String irate) {
		this.irate = irate;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getClosed_quantity() {
		return closed_quantity;
	}

	public void setClosed_quantity(String closed_quantity) {
		this.closed_quantity = closed_quantity;
	}

}
