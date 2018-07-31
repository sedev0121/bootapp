package com.srm.platform.vendor.utility;

import java.io.Serializable;

public class StatementSearchResult implements Serializable {

	private static final long serialVersionUID = -2320094674551818628L;

	String code;

	String state;

	String maker;

	String makedate;

	String verifier;

	String verifydate;

	String confirmer;

	String confirmdate;

	String remark;

	String type;

	String invoice_code;

	String vendor_name;

	String vendor_code;

	public StatementSearchResult(String code, String state, String maker, String makedate, String verifier,
			String verifydate, String confirmer, String confirmdate, String remark, String invoice_code,
			String vendor_name, String vendor_code, String type) {

		this.code = code;
		this.state = state;
		this.maker = maker;
		this.makedate = makedate;
		this.verifier = verifier;
		this.verifydate = verifydate;
		this.confirmdate = confirmdate;
		this.confirmer = confirmer;
		this.remark = remark;
		this.invoice_code = invoice_code;
		this.vendor_code = vendor_code;
		this.vendor_name = vendor_name;
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getConfirmer() {
		return confirmer;
	}

	public void setConfirmer(String confirmer) {
		this.confirmer = confirmer;
	}

	public String getConfirmdate() {
		return confirmdate;
	}

	public void setConfirmdate(String confirmdate) {
		this.confirmdate = confirmdate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMaker() {
		return maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}

	public String getMakedate() {
		return makedate;
	}

	public void setMakedate(String makedate) {
		this.makedate = makedate;
	}

	public String getVerifier() {
		return verifier;
	}

	public void setVerifier(String verifier) {
		this.verifier = verifier;
	}

	public String getVerifydate() {
		return verifydate;
	}

	public void setVerifydate(String verifydate) {
		this.verifydate = verifydate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getInvoice_code() {
		return invoice_code;
	}

	public void setInvoice_code(String invoice_code) {
		this.invoice_code = invoice_code;
	}

	public String getVendor_name() {
		return vendor_name;
	}

	public void setVendor_name(String vendor_name) {
		this.vendor_name = vendor_name;
	}

	public String getVendor_code() {
		return vendor_code;
	}

	public void setVendor_code(String vendor_code) {
		this.vendor_code = vendor_code;
	}

}
