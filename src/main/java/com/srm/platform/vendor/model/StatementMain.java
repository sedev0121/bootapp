package com.srm.platform.vendor.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "statement_main")
public class StatementMain {
	@Id
	private String code;

	private Date makedate;

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "maker_id", referencedColumnName = "id")
	Account maker;

	private Date verifydate;

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "verifier_id", referencedColumnName = "id")
	Account verifier;

	@Column(name = "invoice_code")
	private String invoiceCode;

	private String remark;
	private Integer state;

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "vendor_code", referencedColumnName = "code")
	Vendor vendor;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getMakedate() {
		return makedate;
	}

	public void setMakedate(Date makedate) {
		this.makedate = makedate;
	}

	public Date getVerifydate() {
		return verifydate;
	}

	public void setVerifydate(Date verifydate) {
		this.verifydate = verifydate;
	}

	public Account getMaker() {
		return maker;
	}

	public void setMaker(Account maker) {
		this.maker = maker;
	}

	public Account getVerifier() {
		return verifier;
	}

	public void setVerifier(Account verifier) {
		this.verifier = verifier;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
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

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

}
