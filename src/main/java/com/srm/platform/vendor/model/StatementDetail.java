package com.srm.platform.vendor.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "statement_detail")
public class StatementDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String code;

	@Column(name = "closed_quantity")
	private Float closedQuantity;

	@Column(name = "closed_price")
	private Double closedPrice;

	@Column(name = "closed_money")
	private Double closedMoney;

	@Column(name = "closed_tax_price")
	private Double closedTaxPrice;

	@Column(name = "tax_rate")
	private Float taxRate;

	@Column(name = "closed_tax_money")
	private Double closedTaxMoney;

	@Column(name = "purchase_in_detail_id")
	Long purchaseInDetailId;

	private String memo;

	private Float unitWeight;

	private Float yinci;

	private Float yuanci;
	private Float realQuantity;
	private Integer purchaseinType;

	private Integer rowNo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getRowNo() {
		return rowNo;
	}

	public void setRowNo(Integer rowNo) {
		this.rowNo = rowNo;
	}

	public void setUnitWeight(Float unitWeight) {
		this.unitWeight = unitWeight;
	}

	public void setYinci(Float yinci) {
		this.yinci = yinci;
	}

	public void setYuanci(Float yuanci) {
		this.yuanci = yuanci;
	}

	public void setRealQuantity(Float realQuantity) {
		this.realQuantity = realQuantity;
	}

	public float getUnitWeight() {
		return unitWeight;
	}

	public void setUnitWeight(float unitWeight) {
		this.unitWeight = unitWeight;
	}

	public float getYinci() {
		return yinci;
	}

	public void setYinci(float yinci) {
		this.yinci = yinci;
	}

	public float getYuanci() {
		return yuanci;
	}

	public void setYuanci(float yuanci) {
		this.yuanci = yuanci;
	}

	public float getRealQuantity() {
		return realQuantity;
	}

	public void setRealQuantity(float realQuantity) {
		this.realQuantity = realQuantity;
	}

	public Integer getPurchaseinType() {
		return purchaseinType;
	}

	public void setPurchaseinType(Integer purchaseinType) {
		this.purchaseinType = purchaseinType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Float getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Float taxRate) {
		this.taxRate = taxRate;
	}

	public Float getClosedQuantity() {
		return closedQuantity;
	}

	public void setClosedQuantity(Float closedQuantity) {
		this.closedQuantity = closedQuantity;
	}

	public Double getClosedPrice() {
		return closedPrice;
	}

	public void setClosedPrice(Double closedPrice) {
		this.closedPrice = closedPrice;
	}

	public Double getClosedMoney() {
		return closedMoney;
	}

	public void setClosedMoney(Double closedMoney) {
		this.closedMoney = closedMoney;
	}

	public Double getClosedTaxPrice() {
		return closedTaxPrice;
	}

	public void setClosedTaxPrice(Double closedTaxPrice) {
		this.closedTaxPrice = closedTaxPrice;
	}

	public Double getClosedTaxMoney() {
		return closedTaxMoney;
	}

	public void setClosedTaxMoney(Double closedTaxMoney) {
		this.closedTaxMoney = closedTaxMoney;
	}

	public Long getPurchaseInDetailId() {
		return purchaseInDetailId;
	}

	public void setPurchaseInDetailId(Long purchaseInDetailId) {
		this.purchaseInDetailId = purchaseInDetailId;
	}

}
