package com.srm.platform.vendor.saveform;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public class ContractSaveForm {

	private String code;
	private String date;
	private String name;
	private String project_no;
	private Double base_price;
	private Double floating_price;
	private Integer floating_direction;
	private String memo;
	private String pay_mode;
	
	private Integer type;	
	private Integer state;
	private Integer kind;
	private Integer price_type;	
	private Integer quantity_type;
	
	private Long company;
	private String vendor;
	private Integer tax_rate;
	private String start_date;
	private String end_date;

	private List<MultipartFile> attach;

	private List<Map<String, String>> table;
	
	private List<Long> attachIds;

	private String content;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getFloating_direction() {
		return floating_direction;
	}

	public void setFloating_direction(Integer floating_direction) {
		this.floating_direction = floating_direction;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProject_no() {
		return project_no;
	}

	public void setProject_no(String project_no) {
		this.project_no = project_no;
	}

	public Double getBase_price() {
		return base_price;
	}

	public void setBase_price(Double base_price) {
		this.base_price = base_price;
	}

	public Double getFloating_price() {
		return floating_price;
	}

	public void setFloating_price(Double floating_price) {
		this.floating_price = floating_price;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getPay_mode() {
		return pay_mode;
	}

	public void setPay_mode(String pay_mode) {
		this.pay_mode = pay_mode;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getKind() {
		return kind;
	}

	public void setKind(Integer kind) {
		this.kind = kind;
	}

	public Integer getPrice_type() {
		return price_type;
	}

	public void setPrice_type(Integer price_type) {
		this.price_type = price_type;
	}

	public Integer getQuantity_type() {
		return quantity_type;
	}

	public void setQuantity_type(Integer quantity_type) {
		this.quantity_type = quantity_type;
	}

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public Integer getTax_rate() {
		return tax_rate;
	}

	public void setTax_rate(Integer tax_rate) {
		this.tax_rate = tax_rate;
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

	public List<MultipartFile> getAttach() {
		return attach;
	}

	public void setAttach(List<MultipartFile> attach) {
		this.attach = attach;
	}

	public List<Map<String, String>> getTable() {
		return table;
	}

	public void setTable(List<Map<String, String>> table) {
		this.table = table;
	}

	public List<Long> getAttachIds() {
		return attachIds;
	}

	public void setAttachIds(List<Long> attachIds) {
		this.attachIds = attachIds;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}
