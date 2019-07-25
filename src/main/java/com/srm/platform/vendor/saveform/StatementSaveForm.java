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
	private Long company;
	private Integer type;
	private Integer invoice_state;
	private Integer invoice_type;
	private Double costSum;
	private Double taxCostSum;
	private Double taxSum;
	private Double adjustCostSum;

	private Float tax_rate;

	private MultipartFile attach;

	private List<Map<String, String>> table;

	private String content;
	
	private String action;
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Double getCostSum() {
		return costSum;
	}

	public void setCostSum(Double costSum) {
		this.costSum = costSum;
	}

	public Double getTaxCostSum() {
		return taxCostSum;
	}

	public void setTaxCostSum(Double taxCostSum) {
		this.taxCostSum = taxCostSum;
	}

	public Double getTaxSum() {
		return taxSum;
	}

	public void setTaxSum(Double taxSum) {
		this.taxSum = taxSum;
	}

	public Double getAdjustCostSum() {
		return adjustCostSum;
	}

	public void setAdjustCostSum(Double adjustCostSum) {
		this.adjustCostSum = adjustCostSum;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getInvoice_state() {
		return invoice_state;
	}

	public void setInvoice_state(Integer invoice_state) {
		this.invoice_state = invoice_state;
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

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}

	public boolean isInvoiceAction() {
		return "invoice".equalsIgnoreCase(action);
	}
}
