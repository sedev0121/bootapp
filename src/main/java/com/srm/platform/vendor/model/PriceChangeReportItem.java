package com.srm.platform.vendor.model;

import java.io.Serializable;

public class PriceChangeReportItem implements Serializable {

	private static final long serialVersionUID = 4569283053781554436L;

	String vendorname;
	
	String vendorcode;

	String inventoryname;
	
	String inventorycode;

	String specs;
	
	String unitname;
	
	float previousprice;
	
	float currentprice;
	
	float changepercent;
	
	float averageprice;
	
	public PriceChangeReportItem(String vendorname, String vendorcode,
			String inventoryname, String inventorycode, String specs, String unitname,
			float previousprice, float currentprice, float changepercent, float averageprice) {
		this.vendorname = vendorname;
		this.vendorcode = vendorcode;
		this.inventoryname = inventoryname;
		this.inventorycode = inventorycode;
		this.specs = specs;
		this.unitname = unitname;
		this.previousprice = previousprice;
		this.currentprice = currentprice;
		this.changepercent = changepercent;
		this.averageprice = averageprice;
	}
	
	public String getVendorname() {
		return this.vendorname;
	}
	
	public void setVendorname(String vendorname) {
		this.vendorname = vendorname;
	}

	public String getVendorcode() {
		return this.vendorcode;
	}
	
	public void setVendorcode(String vendorcode) {
		this.vendorcode = vendorcode;
	}
	

	public String getInventoryName() {
		return this.inventoryname;
	}
	
	public void setInventoryName(String inventoryname) {
		this.inventoryname = inventoryname;
	}

	public String getInventorycode() {
		return this.inventorycode;
	}
	
	public void setInventorycode(String inventorycode) {
		this.inventorycode = inventorycode;
	}
	
	public String getSpecs() {
		return this.specs;
	}
	
	public void setSpecs(String specs) {
		this.specs = specs;
	}
	
	public String getUnitname() {
		return this.unitname;
	}
	
	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}
	
	public float getPreviousprice() {
		return this.previousprice;
	}
	
	public void setPreviousPrice(float previousprice) {
		this.previousprice = previousprice;
	}
	
	public float getCurrentprice() {
		return this.currentprice;
	}
	
	public void setCurrentprice(float currentprice) {
		this.currentprice = currentprice;
	}

	public float getChangepercent() {
		return this.changepercent;
	}

	public void setChangepercent(float changepercent) {
		this.changepercent = changepercent;
	}
	
	public float getAverageprice() {
		return this.averageprice;
	}
	
	public void setAverageprice(float averageprice) {
		this.averageprice = averageprice;
	}
}
