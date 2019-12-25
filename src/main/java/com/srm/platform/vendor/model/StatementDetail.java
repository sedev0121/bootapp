package com.srm.platform.vendor.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "statement_detail")
public class StatementDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String code;
	
	@JsonProperty("row_no")
	private Integer rowNo;

	@Column(name = "pi_detail_id")
	Long piDetailId;

	@JsonProperty("adjust_tax_cost")
	private Double adjustTaxCost;

	@Column(name = "tax_rate")
	private Integer taxRate = 13;
	

	private Double price;
	private Double cost;
	private Double taxPrice;
	private Double taxCost;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Integer taxRate) {
		this.taxRate = taxRate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getRowNo() {
		return rowNo;
	}

	public void setRowNo(Integer rowNo) {
		this.rowNo = rowNo;
	}

	public Long getPiDetailId() {
		return piDetailId;
	}

	public void setPiDetailId(Long piDetailId) {
		this.piDetailId = piDetailId;
	}

	public Double getAdjustTaxCost() {
		return adjustTaxCost;
	}

	public void setAdjustTaxCost(Double adjustTaxCost) {
		this.adjustTaxCost = adjustTaxCost;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Double getTaxPrice() {
		return taxPrice;
	}

	public void setTaxPrice(Double taxPrice) {
		this.taxPrice = taxPrice;
	}

	public Double getTaxCost() {
		return taxCost;
	}

	public void setTaxCost(Double taxCost) {
		this.taxCost = taxCost;
	}
	
}
