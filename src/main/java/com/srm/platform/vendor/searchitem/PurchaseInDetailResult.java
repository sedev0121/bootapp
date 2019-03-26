package com.srm.platform.vendor.searchitem;

import java.io.Serializable;

public class PurchaseInDetailResult implements Serializable {

	private static final long serialVersionUID = 2283867296635029703L;

	String id;

	String code;

	String date;
	String verify_date;

	Integer rowno;

	String inventoryname;

	String inventory_code;

	String specs;

	String unitname;

	String quantity;

	String price;

	String cost;

	String tax_price;

	String tax_rate;

	String state;

	String tax_cost;

	String memo;

	String nat_tax_price;

	String material_quantity;

	String material_tax_price;

	String vendorcode;
	String vendorname;
	String type;
	String bredvouch;
	String mainmemo;

	String poCode;
	String natPrice;
	String natTaxRate;

	public PurchaseInDetailResult(String id, String code, String date, String verify_date,Integer rowno, String inventoryname,
			String inventorycode, String specs, String unitname, String quantity, String price, String cost,
			String tax_price, String tax_rate, String tax_cost, String memo, String nat_tax_price,
			String material_quantity, String material_tax_price, String vendorname, String vendorcode, String type,
			String bredvouch, String mainmemo, String state, String poCode, String natPrice, String natTaxRate) {

		this.id = id;
		this.code = code;
		this.date = date;
		this.verify_date = verify_date;
		this.rowno = rowno;
		this.inventoryname = inventoryname;
		this.inventory_code = inventorycode;
		this.specs = specs;
		this.unitname = unitname;
		this.quantity = quantity;
		this.price = price;
		this.cost = cost;
		this.memo = memo;

		this.tax_price = tax_price;
		this.tax_cost = tax_cost;
		this.tax_rate = tax_rate;

		this.nat_tax_price = nat_tax_price;
		this.material_quantity = material_quantity;
		this.material_tax_price = material_tax_price;

		this.vendorcode = vendorcode;
		this.vendorname = vendorname;
		this.type = type;
		this.bredvouch = bredvouch;
		this.mainmemo = mainmemo;
		this.state = state;

		this.poCode = poCode;
		this.natPrice = natPrice;
		this.natTaxRate = natTaxRate;
	}

	
	
	public String getVerify_date() {
		return verify_date;
	}


	public void setVerify_date(String verify_date) {
		this.verify_date = verify_date;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPo_code() {
		return poCode;
	}

	public void setPoCode(String poCode) {
		this.poCode = poCode;
	}

	public String getNat_price() {
		return natPrice;
	}

	public void setNatPrice(String natPrice) {
		this.natPrice = natPrice;
	}

	public String getNat_tax_rate() {
		return natTaxRate;
	}

	public void setNatTaxRate(String natTaxRate) {
		this.natTaxRate = natTaxRate;
	}

	public String getPoCode() {
		return poCode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMainmemo() {
		return mainmemo;
	}

	public void setMainmemo(String mainmemo) {
		this.mainmemo = mainmemo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBredvouch() {
		return bredvouch;
	}

	public void setBredvouch(String bredvouch) {
		this.bredvouch = bredvouch;
	}

	public String getVendorcode() {
		return vendorcode;
	}

	public void setVendorcode(String vendorcode) {
		this.vendorcode = vendorcode;
	}

	public String getVendorname() {
		return vendorname;
	}

	public void setVendorname(String vendorname) {
		this.vendorname = vendorname;
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

	public String getInventory_code() {
		return inventory_code;
	}

	public void setInventory_code(String inventory_code) {
		this.inventory_code = inventory_code;
	}

	public String getNat_tax_price() {
		return nat_tax_price;
	}

	public void setNat_tax_price(String nat_tax_price) {
		this.nat_tax_price = nat_tax_price;
	}

	public String getMaterial_quantity() {
		return material_quantity;
	}

	public void setMaterial_quantity(String material_quantity) {
		this.material_quantity = material_quantity;
	}

	public String getMaterial_tax_price() {
		return material_tax_price;
	}

	public void setMaterial_tax_price(String material_tax_price) {
		this.material_tax_price = material_tax_price;
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
