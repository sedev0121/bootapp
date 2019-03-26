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
import com.srm.platform.vendor.searchitem.PurchaseInDetailResult;
import com.srm.platform.vendor.utility.Constants;

@Entity

@SqlResultSetMapping(name = "PurchaseInDetailResult", classes = {
		@ConstructorResult(targetClass = PurchaseInDetailResult.class, columns = {
				@ColumnResult(name = "id", type = String.class), 
				@ColumnResult(name = "code", type = String.class),
				@ColumnResult(name = "date", type = String.class),
				@ColumnResult(name = "verify_date", type = String.class),
				@ColumnResult(name = "rowno", type = Integer.class),
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
				@ColumnResult(name = "memo", type = String.class),
				@ColumnResult(name = "nat_tax_price", type = String.class),
				@ColumnResult(name = "material_quantity", type = String.class),
				@ColumnResult(name = "material_tax_price", type = String.class),
				@ColumnResult(name = "vendorname", type = String.class),
				@ColumnResult(name = "vendorcode", type = String.class),
				@ColumnResult(name = "type", type = String.class),
				@ColumnResult(name = "bredvouch", type = String.class),
				@ColumnResult(name = "mainmemo", type = String.class),
				@ColumnResult(name = "state", type = String.class),
				@ColumnResult(name = "po_code", type = String.class),
				@ColumnResult(name = "nat_price", type = String.class),
				@ColumnResult(name = "nat_tax_rate", type = String.class) }) })

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
	private Double quantity;

	private Double price;
	private Double cost;
	private Double tax;

	private Double taxPrice;
	private Double taxRate;
	private Double taxCost;
	private String memo;

	private Double natPrice;
	private Double natCost;
	private Double natTaxRate;
	private Double natTax;
	private Double natTaxPrice;
	private Double natTaxCost;

	private String materialCode;
	private String materialName;
	private String materialUnitname;
	private Double materialQuantity;

	private Double materialPrice;
	private Double materialTaxPrice;

	private String poCode;
	private Long piDetailId;
	private Long poDetailId;

	private Integer state = Constants.PURCHASE_IN_STATE_WAIT;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPiDetailId() {
		return piDetailId;
	}

	public void setPiDetailId(Long piDetailId) {
		this.piDetailId = piDetailId;
	}

	public Long getPoDetailId() {
		return poDetailId;
	}

	public void setPoDetailId(Long poDetailId) {
		this.poDetailId = poDetailId;
	}

	public String getPoCode() {
		return poCode;
	}

	public void setPoCode(String poCode) {
		this.poCode = poCode;
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

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Double getNatPrice() {
		return natPrice;
	}

	public void setNatPrice(Double natPrice) {
		this.natPrice = natPrice;
	}

	public Double getNatCost() {
		return natCost;
	}

	public void setNatCost(Double natCost) {
		this.natCost = natCost;
	}

	public Double getNatTaxRate() {
		return natTaxRate;
	}

	public void setNatTaxRate(Double natTaxRate) {
		this.natTaxRate = natTaxRate;
	}

	public Double getNatTax() {
		return natTax;
	}

	public void setNatTax(Double natTax) {
		this.natTax = natTax;
	}

	public Double getNatTaxPrice() {
		return natTaxPrice;
	}

	public void setNatTaxPrice(Double natTaxPrice) {
		this.natTaxPrice = natTaxPrice;
	}

	public Double getNatTaxCost() {
		return natTaxCost;
	}

	public void setNatTaxCost(Double natTaxCost) {
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

	public Double getMaterialQuantity() {
		return materialQuantity;
	}

	public void setMaterialQuantity(Double materialQuantity) {
		this.materialQuantity = materialQuantity;
	}

	public Double getMaterialPrice() {
		return materialPrice;
	}

	public void setMaterialPrice(Double materialPrice) {
		this.materialPrice = materialPrice;
	}

	public Double getMaterialTaxPrice() {
		return materialTaxPrice;
	}

	public void setMaterialTaxPrice(Double materialTaxPrice) {
		this.materialTaxPrice = materialTaxPrice;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

}
