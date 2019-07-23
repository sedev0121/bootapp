package com.srm.platform.vendor.searchitem;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PurchaseInDetailResult implements Serializable {

	private static final long serialVersionUID = 2283867296635029703L;

	String id;

	String code;

	String date;
	String verify_date;

	@JsonProperty("row_no")
	Integer rowno;

	String inventory_name;

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

	@JsonProperty("confirmed_memo")
	String confirmedMemo;


	String vendorcode;
	String vendorname;
	String type;
	String bredvouch;
	String mainmemo;

	@JsonProperty("po_code")
	String poCode;
	
	@JsonProperty("po_row_no")
	Integer poRowNo;
	
	@JsonProperty("delivery_code")
	String deliveryCode;
	
	@JsonProperty("delivery_row_no")
	Integer deliveryRowNo;
	
	@JsonProperty("delivered_quantity")
	String deliveredQuantity;

	@JsonProperty("company_name")
	String companyName;
	
	@JsonProperty("store_name")
	String storeName;
	
	@JsonProperty("sync_date")
	Date syncDate;
	
	public PurchaseInDetailResult(String id, String code, String date, String verify_date,Integer rowno, String inventoryname,
			String inventorycode, String specs, String unitname, String quantity, String price, String cost,
			String tax_price, String tax_rate, String tax_cost, String confirmedMemo, String vendorname, String vendorcode, String type,
			String bredvouch, String state, String poCode, Integer poRowNo, String deliveryCode, Integer deliveryRowNo, 
			String deliveredQuantity, String companyName, String storeName, Date syncDate) {

		this.id = id;
		this.code = code;
		this.date = date;
		this.verify_date = verify_date;
		this.rowno = rowno;
		this.inventory_name = inventoryname;
		this.inventory_code = inventorycode;
		this.specs = specs;
		this.unitname = unitname;
		this.quantity = quantity;
		this.price = price;
		this.cost = cost;
		this.confirmedMemo = confirmedMemo;

		this.tax_price = tax_price;
		this.tax_cost = tax_cost;
		this.tax_rate = tax_rate;

		this.vendorcode = vendorcode;
		this.vendorname = vendorname;
		this.type = type;
		this.bredvouch = bredvouch;
		
		this.state = state;

		this.poCode = poCode;
		this.poRowNo = poRowNo;
		this.deliveryCode = deliveryCode;
		this.deliveryRowNo = deliveryRowNo;
		this.deliveredQuantity = deliveredQuantity;
		
		this.companyName = companyName;
		this.storeName = storeName;
		this.syncDate = syncDate;
		
	}

	public Date getSyncDate() {
		return syncDate;
	}

	public void setSyncDate(Date syncDate) {
		this.syncDate = syncDate;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getDeliveredQuantity() {
		return deliveredQuantity;
	}

	public void setConfirmedMemo(String confirmedMemo) {
		this.confirmedMemo = confirmedMemo;
	}

	public Integer getPoRowNo() {
		return poRowNo;
	}

	public void setPoRowNo(Integer poRowNo) {
		this.poRowNo = poRowNo;
	}

	public String getDeliveryCode() {
		return deliveryCode;
	}

	public void setDeliveryCode(String deliveryCode) {
		this.deliveryCode = deliveryCode;
	}

	public Integer getDeliveryRowNo() {
		return deliveryRowNo;
	}

	public void setDeliveryRowNo(Integer deliveryRowNo) {
		this.deliveryRowNo = deliveryRowNo;
	}

	public void setDeliveredQuantity(String deliveredQuantity) {
		this.deliveredQuantity = deliveredQuantity;
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

	public void setPoCode(String poCode) {
		this.poCode = poCode;
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

	public Integer getRowno() {
		return rowno;
	}

	public void setRowno(Integer rowno) {
		this.rowno = rowno;
	}

	public String getInventory_name() {
		return inventory_name;
	}

	public void setInventory_name(String inventoryname) {
		this.inventory_name = inventoryname;
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

	public String getConfirmedMemo() {
		return confirmedMemo;
	}

	public void setMemo(String confirmedMemo) {
		this.confirmedMemo = confirmedMemo;
	}

}
