package com.srm.platform.vendor.utility;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class U8InvoicePostEntry {

	private String inventorycode;
	private Float quantity;
	private Float originalmoney;
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

	public Float getOriginalmoney() {
		return originalmoney;
	}

	public void setOriginalmoney(Float originalmoney) {
		this.originalmoney = originalmoney;
	}

	public Float getTaxrate() {
		return taxrate;
	}

	public void setTaxrate(Float taxrate) {
		this.taxrate = taxrate;
	}

}
