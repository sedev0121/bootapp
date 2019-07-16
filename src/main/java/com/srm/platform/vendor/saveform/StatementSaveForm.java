package com.srm.platform.vendor.saveform;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.srm.platform.vendor.utility.Utils;

public class StatementSaveForm {

	private String code;
	private String vendor;
	private String invoice_code;
	private String remark;

	private Integer state;
	private Long maker;
	private Date make_date;
	private Integer type;
	private Integer invoice_type;

	private Long verifier;
	private Date verify_date;

	private Float tax_rate;

	private MultipartFile attach;

	private List<Map<String, String>> table;

	private String content;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getInvoice_type() {
		return invoice_type;
	}

	public void setInvoice_type(Integer invoice_type) {
		this.invoice_type = invoice_type;
	}

	public MultipartFile getAttach() {
		return attach;
	}

	public void setAttach(MultipartFile attach) {
		this.attach = attach;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public Float getTax_rate() {
		return tax_rate;
	}

	public void setTax_rate(Float tax_rate) {
		this.tax_rate = tax_rate;
	}

	public void setMake_date(Date make_date) {
		this.make_date = make_date;
	}

	public void setVerify_date(Date verify_date) {
		this.verify_date = verify_date;
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
		this.make_date = Utils.parseDate(make_date);
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
		this.verify_date = Utils.parseDate(verify_date);
	}

	public List<Map<String, String>> getTable() {
		return table;
	}

	public void setTable(List<Map<String, String>> table) {
		this.table = table;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
