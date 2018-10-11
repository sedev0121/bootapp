package com.srm.platform.vendor.model;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

public class StockReportItem implements Serializable {
	private static final long serialVersionUID = -4666897469919025415L;

	private String code;
	
	private String name;
	
	private String specs;
	
	private String unitname;
	
	private String stockname;
	
	private String mainqty;
	
	private String availableqty;
	
	public StockReportItem(String code, String name, String specs, String unitname, String stockname, String mainqty, String availableqty) {
		this.code = code;
		this.name = name;
		this.specs = specs;
		this.unitname = unitname;
		this.stockname = stockname;
		this.mainqty = mainqty;
		this.availableqty = availableqty;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getSpecs() {
		return this.specs;
	}
	
	public String getUnitname() {
		return this.unitname;
	}
	
	public String getStockName() {
		return this.stockname;
	}
	
	public String getMainQty() {
		return this.mainqty;
	}
	
	public String getAvailableQty() {
		return this.availableqty;
	}
}
