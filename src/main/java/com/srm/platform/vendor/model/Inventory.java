package com.srm.platform.vendor.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "inventory")
public class Inventory {
	@Id
	private String code;
	private String name;
	private String specs;

	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "sort_code", referencedColumnName = "code", nullable=true)
	@ManyToOne()
	private InventoryClass inventoryClass;

	@JsonProperty(value = "main_measure")
	private String mainMeasure;

	private String defwarehouse;
	private String defwarehousename;
	private Double iimptaxrate;
	private Date startDate;
	private Date endDate;
	private Date modifyDate;

	public Inventory() {

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

	public InventoryClass getInventoryClass() {
		return inventoryClass;
	}

	public void setInventoryClass(InventoryClass inventoryClass) {
		this.inventoryClass = inventoryClass;
	}

	public String getMainMeasure() {
		return mainMeasure;
	}

	public void setMainMeasure(String mainMeasure) {
		this.mainMeasure = mainMeasure;
	}

	public String getDefwarehouse() {
		return defwarehouse;
	}

	public void setDefwarehouse(String defwarehouse) {
		this.defwarehouse = defwarehouse;
	}

	public String getDefwarehousename() {
		return defwarehousename;
	}

	public void setDefwarehousename(String defwarehousename) {
		this.defwarehousename = defwarehousename;
	}

	public Double getIimptaxrate() {
		return iimptaxrate;
	}

	public void setIimptaxrate(Double iimptaxrate) {
		this.iimptaxrate = iimptaxrate;
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

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

}
