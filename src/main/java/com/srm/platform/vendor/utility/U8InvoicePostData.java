package com.srm.platform.vendor.utility;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class U8InvoicePostData {

	private String invoicetype;
	private String purchasecode;
	private String date;
	private String vendorcode;
	private String delegatecode;
	private String maker;
	private String invoicecode;

	private List<U8InvoicePostEntry> entry;

	public String getInvoicetype() {
		return invoicetype;
	}

	public void setInvoicetype(String invoicetype) {
		this.invoicetype = invoicetype;
	}

	public String getPurchasecode() {
		return purchasecode;
	}

	public void setPurchasecode(String purchasecode) {
		this.purchasecode = purchasecode;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getVendorcode() {
		return vendorcode;
	}

	public void setVendorcode(String vendorcode) {
		this.vendorcode = vendorcode;
	}

	public String getDelegatecode() {
		return delegatecode;
	}

	public void setDelegatecode(String delegatecode) {
		this.delegatecode = delegatecode;
	}

	public String getMaker() {
		return maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}

	public String getInvoicecode() {
		return invoicecode;
	}

	public void setInvoicecode(String invoicecode) {
		this.invoicecode = invoicecode;
	}

	public List<U8InvoicePostEntry> getEntry() {
		return entry;
	}

	public void setEntry(List<U8InvoicePostEntry> entry) {
		this.entry = entry;
	}

}
