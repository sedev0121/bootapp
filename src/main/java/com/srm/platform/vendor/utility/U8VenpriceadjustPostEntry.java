package com.srm.platform.vendor.utility;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class U8VenpriceadjustPostEntry {

	private String cvencode;
	private String cinvcode;
	private Float iunitprice;
	private Float itaxrate;
	private Float itaxunitprice;
	private String dstartdate;

	public String getCvencode() {
		return cvencode;
	}

	public void setCvencode(String cvencode) {
		this.cvencode = cvencode;
	}

	public String getCinvcode() {
		return cinvcode;
	}

	public void setCinvcode(String cinvcode) {
		this.cinvcode = cinvcode;
	}

	public Float getIunitprice() {
		return iunitprice;
	}

	public void setIunitprice(Float iunitprice) {
		this.iunitprice = iunitprice;
	}

	public Float getItaxrate() {
		return itaxrate;
	}

	public void setItaxrate(Float itaxrate) {
		this.itaxrate = itaxrate;
	}

	public Float getItaxunitprice() {
		return itaxunitprice;
	}

	public void setItaxunitprice(Float itaxunitprice) {
		this.itaxunitprice = itaxunitprice;
	}

	public String getDstartdate() {
		return dstartdate;
	}

	public void setDstartdate(String dstartdate) {
		this.dstartdate = dstartdate;
	}

}
