package com.srm.platform.vendor.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StatementSaveForm {

	private String code;
	private String vendor;
	private String invoice_code;
	private String remark;

	private Integer state;
	private Long maker;
	private Date make_date;

	private Long verifier;
	private Date verify_date;

	private List<Map<String, String>> table;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getInvoice_code() {
		return invoice_code;
	}

	public void setInvoice_code(String invoice_code) {
		this.invoice_code = invoice_code;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Long getMaker() {
		return maker;
	}

	public void setMaker(Long maker) {
		this.maker = maker;
	}

	public Date getMake_date() {
		return make_date;
	}

	public void setMake_date(String make_date) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			this.make_date = dateFormatter.parse(make_date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Long getVerifier() {
		return verifier;
	}

	public void setVerifier(Long verifier) {
		this.verifier = verifier;
	}

	public Date getVerify_date() {
		return verify_date;
	}

	public void setVerify_date(String verify_date) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			this.verify_date = dateFormatter.parse(verify_date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Map<String, String>> getTable() {
		return table;
	}

	public void setTable(List<Map<String, String>> table) {
		this.table = table;
	}

}
