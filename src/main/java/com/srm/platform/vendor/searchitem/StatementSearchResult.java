package com.srm.platform.vendor.searchitem;

import java.io.Serializable;

public class StatementSearchResult implements Serializable {

	private static final long serialVersionUID = -2320094674551818628L;

	String code;
	String date;
	String type;
	String company_name;
	String vendor_code;
	String vendor_name;
	String state;
	
	String make_date;	
	String review_date;
	String deploy_date;
	String confirm_date;
	String invoice_code;
	String invoice_state;
	String invoice_type;

	String erp_invoice_make_name;
	String erp_invoice_make_date;

	public StatementSearchResult(String code, String date, String type, String company_name, String vendor_code, 
			String vendor_name, String state, String make_date, String review_date, String deploy_date, String confirm_date,
			String invoice_code, String invoice_state, String invoice_type, String erp_invoice_make_name, String erp_invoice_make_date) {

		this.code = code;
		this.date = date;
		this.type = type;
		this.company_name = company_name;
		this.vendor_code = vendor_code;
		this.vendor_name = vendor_name;
		this.state = state;
		this.make_date = make_date;
		this.review_date = review_date;
		this.deploy_date = deploy_date;
		this.confirm_date = confirm_date;
		this.invoice_code = invoice_code;
		this.invoice_state = invoice_state;
		this.invoice_type = invoice_type;
		this.erp_invoice_make_name = erp_invoice_make_name;
		this.erp_invoice_make_date = erp_invoice_make_date;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getVendor_code() {
		return vendor_code;
	}

	public void setVendor_code(String vendor_code) {
		this.vendor_code = vendor_code;
	}

	public String getVendor_name() {
		return vendor_name;
	}

	public void setVendor_name(String vendor_name) {
		this.vendor_name = vendor_name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMake_date() {
		return make_date;
	}

	public void setMake_date(String make_date) {
		this.make_date = make_date;
	}

	public String getReview_date() {
		return review_date;
	}

	public void setReview_date(String review_date) {
		this.review_date = review_date;
	}

	public String getDeploy_date() {
		return deploy_date;
	}

	public void setDeploy_date(String deploy_date) {
		this.deploy_date = deploy_date;
	}

	public String getConfirm_date() {
		return confirm_date;
	}

	public void setConfirm_date(String confirm_date) {
		this.confirm_date = confirm_date;
	}

	public String getInvoice_code() {
		return invoice_code;
	}

	public void setInvoice_code(String invoice_code) {
		this.invoice_code = invoice_code;
	}

	public String getInvoice_state() {
		return invoice_state;
	}

	public void setInvoice_state(String invoice_state) {
		this.invoice_state = invoice_state;
	}

	public String getInvoice_type() {
		return invoice_type;
	}

	public void setInvoice_type(String invoice_type) {
		this.invoice_type = invoice_type;
	}

	public String getErp_invoice_make_name() {
		return erp_invoice_make_name;
	}

	public void setErp_invoice_make_name(String erp_invoice_make_name) {
		this.erp_invoice_make_name = erp_invoice_make_name;
	}

	public String getErp_invoice_make_date() {
		return erp_invoice_make_date;
	}

	public void setErp_invoice_make_date(String erp_invoice_make_date) {
		this.erp_invoice_make_date = erp_invoice_make_date;
	}
	
	
}
