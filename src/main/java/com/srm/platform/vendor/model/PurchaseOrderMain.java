package com.srm.platform.vendor.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.srm.platform.vendor.searchitem.PurchaseOrderSearchResult;

@Entity

@SqlResultSetMapping(name = "PurchaseOrderSearchResult", classes = {
	@ConstructorResult(targetClass = PurchaseOrderSearchResult.class, columns = {
		@ColumnResult(name = "code", type = String.class), 
		@ColumnResult(name = "vencode", type = String.class), 
		@ColumnResult(name = "audittime", type = Date.class),
		@ColumnResult(name = "state", type = String.class),
		@ColumnResult(name = "vendorname", type = String.class),
		@ColumnResult(name = "companyname", type = String.class),
		@ColumnResult(name = "deployername", type = String.class),
		@ColumnResult(name = "reviewername", type = String.class),
		@ColumnResult(name = "deploydate", type = Date.class),
		@ColumnResult(name = "reviewdate", type = Date.class),
		@ColumnResult(name = "maker", type = String.class), 
		@ColumnResult(name = "makedate", type = Date.class),
		@ColumnResult(name = "sum", type = Double.class), 
		@ColumnResult(name = "money", type = Double.class),
		@ColumnResult(name = "srmstate", type = Integer.class),
		@ColumnResult(name = "purchase_type_name", type = String.class),
		@ColumnResult(name = "prepay_money", type = Double.class),
		@ColumnResult(name = "verifier", type = String.class),
		@ColumnResult(name = "closer", type = String.class),
		@ColumnResult(name = "department", type = String.class),
		@ColumnResult(name = "person", type = String.class),
		@ColumnResult(name = "tax_rate", type = Double.class),
		@ColumnResult(name = "exchange_rate", type = Double.class),
		@ColumnResult(name = "currency", type = String.class)
	}) 
})

@Table(name = "purchase_order_main")
public class PurchaseOrderMain {
	@Id
	private String code;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "vencode", referencedColumnName = "code")
	Vendor vendor;

	private String poid;
	
	@JsonProperty("purchase_type_name")
	private String purchaseTypeName;

	private String remark;
	private String state;

	private Integer srmstate;
	private Date orderdate;

	private String maker;
	private Date makedate;

	private Date audittime;
	private Date changeaudittime;

	private String verifier;
	private String changeverifier;

	private String closer;
	private Date closedate;

	private String locker;
	private Date lockdate;
	
	@JsonProperty("tax_rate")
	private Double taxRate;
	
	private String currency;
	
	@JsonProperty("exchange_rate")
	private Double exchangeRate;
	
	private String department;
	private String person;
	

	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "company_id", referencedColumnName = "id")
	private Company company;
	
	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "store_id", referencedColumnName = "id")
	private Store store;
	
	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "deployer", referencedColumnName = "id")
	private Account deployer;
	private Date deploydate;

	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "reviewer", referencedColumnName = "id")
	private Account reviewer;
	private Date reviewdate;

	public PurchaseOrderMain() {

	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(Double exchangeRate) {
		this.exchangeRate = exchangeRate;
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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public Account getDeployer() {
		return deployer;
	}

	public void setDeployer(Account deployer) {
		this.deployer = deployer;
	}

	public Date getDeploydate() {
		return deploydate;
	}

	public void setDeploydate(Date deploydate) {
		this.deploydate = deploydate;
	}

	public Account getReviewer() {
		return reviewer;
	}

	public void setReviewer(Account reviewer) {
		this.reviewer = reviewer;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public String getPurchaseTypeName() {
		return purchaseTypeName;
	}

	public void setPurchaseTypeName(String purchaseTypeName) {
		this.purchaseTypeName = purchaseTypeName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public String getVerifier() {
		return verifier;
	}

	public void setVerifier(String verifier) {
		this.verifier = verifier;
	}

	public String getCloser() {
		return closer;
	}

	public void setCloser(String closer) {
		this.closer = closer;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Integer getSrmstate() {
		return srmstate;
	}

	public void setSrmstate(Integer srmstate) {
		this.srmstate = srmstate;
	}

	public String getLocker() {
		return locker;
	}

	public void setLocker(String locker) {
		this.locker = locker;
	}

	public Date getLockdate() {
		return lockdate;
	}

	public void setLockdate(Date lockdate) {
		this.lockdate = lockdate;
	}

	public Date getReviewdate() {
		return reviewdate;
	}

	public void setReviewdate(Date reviewdate) {
		this.reviewdate = reviewdate;
	}

	public Date getClosedate() {
		return closedate;
	}

	public void setClosedate(Date closedate) {
		this.closedate = closedate;
	}

	public Date getOrderdate() {
		return orderdate;
	}

	public void setOrderdate(Date orderdate) {
		this.orderdate = orderdate;
	}

	public String getChangeverifier() {
		return changeverifier;
	}

	public void setChangeverifier(String changeverifier) {
		this.changeverifier = changeverifier;
	}

	public Date getChangeaudittime() {
		return changeaudittime;
	}

	public void setChangeaudittime(Date changeaudittime) {
		this.changeaudittime = changeaudittime;
	}

	public Date getAudittime() {
		return audittime;
	}

	public void setAudittime(Date audittime) {
		this.audittime = audittime;
	}

	public String getPoid() {
		return poid;
	}

	public void setPoid(String poid) {
		this.poid = poid;
	}

}
