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
	
	double previousprice;
	
	double currentprice;
	
	double changepercent;
	
	double averageprice;
	
	public PriceChangeReportItem(String vendorname, String vendorcode,
			String inventoryname, String inventorycode, String specs, String unitname,
			double previousprice, double currentprice, double changepercent, double averageprice) {
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
	
	public double getPreviousprice() {
		return this.previousprice;
	}
	
	public void setPreviousPrice(double previousprice) {
		this.previousprice = previousprice;
	}
	
	public double getCurrentprice() {
		return this.currentprice;
	}
	
	public void setCurrentprice(double currentprice) {
		this.currentprice = currentprice;
	}

	public double getChangepercent() {
		return this.changepercent;
	}

	public void setChangepercent(double changepercent) {
		this.changepercent = changepercent;
	}
	
	public double getAverageprice() {
		return this.averageprice;
	}
	
	public void setAverageprice(double averageprice) {
		this.averageprice = averageprice;
	}
}
