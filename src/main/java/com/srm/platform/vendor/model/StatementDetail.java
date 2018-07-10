package com.srm.platform.vendor.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
	private Float closedPrice;

	@Column(name = "closed_money")
	private Float closedMoney;

	@Column(name = "closed_tax_price")
	private Float closedTaxPrice;

	@Column(name = "closed_tax_money")
	private Float closedTaxMoney;

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "purchase_in_detail_id", referencedColumnName = "id")
	PurchaseInDetail purcaseInDetail;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Float getClosedQuantity() {
		return closedQuantity;
	}

	public void setClosedQuantity(Float closedQuantity) {
		this.closedQuantity = closedQuantity;
	}

	public Float getClosedPrice() {
		return closedPrice;
	}

	public void setClosedPrice(Float closedPrice) {
		this.closedPrice = closedPrice;
	}

	public Float getClosedMoney() {
		return closedMoney;
	}

	public void setClosedMoney(Float closedMoney) {
		this.closedMoney = closedMoney;
	}

	public Float getClosedTaxPrice() {
		return closedTaxPrice;
	}

	public void setClosedTaxPrice(Float closedTaxPrice) {
		this.closedTaxPrice = closedTaxPrice;
	}

	public Float getClosedTaxMoney() {
		return closedTaxMoney;
	}

	public void setClosedTaxMoney(Float closedTaxMoney) {
		this.closedTaxMoney = closedTaxMoney;
	}

	public PurchaseInDetail getPurcaseInDetail() {
		return purcaseInDetail;
	}

	public void setPurcaseInDetail(PurchaseInDetail purcaseInDetail) {
		this.purcaseInDetail = purcaseInDetail;
	}

}
