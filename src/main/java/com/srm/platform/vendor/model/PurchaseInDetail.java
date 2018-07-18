package com.srm.platform.vendor.model;

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
import com.srm.platform.vendor.utility.PurchaseInDetailResult;

@Entity

@SqlResultSetMapping(name = "PurchaseInDetailResult", classes = {
		@ConstructorResult(targetClass = PurchaseInDetailResult.class, columns = {
				@ColumnResult(name = "id", type = String.class), @ColumnResult(name = "code", type = String.class),
				@ColumnResult(name = "date", type = String.class), @ColumnResult(name = "rowno", type = Integer.class),
				@ColumnResult(name = "inventoryname", type = String.class),
				@ColumnResult(name = "inventorycode", type = String.class),
				@ColumnResult(name = "specs", type = String.class),
				@ColumnResult(name = "unitname", type = String.class),
				@ColumnResult(name = "quantity", type = String.class),
				@ColumnResult(name = "price", type = String.class), @ColumnResult(name = "cost", type = String.class),
				@ColumnResult(name = "cmassunitname", type = String.class),
				@ColumnResult(name = "assitantunitname", type = String.class),
				@ColumnResult(name = "irate", type = String.class), @ColumnResult(name = "number", type = String.class),
				@ColumnResult(name = "state", type = String.class),
				@ColumnResult(name = "closed_quantity", type = String.class) }) })

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
	@JoinColumn(name = "inventorycode", referencedColumnName = "code")
	private Inventory inventory;

	private Integer rowno;
	private Float quantity;

	private Float price;
	private Float cost;
	private String cmassunitname;
	private String assitantunitname;
	private Float irate;
	private Float number;
	private Float confirmed_quantity;
	private Integer state;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Float getCost() {
		return cost;
	}

	public void setCost(Float cost) {
		this.cost = cost;
	}

	public String getCmassunitname() {
		return cmassunitname;
	}

	public void setCmassunitname(String cmassunitname) {
		this.cmassunitname = cmassunitname;
	}

	public String getAssitantunitname() {
		return assitantunitname;
	}

	public void setAssitantunitname(String assitantunitname) {
		this.assitantunitname = assitantunitname;
	}

	public Float getIrate() {
		return irate;
	}

	public void setIrate(Float irate) {
		this.irate = irate;
	}

	public Float getNumber() {
		return number;
	}

	public void setNumber(Float number) {
		this.number = number;
	}

	public Float getConfirmed_quantity() {
		return confirmed_quantity;
	}

	public void setConfirmed_quantity(Float confirmed_quantity) {
		this.confirmed_quantity = confirmed_quantity;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

}
