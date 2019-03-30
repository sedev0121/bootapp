package com.srm.platform.vendor.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "inventory")
public class Inventory {
	@Id
	private String code;
	private String name;
	private String specs;
	private String sortCode;
	private String mainMeasure;
	private String defwarehouse;
	private String defwarehousename;
	private Float iimptaxrate;
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

	public String getSortCode() {
		return sortCode;
	}

	public void setSortCode(String sortCode) {
		this.sortCode = sortCode;
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

	public Float getIimptaxrate() {
		return iimptaxrate;
	}

	public void setIimptaxrate(Float iimptaxrate) {
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
