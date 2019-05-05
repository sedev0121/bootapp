package com.srm.platform.vendor.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.lang.Nullable;

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

	@Nullable
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "box_class_id", referencedColumnName = "id", nullable=true)
	@ManyToOne()
	private BoxClass boxClass;

	@JsonProperty(value = "main_measure")
	private String mainMeasure;

	private String defwarehouse;
	private String defwarehousename;
	private Double iimptaxrate;
	private Date startDate;
	private Date endDate;
	private Date modifyDate;
	
	@JsonProperty(value = "count_per_box")
	private Integer countPerBox;

	@JsonProperty(value = "is_asset")
	private Integer isAsset;
	
	@JsonProperty(value = "is_import")
	private Integer isImport;
	
	@JsonProperty(value = "is_purchase")
	private Integer isPurchase;
	
	@JsonProperty(value = "is_weiwai")
	private Integer isWeiwai;
	
	public Inventory() {

	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public BoxClass getBoxClass() {
		return boxClass;
	}

	public void setBoxClass(BoxClass boxClass) {
		this.boxClass = boxClass;
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

	public Integer getIsAsset() {
		return isAsset;
	}

	public void setIsAsset(Integer isAsset) {
		this.isAsset = isAsset;
	}

	public Integer getIsImport() {
		return isImport;
	}

	public void setIsImport(Integer isImport) {
		this.isImport = isImport;
	}

	public Integer getIsPurchase() {
		return isPurchase;
	}

	public void setIsPurchase(Integer isPurchase) {
		this.isPurchase = isPurchase;
	}

	public Integer getIsWeiwai() {
		return isWeiwai;
	}

	public void setIsWeiwai(Integer isWeiwai) {
		this.isWeiwai = isWeiwai;
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

	public Integer getCountPerBox() {
		return countPerBox;
	}

	public void setCountPerBox(Integer countPerBox) {
		this.countPerBox = countPerBox;
	}

}
