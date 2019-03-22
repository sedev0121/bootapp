package com.srm.platform.vendor.searchitem;

import java.io.Serializable;
import java.util.Date;

import com.srm.platform.vendor.utility.Utils;

public class NoticeReadSearchResult implements Serializable {

	private static final long serialVersionUID = -7774864748081640759L;

	String realname;

	String vendorname;

	String unitname;

	Date readdate;

	public NoticeReadSearchResult(String realname, String vendorname, String unitname, Date readdate) {

		this.realname = realname;
		this.vendorname = vendorname;
		this.unitname = unitname;
		this.readdate = readdate;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getVendorname() {
		return vendorname;
	}

	public void setVendorname(String vendorname) {
		this.vendorname = vendorname;
	}

	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

	public String getRead_date() {
		return Utils.formatDateTime(readdate);
	}

	public void setReaddate(Date readdate) {
		this.readdate = readdate;
	}

}
