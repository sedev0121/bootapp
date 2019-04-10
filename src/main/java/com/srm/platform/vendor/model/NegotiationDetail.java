package com.srm.platform.vendor.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.srm.platform.vendor.utility.Constants;

@Entity
@Table(name = "negotiation_detail")
public class NegotiationDetail implements Serializable {
	private static final long serialVersionUID = 5855332316773551036L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "code")
	@ManyToOne()
	private NegotiationMain main;

	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "inventory_code")
	@ManyToOne()
	private Inventory inventory;

	@JsonProperty("tax_price")
	private Double taxPrice;
	
	@JsonProperty("tax_rate")
	private Double taxRate;
	
	private Double price;
	
	@JsonProperty("max_quantity")
	private Double maxQuantity;
	
	private Integer valid = Constants.VALID_NO;
	
	private String memo;
	
	@JsonProperty("start_date")
	private Date startDate = new Date();

	@JsonProperty("end_date")
	private Date endDate;

	
	public NegotiationDetail() {
		
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public NegotiationMain getMain() {
		return main;
	}


	public void setMain(NegotiationMain main) {
		this.main = main;
	}


	public Inventory getInventory() {
		return inventory;
	}


	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}


	public Double getTaxPrice() {
		return taxPrice;
	}


	public void setTaxPrice(Double taxPrice) {
		this.taxPrice = taxPrice;
	}


	public Double getTaxRate() {
		return taxRate;
	}


	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}


	public Double getPrice() {
		return price;
	}


	public void setPrice(Double price) {
		this.price = price;
	}


	public Double getMaxQuantity() {
		return maxQuantity;
	}


	public void setMaxQuantity(Double maxQuantity) {
		this.maxQuantity = maxQuantity;
	}


	public Integer getValid() {
		return valid;
	}


	public void setValid(Integer valid) {
		this.valid = valid;
	}


	public String getMemo() {
		return memo;
	}


	public void setMemo(String memo) {
		this.memo = memo;
	}


	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Date getEndDate() {
		return endDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
