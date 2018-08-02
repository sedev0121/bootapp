package com.srm.platform.vendor.utility;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class U8InvoicePostEntry {

	private String inventorycode;
	private Float quantity;
	private Float oritaxcost;
	private Float taxrate;

	public String getInventorycode() {
		return inventorycode;
	}

	public void setInventorycode(String inventorycode) {
		this.inventorycode = inventorycode;
	}

	public Float getQuantity() {
		return quantity;
	}

	public void setQuantity(Float quantity) {
		this.quantity = quantity;
	}

	public Float getOritaxcost() {
		return oritaxcost;
	}

	public void setOritaxcost(Float oritaxcost) {
		this.oritaxcost = oritaxcost;
	}

	public Float getTaxrate() {
		return taxrate;
	}

	public void setTaxrate(Float taxrate) {
		this.taxrate = taxrate;
	}

}
