package com.srm.platform.vendor.utility;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class U8VenpriceadjustPostData {

	private String ccode;
	private String maker;

	private List<U8VenpriceadjustPostEntry> entry;

	public String getCcode() {
		return ccode;
	}

	public void setCcode(String ccode) {
		this.ccode = ccode;
	}

	public String getMaker() {
		return maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}

	public List<U8VenpriceadjustPostEntry> getEntry() {
		return entry;
	}

	public void setEntry(List<U8VenpriceadjustPostEntry> entry) {
		this.entry = entry;
	}

}
