package com.srm.platform.vendor.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.srm.platform.vendor.searchitem.PurchaseInDetailResult;
import com.srm.platform.vendor.utility.Constants;

@Entity

@SqlResultSetMapping(name = "PurchaseInDetailResult", classes = {
		@ConstructorResult(targetClass = PurchaseInDetailResult.class, columns = {
				@ColumnResult(name = "id", type = String.class), 
				@ColumnResult(name = "code", type = String.class),
				@ColumnResult(name = "date", type = String.class),
				@ColumnResult(name = "verify_date", type = String.class),
				@ColumnResult(name = "row_no", type = Integer.class),
				@ColumnResult(name = "inventoryname", type = String.class),
				@ColumnResult(name = "inventory_code", type = String.class),
				@ColumnResult(name = "specs", type = String.class),
				@ColumnResult(name = "unitname", type = String.class),
				@ColumnResult(name = "quantity", type = String.class),
				@ColumnResult(name = "price", type = String.class), 
				@ColumnResult(name = "cost", type = String.class),
				@ColumnResult(name = "tax_price", type = String.class),
				@ColumnResult(name = "tax_rate", type = String.class),
				@ColumnResult(name = "tax_cost", type = String.class),
				@ColumnResult(name = "confirmed_memo", type = String.class),
				@ColumnResult(name = "vendorname", type = String.class),
				@ColumnResult(name = "vendorcode", type = String.class),
				@ColumnResult(name = "type", type = String.class),
				@ColumnResult(name = "bredvouch", type = String.class),
				@ColumnResult(name = "state", type = String.class),
				@ColumnResult(name = "po_code", type = String.class),
				@ColumnResult(name = "po_row_no", type = Integer.class),
				@ColumnResult(name = "delivery_code", type = String.class),
				@ColumnResult(name = "delivery_row_no", type = Integer.class),
				@ColumnResult(name = "delivered_quantity", type = String.class),
				@ColumnResult(name = "company_name", type = String.class),
				@ColumnResult(name = "store_name", type = String.class),
				@ColumnResult(name = "sync_date", type = Date.class),
			}) 
	})

@Table(name = "purchase_in_detail")
public class PurchaseInDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "code", referencedColumnName = "code")
	private PurchaseInMain main;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "inventory_code", referencedColumnName = "code")
	private Inventory inventory;

	@JsonProperty("row_no")
	private Integer rowNo;
	
	private Double quantity;

	private Double tax;
	private Double taxRate;

	private Double price;
	private Double cost;

	private Double taxPrice;
	private Double taxCost;
	
	private String poCode;
	private Integer poRowNo;
	
	private String deliveryCode;
	private Integer deliveryRowNo;
	
	private Long autoId;
	

	private Integer state = Constants.PURCHASE_IN_STATE_WAIT;
	
	@JsonProperty("sync_date")
	private Date syncDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getSyncDate() {
		return syncDate;
	}

	public void setSyncDate(Date syncDate) {
		this.syncDate = syncDate;
	}

	public String getPoCode() {
		return poCode;
	}

	public void setPoCode(String poCode) {
		this.poCode = poCode;
	}

	public Integer getPoRowNo() {
		return poRowNo;
	}

	public void setPoRowNo(Integer poRowNo) {
		this.poRowNo = poRowNo;
	}

	public String getDeliveryCode() {
		return deliveryCode;
	}

	public void setDeliveryCode(String deliveryCode) {
		this.deliveryCode = deliveryCode;
	}

	public Integer getDeliveryRowNo() {
		return deliveryRowNo;
	}

	public void setDeliveryRowNo(Integer deliveryRowNo) {
		this.deliveryRowNo = deliveryRowNo;
	}

	public Double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}

	public Double getTaxCost() {
		return taxCost;
	}

	public void setTaxCost(Double taxCost) {
		this.taxCost = taxCost;
	}

	public PurchaseInMain getMain() {
		return main;
	}

	public void setMain(PurchaseInMain main) {
		this.main = main;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
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

	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	public Double getTaxPrice() {
		return taxPrice;
	}

	public void setTaxPrice(Double taxPrice) {
		this.taxPrice = taxPrice;
	}	

	public Integer getRowNo() {
		return rowNo;
	}

	public void setRowNo(Integer rowNo) {
		this.rowNo = rowNo;
	}

	public Long getAutoId() {
		return autoId;
	}

	public void setAutoId(Long autoId) {
		this.autoId = autoId;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

}
