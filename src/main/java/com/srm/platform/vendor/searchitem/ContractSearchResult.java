package com.srm.platform.vendor.searchitem;

import java.io.Serializable;

public class ContractSearchResult implements Serializable {

	private static final long serialVersionUID = -2320094674551818628L;

	String code;
	Integer state;
	String name;
	Integer type;
	Integer kind;
	String company_name;
	String vendor_code;
	String vendor_name;
	String project_no;
	Integer price_type;
	String date;
	String start_date;
	String end_date;	
	Integer quantity_type;	
	String tax_rate;	
	String pay_mode;
	String make_name;
	String make_date;
	
	String memo;

	public ContractSearchResult(String code, Integer state, String name, Integer type, Integer kind, 
			String company_name, String vendor_code, String vendor_name, String project_no, Integer price_type, String date, String start_date,
			String end_date, Integer quantity_type, String tax_rate, String pay_mode, String make_name,
			String make_date, String memo) {

		this.code = code;
		this.state = state;
		this.name = name;
		this.date = date;
		this.type = type;
		this.kind = kind;
		this.company_name = company_name;
		this.vendor_code = vendor_code;
		this.vendor_name = vendor_name;
		this.project_no = project_no;
		this.make_date = make_date;
		this.price_type = price_type;
		this.date = date;
		this.start_date = start_date;
		this.end_date = end_date;
		this.quantity_type = quantity_type;
		this.tax_rate = tax_rate;
		
		this.pay_mode = pay_mode;
		this.make_name = make_name;
		this.make_date = make_date;
		this.memo = memo;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getKind() {
		return kind;
	}

	public void setKind(Integer kind) {
		this.kind = kind;
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

	public String getProject_no() {
		return project_no;
	}

	public void setProject_no(String project_no) {
		this.project_no = project_no;
	}

	public Integer getPrice_type() {
		return price_type;
	}

	public void setPrice_type(Integer price_type) {
		this.price_type = price_type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public Integer getQuantity_type() {
		return quantity_type;
	}

	public void setQuantity_type(Integer quantity_type) {
		this.quantity_type = quantity_type;
	}

	public String getTax_rate() {
		return tax_rate;
	}

	public void setTax_rate(String tax_rate) {
		this.tax_rate = tax_rate;
	}

	public String getPay_mode() {
		return pay_mode;
	}

	public void setPay_mode(String pay_mode) {
		this.pay_mode = pay_mode;
	}

	public String getMake_name() {
		return make_name;
	}

	public void setMake_name(String make_name) {
		this.make_name = make_name;
	}

	public String getMake_date() {
		return make_date;
	}

	public void setMake_date(String make_date) {
		this.make_date = make_date;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	
}
