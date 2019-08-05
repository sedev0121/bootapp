package com.srm.platform.vendor.searchitem;

import java.io.Serializable;

public class ContractDetailSearchResult implements Serializable {

	private static final long serialVersionUID = -7476789702299949678L;

	Long id;
	Integer row_no;	
	String code;	
	String name;
	String specs;	
	String main_measure;	

	Double quantity;	
	Double tax_price;	
	String memo;	
	Integer floating_direction;	
	Double floating_price;

	public ContractDetailSearchResult(Long id, Integer row_no, String code, String name,  
			String specs, String main_measure, Double quantity, Double tax_price, String memo, Integer floating_direction, Double floating_price) {

		this.id = id;
		this.row_no = row_no;
		this.code = code;
		this.name = name;
		this.specs = specs;
		this.main_measure = main_measure;
		this.quantity = quantity;
		this.tax_price = tax_price;
		this.memo = memo;
		this.floating_direction = floating_direction;
		this.floating_price = floating_price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getRow_no() {
		return row_no;
	}

	public void setRow_no(Integer row_no) {
		this.row_no = row_no;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpecs() {
		return specs;
	}

	public void setSpecs(String specs) {
		this.specs = specs;
	}

	public String getMain_measure() {
		return main_measure;
	}

	public void setMain_measure(String main_measure) {
		this.main_measure = main_measure;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getTax_price() {
		return tax_price;
	}

	public void setTax_price(Double tax_price) {
		this.tax_price = tax_price;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getFloating_direction() {
		return floating_direction;
	}

	public void setFloating_direction(Integer floating_direction) {
		this.floating_direction = floating_direction;
	}

	public Double getFloating_price() {
		return floating_price;
	}

	public void setFloating_price(Double floating_price) {
		this.floating_price = floating_price;
	}
	
	
}
