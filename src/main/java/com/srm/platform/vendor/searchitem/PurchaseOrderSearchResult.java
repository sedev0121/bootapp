package com.srm.platform.vendor.searchitem;

import java.io.Serializable;
import java.util.Date;

public class PurchaseOrderSearchResult implements Serializable {

	private static final long serialVersionUID = 3692876780746931969L;

	private String code;

	private String state;

	private String vencode;
	
	private String verifier;
	
	private String closer;
	
	private Date closedate;
	private Date orderdate;
	
	private Date audittime;

	private String vendorname;
	
	private String companyname;

	private String deployername;

	private String reviewername;

	private Date deploydate;

	private Date reviewdate;

	private String maker;

	private Date makedate;

	private Double sum;

	private Double money;

	private Double prepay_money;

	private Integer srmstate;

	private String purchase_type_name;
	
	private String department;
	private String person;
	private Double tax_rate;
	private Double exchange_rate;
	private String currency;

	private String contract_code;
	private Double base_price;
	
	public PurchaseOrderSearchResult(String code, String vencode, Date audittime, String state, String vendorname, String companyname,
			String deployername, String reviewername, Date deploydate, Date reviewdate, String maker, Date makedate,
			Double sum, Double money, Integer srmstate, String purchase_type_name, Double prepay_money, String verifier, String closer, 
			String department, String person, Double tax_rate, Double exchange_rate, String currency, Date closedate, Date orderdate,
			String contract_code, Double base_price) {
		this.code = code;
		this.setVencode(vencode);
		this.setAudittime(audittime);
		this.state = state;
		this.vendorname = vendorname;
		this.companyname = companyname;
		this.deployername = deployername;
		this.deploydate = deploydate;
		this.reviewdate = reviewdate;
		this.reviewername = reviewername;
		this.makedate = makedate;
		this.maker = maker;
		this.sum = sum;
		this.money = money;
		this.srmstate = srmstate;
		this.purchase_type_name = purchase_type_name;
		this.prepay_money = prepay_money;
		this.verifier = verifier;
		this.closer = closer;
		
		this.department = department;
		this.person = person;
		this.tax_rate = tax_rate;
		this.exchange_rate = exchange_rate;
		this.currency = currency;
		this.closedate = closedate;
		this.orderdate = orderdate;

		this.contract_code = contract_code;
		this.base_price = base_price;
	}

	public Double getPrepay_money() {
		return prepay_money;
	}

	public void setPrepay_money(Double prepay_money) {
		this.prepay_money = prepay_money;
	}

	public String getContract_code() {
		return contract_code;
	}

	public void setContract_code(String contract_code) {
		this.contract_code = contract_code;
	}

	public Double getBase_price() {
		return base_price;
	}

	public void setBase_price(Double base_price) {
		this.base_price = base_price;
	}

	public Date getOrderdate() {
		return orderdate;
	}

	public void setOrderdate(Date orderdate) {
		this.orderdate = orderdate;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public Double getTax_rate() {
		return tax_rate;
	}

	public void setTax_rate(Double tax_rate) {
		this.tax_rate = tax_rate;
	}

	public Double getExchange_rate() {
		return exchange_rate;
	}

	public void setExchange_rate(Double exchange_rate) {
		this.exchange_rate = exchange_rate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCloser() {
		return closer;
	}

	public void setCloser(String closer) {
		this.closer = closer;
	}

	public Date getClosedate() {
		return closedate;
	}

	public void setClosedate(Date closedate) {
		this.closedate = closedate;
	}

	public String getVerifier() {
		return verifier;
	}

	public void setVerifier(String verifier) {
		this.verifier = verifier;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getVendorname() {
		return vendorname;
	}

	public void setVendorname(String vendorname) {
		this.vendorname = vendorname;
	}

	public String getDeployername() {
		return deployername;
	}

	public void setDeployername(String deployername) {
		this.deployername = deployername;
	}

	public String getReviewername() {
		return reviewername;
	}

	public void setReviewername(String reviewername) {
		this.reviewername = reviewername;
	}

	public Date getDeploydate() {
		return deploydate;
	}

	public void setDeploydate(Date deploydate) {
		this.deploydate = deploydate;
	}

	public Date getReviewdate() {
		return reviewdate;
	}

	public void setReviewdate(Date reviewdate) {
		this.reviewdate = reviewdate;
	}

	public String getMaker() {
		return maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}

	public Date getMakedate() {
		return makedate;
	}

	public void setMakedate(Date makedate) {
		this.makedate = makedate;
	}

	public Double getSum() {
		return sum;
	}

	public void setSum(Double sum) {
		this.sum = sum;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Integer getSrmstate() {
		return srmstate;
	}

	public void setSrmstate(Integer srmstate) {
		this.srmstate = srmstate;
	}

	public String getPurchase_type_name() {
		return purchase_type_name;
	}

	public void setPurchase_type_name(String purchase_type_name) {
		this.purchase_type_name = purchase_type_name;
	}

	public String getVencode() {
		return vencode;
	}

	public void setVencode(String vencode) {
		this.vencode = vencode;
	}

	public Date getAudittime() {
		return audittime;
	}

	public void setAudittime(Date audittime) {
		this.audittime = audittime;
	}

}
