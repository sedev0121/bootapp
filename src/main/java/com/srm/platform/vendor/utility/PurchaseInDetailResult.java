package com.srm.platform.vendor.utility;

import java.io.Serializable;

public class PurchaseInDetailResult implements Serializable {

	private static final long serialVersionUID = 2283867296635029703L;

	String id;

	String code;

	String date;

	Integer rowno;

	String inventoryname;

	String inventory_code;

	String specs;

	String unitname;

	String quantity;

	String price;

	String cost;

	String closed_quantity;

	String remain_quantity;

	String tax_price;

	String tax_rate;

	String tax_cost;

	String memo;

	public PurchaseInDetailResult(String id, String code, String date, Integer rowno, String inventoryname,
			String inventorycode, String specs, String unitname, String quantity, String price, String cost,
			String closed_quantity, String remain_quantity, String tax_price, String tax_rate, String tax_cost,
			String memo) {

		this.id = id;
		this.code = code;
		this.date = date;
		this.rowno = rowno;
		this.inventoryname = inventoryname;
		this.inventory_code = inventorycode;
		this.specs = specs;
		this.unitname = unitname;
		this.quantity = quantity;
		this.price = price;
		this.cost = cost;

		this.closed_quantity = closed_quantity;
		this.remain_quantity = remain_quantity;

		this.tax_price = tax_price;
		this.tax_cost = tax_cost;
		this.tax_rate = tax_rate;
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
		return inventory_code;
	}

	public void setInventorycode(String inventorycode) {
		this.inventory_code = inventorycode;
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

	public String getClosed_quantity() {
		return closed_quantity;
	}

	public void setClosed_quantity(String closed_quantity) {
		this.closed_quantity = closed_quantity;
	}

	public String getRemain_quantity() {
		return remain_quantity;
	}

	public void setRemain_quantity(String remain_quantity) {
		this.remain_quantity = remain_quantity;
	}

	public String getTax_price() {
		return tax_price;
	}

	public void setTax_price(String tax_price) {
		this.tax_price = tax_price;
	}

	public String getTax_rate() {
		return tax_rate;
	}

	public void setTax_rate(String tax_rate) {
		this.tax_rate = tax_rate;
	}

	public String getTax_cost() {
		return tax_cost;
	}

	public void setTax_cost(String tax_cost) {
		this.tax_cost = tax_cost;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}
