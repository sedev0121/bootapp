package com.srm.platform.vendor.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity

@Table(name = "purchase_order_detail")
public class PurchaseOrderDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "code", referencedColumnName = "code")
	private PurchaseOrderMain main;

	@Nullable
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "inventory_code", referencedColumnName = "code")
	private Inventory inventory;
	
	@JsonProperty(value = "unit_name")
	private String unitName;

	@JsonProperty(value = "inventory_name")
	private String inventoryName;

	@JsonProperty(value = "inventory_class_code")
	private String inventoryClassCode;

	@JsonProperty(value = "inventory_class_name")
	private String inventoryClassName;

	@JsonProperty(value = "row_no")
	private Integer rowNo;
	
	private Double quantity;

	private Double price;
	@JsonProperty(value = "tax_price")
	private Double taxPrice;

	private Double money;
	private Double sum;
	@JsonProperty(value = "nat_price")
	private Double natPrice;
	@JsonProperty(value = "nat_tax_price")
	private Double natTaxPrice;
	@JsonProperty(value = "nat_money")
	private Double natMoney;
	@JsonProperty(value = "nat_sum")
	private Double natSum;
	@JsonProperty(value = "prepay_money")
	private Double prepayMoney;
	@JsonProperty(value = "arrive_date")
	private Date arriveDate;


	@JsonProperty(value = "tax_rate")
	private Double taxRate;
	
	@JsonProperty(value = "backed_quantity")
	private Double backedQuantity;
	
	@JsonProperty(value = "arrived_quantity")
	private Double arrivedQuantity;
	
	@JsonProperty(value = "invoiced_quantity")
	private Double invoicedQuantity;
	
	@JsonProperty(value = "invoiced_money")
	private Double invoicedMoney;
	
	@JsonProperty(value = "closer_name")
	private String closerName;
	
	@JsonProperty(value = "close_date")
	private Date closeDate;
	
	private String memo;
	
	@JsonProperty(value = "confirmed_quantity")
	private Double confirmedQuantity;
	
	@JsonProperty(value = "confirmed_memo")
	private String confirmedMemo;
	
	@JsonProperty(value = "confirmed_date")
	private Date confirmedDate;

	public PurchaseOrderDetail() {

	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public Double getInvoicedMoney() {
		return invoicedMoney;
	}

	public void setInvoicedMoney(Double invoicedMoney) {
		this.invoicedMoney = invoicedMoney;
	}
	
	public Double getConfirmedQuantity() {
		return confirmedQuantity;
	}

	public void setConfirmedQuantity(Double confirmedQuantity) {
		this.confirmedQuantity = confirmedQuantity;
	}

	public String getConfirmedMemo() {
		return confirmedMemo;
	}

	public void setConfirmedMemo(String confirmedMemo) {
		this.confirmedMemo = confirmedMemo;
	}

	public Date getConfirmedDate() {
		return confirmedDate;
	}

	public void setConfirmedDate(Date confirmedDate) {
		this.confirmedDate = confirmedDate;
	}

	public Double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}

	public Double getBackedQuantity() {
		return backedQuantity;
	}

	public void setBackedQuantity(Double backedQuantity) {
		this.backedQuantity = backedQuantity;
	}

	public Double getArrivedQuantity() {
		return arrivedQuantity;
	}

	public void setArrivedQuantity(Double arrivedQuantity) {
		this.arrivedQuantity = arrivedQuantity;
	}

	public Double getInvoicedQuantity() {
		return invoicedQuantity;
	}

	public void setInvoicedQuantity(Double invoicedQuantity) {
		this.invoicedQuantity = invoicedQuantity;
	}

	public String getCloserName() {
		return closerName;
	}

	public void setCloserName(String closerName) {
		this.closerName = closerName;
	}

	public Date getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
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

	public PurchaseOrderMain getMain() {
		return main;
	}

	public void setMain(PurchaseOrderMain main) {
		this.main = main;
	}

	public Integer getRowNo() {
		return rowNo;
	}

	public void setRowNo(Integer rowNo) {
		this.rowNo = rowNo;
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

	public Double getTaxPrice() {
		return taxPrice;
	}

	public void setTaxPrice(Double taxPrice) {
		this.taxPrice = taxPrice;
	}

	public Double getNatPrice() {
		return natPrice;
	}

	public void setNatPrice(Double natPrice) {
		this.natPrice = natPrice;
	}

	public Double getNatTaxPrice() {
		return natTaxPrice;
	}

	public void setNatTaxPrice(Double natTaxPrice) {
		this.natTaxPrice = natTaxPrice;
	}

	public Double getNatMoney() {
		return natMoney;
	}

	public void setNatMoney(Double natMoney) {
		this.natMoney = natMoney;
	}

	public Double getNatSum() {
		return natSum;
	}

	public void setNatSum(Double natSum) {
		this.natSum = natSum;
	}

	public Double getPrepayMoney() {
		return prepayMoney;
	}

	public void setPrepayMoney(Double prepayMoney) {
		this.prepayMoney = prepayMoney;
	}

	public Date getArriveDate() {
		return arriveDate;
	}

	public void setArriveDate(Date arriveDate) {
		this.arriveDate = arriveDate;
	}

	public String getInventoryClassCode() {
		return inventoryClassCode;
	}

	public void setInventoryClassCode(String inventoryClassCode) {
		this.inventoryClassCode = inventoryClassCode;
	}

}
