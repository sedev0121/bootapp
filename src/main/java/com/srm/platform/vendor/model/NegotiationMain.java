package com.srm.platform.vendor.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.Utils;

@Entity
@Table(name = "negotiation_main")
public class NegotiationMain implements Serializable {
	private static final long serialVersionUID = 5855332316773551036L;

	@Id
	private String code;

	@JsonProperty("order_code")
	private String orderCode;

	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "vendor_code")
	@ManyToOne()
	private Vendor vendor;

	@JsonProperty("tax_rate")
	private Double taxRate = Constants.DEFAULT_TAX_RATE;
	
	private Integer state = Constants.NEGOTIATION_STATE_NEW;
	

	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "maker_id")
	@ManyToOne()
	private Account maker;

	@JsonProperty("make_date")
	private Date makeDate = new Date();

	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "reviewer_id")
	@ManyToOne()
	private Account reviewer;

	@JsonProperty("review_date")
	private Date reviewDate;

	
	public NegotiationMain() {
		this.code = Utils.generateId();
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getOrderCode() {
		return orderCode;
	}


	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}


	public Vendor getVendor() {
		return vendor;
	}


	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}


	public Double getTaxRate() {
		return taxRate;
	}


	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}


	public Integer getState() {
		return state;
	}


	public void setState(Integer state) {
		this.state = state;
	}


	public Account getMaker() {
		return maker;
	}


	public void setMaker(Account maker) {
		this.maker = maker;
	}


	public Date getMakeDate() {
		return makeDate;
	}


	public void setMakeDate(Date makeDate) {
		this.makeDate = makeDate;
	}


	public Account getReviewer() {
		return reviewer;
	}


	public void setReviewer(Account reviewer) {
		this.reviewer = reviewer;
	}


	public Date getReviewDate() {
		return reviewDate;
	}


	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	
}
