package com.srm.platform.vendor.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import com.srm.platform.vendor.searchitem.PurchaseOrderDetailSearchResult;

@Entity

@SqlResultSetMapping(name = "PurchaseOrderDetailSearchResult", classes = {
		@ConstructorResult(targetClass = PurchaseOrderDetailSearchResult.class, columns = {
				@ColumnResult(name = "id", type = Long.class), 
				@ColumnResult(name = "rowno", type = Integer.class),
				@ColumnResult(name = "code"), 
				@ColumnResult(name = "quantity", type = Double.class),
				@ColumnResult(name = "shipped_quantity", type = Double.class), 
				@ColumnResult(name = "inventory_name"),
				@ColumnResult(name = "inventory_code"), 
				@ColumnResult(name = "vendorname"),
				@ColumnResult(name = "vendorcode"), 
				@ColumnResult(name = "specs"), 
				@ColumnResult(name = "unit_name"),
				@ColumnResult(name = "arrivedate", type = Date.class),
				@ColumnResult(name = "last_ship_date", type = Date.class),
				@ColumnResult(name = "confirmdate", type = Date.class), 
				@ColumnResult(name = "arrivenote"),
				@ColumnResult(name = "confirmnote"), 
				@ColumnResult(name = "remain_quantity", type = Double.class) 
				}) })

@Table(name = "purchase_order_detail")
public class PurchaseOrderDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "code", referencedColumnName = "code")
	private PurchaseOrderMain main;

	@JsonProperty(value = "unit_name")
	private String unitName;
	
	@JsonProperty(value = "inventory_code")
	private String inventoryCode;
	
	@JsonProperty(value = "inventory_name")
	private String inventoryName;	
	
	@JsonProperty(value = "inventory_class_code")
	private String inventoryClassCode;
	
	@JsonProperty(value = "inventory_class_name")
	private String inventoryClassName;

	private Integer rowno;
	private Double quantity;

	@Column(name = "shipped_quantity")
	private Double shippedQuantity;
	private Double price;
	private Double taxprice;
	private Double tax;
	private Double money;
	private Double sum;
	private Double natprice;
	private Double nattaxprice;
	private Double natmoney;
	private Double natsum;
	private Double prepaymoney;
	private Date arrivedate;
	private Date confirmdate;
	private String arrivenote;
	private String confirmnote;
	private Double confirmquantity;
	private Date lastShipDate;

	public PurchaseOrderDetail() {

	}

	public String getInventoryCode() {
		return inventoryCode;
	}

	public void setInventoryCode(String inventoryCode) {
		this.inventoryCode = inventoryCode;
	}

	public String getInventoryName() {
		return inventoryName;
	}

	public void setInventoryName(String inventoryName) {
		this.inventoryName = inventoryName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getInventoryClassName() {
		return inventoryClassName;
	}

	public void setInventoryClassName(String inventoryClassName) {
		this.inventoryClassName = inventoryClassName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getLastShipDate() {
		return lastShipDate;
	}

	public void setLastShipDate(Date lastShipDate) {
		this.lastShipDate = lastShipDate;
	}

	public Double getConfirmquantity() {
		return confirmquantity;
	}

	public void setConfirmquantity(Double confirmquantity) {
		this.confirmquantity = confirmquantity;
	}

	public PurchaseOrderMain getMain() {
		return main;
	}

	public void setMain(PurchaseOrderMain main) {
		this.main = main;
	}

	public Integer getRowno() {
		return rowno;
	}

	public void setRowno(Integer rowno) {
		this.rowno = rowno;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getShippedQuantity() {
		return shippedQuantity;
	}

	public void setShippedQuantity(Double shippedQuantity) {
		this.shippedQuantity = shippedQuantity;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	public Double getTaxprice() {
		return taxprice;
	}

	public void setTaxprice(Double taxprice) {
		this.taxprice = taxprice;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Double getSum() {
		return sum;
	}

	public void setSum(Double sum) {
		this.sum = sum;
	}

	public Double getNatprice() {
		return natprice;
	}

	public void setNatprice(Double natprice) {
		this.natprice = natprice;
	}

	public Double getNattaxprice() {
		return nattaxprice;
	}

	public void setNattaxprice(Double nattaxprice) {
		this.nattaxprice = nattaxprice;
	}

	public Double getNatmoney() {
		return natmoney;
	}

	public void setNatmoney(Double natmoney) {
		this.natmoney = natmoney;
	}

	public Double getNatsum() {
		return natsum;
	}

	public void setNatsum(Double natsum) {
		this.natsum = natsum;
	}

	public Double getPrepaymoney() {
		return prepaymoney;
	}

	public void setPrepaymoney(Double prepaymoney) {
		this.prepaymoney = prepaymoney;
	}

	public Date getConfirmdate() {
		return confirmdate;
	}

	public void setConfirmdate(Date confirmdate) {
		this.confirmdate = confirmdate;
	}

	public Date getArrivedate() {
		return arrivedate;
	}

	public void setArrivedate(Date arrivedate) {
		this.arrivedate = arrivedate;
	}

	public String getArrivenote() {
		return arrivenote;
	}

	public void setArrivenote(String arrivenote) {
		this.arrivenote = arrivenote;
	}

	public String getConfirmnote() {
		return confirmnote;
	}

	public void setConfirmnote(String confirmnote) {
		this.confirmnote = confirmnote;
	}

	public String getInventoryClassCode() {
		return inventoryClassCode;
	}

	public void setInventoryClassCode(String inventoryClassCode) {
		this.inventoryClassCode = inventoryClassCode;
	}

}
