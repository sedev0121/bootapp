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
	private Double iOriTaxCost;
	private Double iOriCost;
	private Double iOriMoney;
	private Double iOriTaxPrice;
	private Double iOriSum;
	private Float iTaxRate;
	private Long rdsid;
	private Long iposid;
	private Integer ivouchrowno;
	private Date dindate;
	private Double inattaxprice;
	private Double iCost;
	private Double iMoney;
	private Double iTaxPrice;
	private Double iSum;

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

	public Double getiOriTaxCost() {
		return iOriTaxCost;
	}

	public void setiOriTaxCost(Double iOriTaxCost) {
		this.iOriTaxCost = iOriTaxCost;
	}

	public Double getiOriCost() {
		return iOriCost;
	}

	public void setiOriCost(Double iOriCost) {
		this.iOriCost = iOriCost;
	}

	public Double getiOriMoney() {
		return iOriMoney;
	}

	public void setiOriMoney(Double iOriMoney) {
		this.iOriMoney = iOriMoney;
	}

	public Double getiOriTaxPrice() {
		return iOriTaxPrice;
	}

	public void setiOriTaxPrice(Double iOriTaxPrice) {
		this.iOriTaxPrice = iOriTaxPrice;
	}

	public Double getiOriSum() {
		return iOriSum;
	}

	public void setiOriSum(Double iOriSum) {
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

	public Double getInattaxprice() {
		return inattaxprice;
	}

	public void setInattaxprice(Double inattaxprice) {
		this.inattaxprice = inattaxprice;
	}

	public Double getiCost() {
		return iCost;
	}

	public void setiCost(Double iCost) {
		this.iCost = iCost;
	}

	public Double getiMoney() {
		return iMoney;
	}

	public void setiMoney(Double iMoney) {
		this.iMoney = iMoney;
	}

	public Double getiTaxPrice() {
		return iTaxPrice;
	}

	public void setiTaxPrice(Double iTaxPrice) {
		this.iTaxPrice = iTaxPrice;
	}

	public Double getiSum() {
		return iSum;
	}

	public void setiSum(Double iSum) {
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
