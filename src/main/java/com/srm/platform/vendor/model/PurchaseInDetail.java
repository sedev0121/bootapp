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
				@ColumnResult(name = "inventory_code", type = String.class),
				@ColumnResult(name = "specs", type = String.class),
				@ColumnResult(name = "unitname", type = String.class),
				@ColumnResult(name = "quantity", type = String.class),
				@ColumnResult(name = "price", type = String.class), @ColumnResult(name = "cost", type = String.class),
				@ColumnResult(name = "closed_quantity", type = String.class),
				@ColumnResult(name = "remain_quantity", type = String.class),
				@ColumnResult(name = "tax_price", type = String.class),
				@ColumnResult(name = "tax_rate", type = String.class),
				@ColumnResult(name = "tax_cost", type = String.class),
				@ColumnResult(name = "memo", type = String.class),
				@ColumnResult(name = "nat_tax_price", type = String.class),
				@ColumnResult(name = "material_quantity", type = String.class),
				@ColumnResult(name = "material_tax_price", type = String.class) }) })

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

	private Integer rowno;
	private Float quantity;

	private Float price;
	private Float cost;
	private Float tax;

	private Float taxPrice;
	private Float taxRate;
	private Float taxCost;
	private String memo;

	private Float natPrice;
	private Float natCost;
	private Float natTaxRate;
	private Float natTax;
	private Float natTaxPrice;
	private Float natTaxCost;

	private String materialCode;
	private String materialName;
	private String materialUnitname;
	private Float materialQuantity;

	private Float materialPrice;
	private Float materialTaxPrice;

	private Float confirmedQuantity;
	private Integer state;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Float getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Float taxRate) {
		this.taxRate = taxRate;
	}

	public Float getTaxCost() {
		return taxCost;
	}

	public void setTaxCost(Float taxCost) {
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

	public Float getTax() {
		return tax;
	}

	public void setTax(Float tax) {
		this.tax = tax;
	}

	public Float getTaxPrice() {
		return taxPrice;
	}

	public void setTaxPrice(Float taxPrice) {
		this.taxPrice = taxPrice;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Float getNatPrice() {
		return natPrice;
	}

	public void setNatPrice(Float natPrice) {
		this.natPrice = natPrice;
	}

	public Float getNatCost() {
		return natCost;
	}

	public void setNatCost(Float natCost) {
		this.natCost = natCost;
	}

	public Float getNatTaxRate() {
		return natTaxRate;
	}

	public void setNatTaxRate(Float natTaxRate) {
		this.natTaxRate = natTaxRate;
	}

	public Float getNatTax() {
		return natTax;
	}

	public void setNatTax(Float natTax) {
		this.natTax = natTax;
	}

	public Float getNatTaxPrice() {
		return natTaxPrice;
	}

	public void setNatTaxPrice(Float natTaxPrice) {
		this.natTaxPrice = natTaxPrice;
	}

	public Float getNatTaxCost() {
		return natTaxCost;
	}

	public void setNatTaxCost(Float natTaxCost) {
		this.natTaxCost = natTaxCost;
	}

	public String getMaterialCode() {
		return materialCode;
	}

	public void setMaterialCode(String materialCode) {
		this.materialCode = materialCode;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getMaterialUnitname() {
		return materialUnitname;
	}

	public void setMaterialUnitname(String materialUnitname) {
		this.materialUnitname = materialUnitname;
	}

	public Float getMaterialQuantity() {
		return materialQuantity;
	}

	public void setMaterialQuantity(Float materialQuantity) {
		this.materialQuantity = materialQuantity;
	}

	public Float getMaterialPrice() {
		return materialPrice;
	}

	public void setMaterialPrice(Float materialPrice) {
		this.materialPrice = materialPrice;
	}

	public Float getMaterialTaxPrice() {
		return materialTaxPrice;
	}

	public void setMaterialTaxPrice(Float materialTaxPrice) {
		this.materialTaxPrice = materialTaxPrice;
	}

	public Float getConfirmedQuantity() {
		return confirmedQuantity;
	}

	public void setConfirmedQuantity(Float confirmedQuantity) {
		this.confirmedQuantity = confirmedQuantity;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

}
