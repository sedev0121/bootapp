package com.srm.platform.vendor.utility;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class U8InvoicePostEntry {

	private String cunitcode = "";
	private Float iinvexchrate = 0F;
	private Float inum = 0F;

	private String cinvcode;
	private Float ipbvquantity;
	private Float iOriTaxCost;
	private Float iOriCost;
	private Float iOriMoney;
	private Float iOriTaxPrice;
	private Float iOriSum;
	private Float iTaxRate;
	private Long rdsid;
	private Long iposid;
	private Integer ivouchrowno;
	private Date dindate;
	private Float inattaxprice;
	private Float iCost;
	private Float iMoney;
	private Float iTaxPrice;
	private Float iSum;

	public String getCinvcode() {
		return cinvcode;
	}

	public void setCinvcode(String cinvcode) {
		this.cinvcode = cinvcode;
	}

	public Float getIpbvquantity() {
		return ipbvquantity;
	}

	public void setIpbvquantity(Float ipbvquantity) {
		this.ipbvquantity = ipbvquantity;
	}

	public Float getiOriTaxCost() {
		return iOriTaxCost;
	}

	public void setiOriTaxCost(Float iOriTaxCost) {
		this.iOriTaxCost = iOriTaxCost;
	}

	public Float getiOriCost() {
		return iOriCost;
	}

	public void setiOriCost(Float iOriCost) {
		this.iOriCost = iOriCost;
	}

	public Float getiOriMoney() {
		return iOriMoney;
	}

	public void setiOriMoney(Float iOriMoney) {
		this.iOriMoney = iOriMoney;
	}

	public Float getiOriTaxPrice() {
		return iOriTaxPrice;
	}

	public void setiOriTaxPrice(Float iOriTaxPrice) {
		this.iOriTaxPrice = iOriTaxPrice;
	}

	public Float getiOriSum() {
		return iOriSum;
	}

	public void setiOriSum(Float iOriSum) {
		this.iOriSum = iOriSum;
	}

	public Float getiTaxRate() {
		return iTaxRate;
	}

	public void setiTaxRate(Float iTaxRate) {
		this.iTaxRate = iTaxRate;
	}

	public Long getRdsid() {
		return rdsid;
	}

	public void setRdsid(Long rdsid) {
		this.rdsid = rdsid;
	}

	public Long getIposid() {
		return iposid;
	}

	public void setIposid(Long iposid) {
		this.iposid = iposid;
	}

	public Integer getIvouchrowno() {
		return ivouchrowno;
	}

	public void setIvouchrowno(Integer ivouchrowno) {
		this.ivouchrowno = ivouchrowno;
	}

	public String getDindate() {
		return Utils.formatDateTime(dindate);
	}

	public void setDindate(Date dindate) {
		this.dindate = dindate;
	}

	public Float getInattaxprice() {
		return inattaxprice;
	}

	public void setInattaxprice(Float inattaxprice) {
		this.inattaxprice = inattaxprice;
	}

	public Float getiCost() {
		return iCost;
	}

	public void setiCost(Float iCost) {
		this.iCost = iCost;
	}

	public Float getiMoney() {
		return iMoney;
	}

	public void setiMoney(Float iMoney) {
		this.iMoney = iMoney;
	}

	public Float getiTaxPrice() {
		return iTaxPrice;
	}

	public void setiTaxPrice(Float iTaxPrice) {
		this.iTaxPrice = iTaxPrice;
	}

	public Float getiSum() {
		return iSum;
	}

	public void setiSum(Float iSum) {
		this.iSum = iSum;
	}

	public String getCunitcode() {
		return cunitcode;
	}

	public void setCunitcode(String cunitcode) {
		this.cunitcode = cunitcode;
	}

	public Float getIinvexchrate() {
		return iinvexchrate;
	}

	public void setIinvexchrate(Float iinvexchrate) {
		this.iinvexchrate = iinvexchrate;
	}

	public Float getInum() {
		return inum;
	}

	public void setInum(Float inum) {
		this.inum = inum;
	}

}
