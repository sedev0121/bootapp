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
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.srm.platform.vendor.searchitem.ContractDetailSearchResult;
import com.srm.platform.vendor.searchitem.PurchaseOrderSearchResult;

@Entity


@SqlResultSetMapping(name = "ContractDetailSearchResult", classes = {
	@ConstructorResult(targetClass = ContractDetailSearchResult.class, columns = {
		@ColumnResult(name = "id", type = Long.class), 
		@ColumnResult(name = "row_no", type = Integer.class), 
		@ColumnResult(name = "code", type = String.class),
		@ColumnResult(name = "name", type = String.class),
		@ColumnResult(name = "specs", type = String.class),
		@ColumnResult(name = "main_measure", type = String.class),
		@ColumnResult(name = "quantity", type = Double.class),
		@ColumnResult(name = "tax_price", type = Double.class),
		@ColumnResult(name = "memo", type = String.class),
		@ColumnResult(name = "floating_direction", type = Integer.class),
		@ColumnResult(name = "floating_price", type = Double.class), 
	}) 
})
@Table(name = "contract_detail")
public class ContractDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String code;
	
	@JsonProperty("row_no")
	private Integer rowNo;

	@JsonProperty("floating_direction")
	private Integer floatingDirection;
	
	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "inventory_code", referencedColumnName = "code")
	private Inventory inventory;
	
	private Double quantity;

	@JsonProperty("tax_price")
	private Double taxPrice;
	
	@JsonProperty("floating_price")
	private Double floatingPrice;

	private String memo;

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

	public Double getFloatingPrice() {
		return floatingPrice;
	}

	public void setFloatingPrice(Double floatingPrice) {
		this.floatingPrice = floatingPrice;
	}

	public Integer getFloatingDirection() {
		return floatingDirection;
	}

	public void setFloatingDirection(Integer floatingDirection) {
		this.floatingDirection = floatingDirection;
	}

	public Integer getRowNo() {
		return rowNo;
	}

	public void setRowNo(Integer rowNo) {
		this.rowNo = rowNo;
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
	
}
