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
import com.srm.platform.vendor.utility.PurchaseOrderDetailSearchResult;

@Entity

@SqlResultSetMapping(name = "PurchaseOrderDetailSearchResult", classes = {
		@ConstructorResult(targetClass = PurchaseOrderDetailSearchResult.class, columns = {
				@ColumnResult(name = "rowno", type = Integer.class), @ColumnResult(name = "code"),
				@ColumnResult(name = "quantity", type = Float.class),
				@ColumnResult(name = "shipped_quantity", type = Float.class), @ColumnResult(name = "inventoryname"),
				@ColumnResult(name = "inventorycode"), @ColumnResult(name = "vendorname"),
				@ColumnResult(name = "vendorcode"), @ColumnResult(name = "specs"), @ColumnResult(name = "unitname"),
				@ColumnResult(name = "arrivedate", type = Date.class),
				@ColumnResult(name = "confirmdate", type = Date.class), @ColumnResult(name = "arrivenote"),
				@ColumnResult(name = "confirmnote"), @ColumnResult(name = "remain_quantity", type = Float.class) }) })

@Table(name = "purchase_order_detail")
public class PurchaseOrderDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "code", referencedColumnName = "code")
	private PurchaseOrderMain main;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "inventorycode", referencedColumnName = "code")
	private Inventory inventory;

	private Integer rowno;
	private Float quantity;

	@Column(name = "shipped_quantity")
	private Float shippedQuantity;
	private Float price;
	private Float taxprice;
	private Float tax;
	private Float money;
	private Float sum;
	private Float natprice;
	private Float nattaxprice;
	private Float natmoney;
	private Float natsum;
	private Float prepaymoney;
	private Date arrivedate;
	private Date confirmdate;
	private String arrivenote;
	private String confirmnote;
	private Float confirmquantity;

	public PurchaseOrderDetail() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Float getConfirmquantity() {
		return confirmquantity;
	}

	public void setConfirmquantity(Float confirmquantity) {
		this.confirmquantity = confirmquantity;
	}

	public PurchaseOrderMain getMain() {
		return main;
	}

	public void setMain(PurchaseOrderMain main) {
		this.main = main;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public Integer getRowno() {
		return rowno;
	}

	public void setRowno(Integer rowno) {
		this.rowno = rowno;
	}

	public Float getQuantity() {
		return quantity;
	}

	public void setQuantity(Float quantity) {
		this.quantity = quantity;
	}

	public Float getShippedQuantity() {
		return shippedQuantity;
	}

	public void setShippedQuantity(Float shippedQuantity) {
		this.shippedQuantity = shippedQuantity;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Float getTax() {
		return tax;
	}

	public void setTax(Float tax) {
		this.tax = tax;
	}

	public Float getTaxprice() {
		return taxprice;
	}

	public void setTaxprice(Float taxprice) {
		this.taxprice = taxprice;
	}

	public Float getMoney() {
		return money;
	}

	public void setMoney(Float money) {
		this.money = money;
	}

	public Float getSum() {
		return sum;
	}

	public void setSum(Float sum) {
		this.sum = sum;
	}

	public Float getNatprice() {
		return natprice;
	}

	public void setNatprice(Float natprice) {
		this.natprice = natprice;
	}

	public Float getNattaxprice() {
		return nattaxprice;
	}

	public void setNattaxprice(Float nattaxprice) {
		this.nattaxprice = nattaxprice;
	}

	public Float getNatmoney() {
		return natmoney;
	}

	public void setNatmoney(Float natmoney) {
		this.natmoney = natmoney;
	}

	public Float getNatsum() {
		return natsum;
	}

	public void setNatsum(Float natsum) {
		this.natsum = natsum;
	}

	public Float getPrepaymoney() {
		return prepaymoney;
	}

	public void setPrepaymoney(Float prepaymoney) {
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

}
