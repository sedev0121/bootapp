
package com.srm.platform.vendor.utility;

import java.io.Serializable;
import java.util.Date;

public class InquerySearchResult implements Serializable {

	private static final long serialVersionUID = -7476789702299949678L;
	private String ccode;

	String vendorname;

	String vendorcode;

	Date dstartdate;

	Date denddate;

	String makername;

	String verifiername;

	Date dmakedate;

	Date dverifydate;

	Integer iverifystate;

	Integer type;

	Integer isupplytype;

	Float itaxrate;

	public InquerySearchResult(String ccode, String vendorname, String vendorcode, Date dstartdate, Date denddate,
			String makername, String verifiername, Date dmakedate, Date dverifydate, Integer iverifystate, Integer type,
			Integer isupplytype, Float itaxrate) {
		this.ccode = ccode;
		this.vendorname = vendorname;
		this.vendorcode = vendorcode;
		this.dstartdate = dstartdate;
		this.denddate = denddate;
		this.makername = makername;
		this.verifiername = verifiername;
		this.dmakedate = dmakedate;
		this.dverifydate = dverifydate;
		this.iverifystate = iverifystate;
		this.type = type;
		this.isupplytype = isupplytype;
		this.itaxrate = itaxrate;
	}

	public String getCcode() {
		return ccode;
	}

	public void setCcode(String ccode) {
		this.ccode = ccode;
	}

	public String getVendorname() {
		return vendorname;
	}

	public void setVendorname(String vendorname) {
		this.vendorname = vendorname;
	}

	public String getVendorcode() {
		return vendorcode;
	}

	public void setVendorcode(String vendorcode) {
		this.vendorcode = vendorcode;
	}

	public Date getDstartdate() {
		return dstartdate;
	}

	public void setDstartdate(Date dstartdate) {
		this.dstartdate = dstartdate;
	}

	public Date getDenddate() {
		return denddate;
	}

	public void setDenddate(Date denddate) {
		this.denddate = denddate;
	}

	public String getMakername() {
		return makername;
	}

	public void setMakername(String makername) {
		this.makername = makername;
	}

	public String getVerifiername() {
		return verifiername;
	}

	public void setVerifiername(String verifiername) {
		this.verifiername = verifiername;
	}

	public Date getDmakedate() {
		return dmakedate;
	}

	public void setDmakedate(Date dmakedate) {
		this.dmakedate = dmakedate;
	}

	public Date getDverifydate() {
		return dverifydate;
	}

	public void setDverifydate(Date dverifydate) {
		this.dverifydate = dverifydate;
	}

	public Integer getIverifystate() {
		return iverifystate;
	}

	public void setIverifystate(Integer iverifystate) {
		this.iverifystate = iverifystate;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getIsupplytype() {
		return isupplytype;
	}

	public void setIsupplytype(Integer isupplytype) {
		this.isupplytype = isupplytype;
	}

	public Float getItaxrate() {
		return itaxrate;
	}

	public void setItaxrate(Float itaxrate) {
		this.itaxrate = itaxrate;
	}

}
