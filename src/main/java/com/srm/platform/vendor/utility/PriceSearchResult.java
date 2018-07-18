package com.srm.platform.vendor.utility;

import java.io.Serializable;

public class PriceSearchResult implements Serializable {

	private static final long serialVersionUID = -9139148656062706083L;

	String cinvdate;

	String createdate;

	String description;

	String faddqty;

	String fauxunit;

	String vendorname;

	String inventoryname;

	String favdate;

	String fcanceldate;

	String fcancelno;

	String fprice;

	String ftax;

	String ftaxprice;

	String foldcheckdate;

	String fisoutside;

	String createname;

	String fnote;

	public PriceSearchResult(String cinvdate, String createdate, String description, String faddqty, String fauxunit,
			String vendorname, String inventoryname, String favdate, String fcanceldate, String fcancelno,
			String fprice, String ftax, String ftaxprice, String foldcheckdate, String fisoutside, String createname,
			String fnote) {
		this.cinvdate = cinvdate;
		this.createdate = createdate;
		this.description = description;
		this.faddqty = faddqty;
		this.fauxunit = fauxunit;
		this.vendorname = vendorname;
		this.inventoryname = inventoryname;
		this.favdate = favdate;
		this.fcanceldate = fcanceldate;
		this.fcancelno = fcancelno;
		this.fprice = fprice;
		this.ftax = ftax;
		this.ftaxprice = ftaxprice;
		this.foldcheckdate = foldcheckdate;
		this.fisoutside = fisoutside;
		this.createname = createname;
		this.fnote = fnote;

	}

	public String getCinvdate() {
		return cinvdate;
	}

	public void setCinvdate(String cinvdate) {
		this.cinvdate = cinvdate;
	}

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFaddqty() {
		return faddqty;
	}

	public void setFaddqty(String faddqty) {
		this.faddqty = faddqty;
	}

	public String getFauxunit() {
		return fauxunit;
	}

	public void setFauxunit(String fauxunit) {
		this.fauxunit = fauxunit;
	}

	public String getVendorname() {
		return vendorname;
	}

	public void setVendorname(String vendorname) {
		this.vendorname = vendorname;
	}

	public String getInventoryname() {
		return inventoryname;
	}

	public void setInventoryname(String inventoryname) {
		this.inventoryname = inventoryname;
	}

	public String getFavdate() {
		return favdate;
	}

	public void setFavdate(String favdate) {
		this.favdate = favdate;
	}

	public String getFcanceldate() {
		return fcanceldate;
	}

	public void setFcanceldate(String fcanceldate) {
		this.fcanceldate = fcanceldate;
	}

	public String getFcancelno() {
		return fcancelno;
	}

	public void setFcancelno(String fcancelno) {
		this.fcancelno = fcancelno;
	}

	public String getFprice() {
		return fprice;
	}

	public void setFprice(String fprice) {
		this.fprice = fprice;
	}

	public String getFtax() {
		return ftax;
	}

	public void setFtax(String ftax) {
		this.ftax = ftax;
	}

	public String getFtaxprice() {
		return ftaxprice;
	}

	public void setFtaxprice(String ftaxprice) {
		this.ftaxprice = ftaxprice;
	}

	public String getFoldcheckdate() {
		return foldcheckdate;
	}

	public void setFoldcheckdate(String foldcheckdate) {
		this.foldcheckdate = foldcheckdate;
	}

	public String getFisoutside() {
		return fisoutside;
	}

	public void setFisoutside(String fisoutside) {
		this.fisoutside = fisoutside;
	}

	public String getCreatename() {
		return createname;
	}

	public void setCreatename(String createname) {
		this.createname = createname;
	}

	public String getFnote() {
		return fnote;
	}

	public void setFnote(String fnote) {
		this.fnote = fnote;
	}

}
