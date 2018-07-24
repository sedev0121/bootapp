package com.srm.platform.vendor.utility;

import java.io.Serializable;
import java.util.Date;

public class PurchaseOrderSearchResult implements Serializable {

	private static final long serialVersionUID = 3692876780746931969L;

	private String code;

	private String state;

	private String vendorname;

	private String deployername;

	private String reviewername;

	private Date deploydate;

	private Date reviewdate;

	private String maker;

	private Date makedate;

	private Float sum;

	private Float money;

	private Float prepaymoney;

	private Integer srmstate;

	private String purchase_type_name;

	public PurchaseOrderSearchResult(String code, String state, String vendorname, String deployername,
			String reviewername, Date deploydate, Date reviewdate, String maker, Date makedate, Float sum, Float money,
			Integer srmstate, String purchase_type_name, Float prepaymoney) {
		this.code = code;
		this.state = state;
		this.vendorname = vendorname;
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
		this.prepaymoney = prepaymoney;

	}

	public Float getPrepaymoney() {
		return prepaymoney;
	}

	public void setPrepaymoney(Float prepaymoney) {
		this.prepaymoney = prepaymoney;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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

	public Float getSum() {
		return sum;
	}

	public void setSum(Float sum) {
		this.sum = sum;
	}

	public Float getMoney() {
		return money;
	}

	public void setMoney(Float money) {
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

}
