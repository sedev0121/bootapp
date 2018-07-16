package com.srm.platform.vendor.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "inventory")
public class Inventory {
	@Id
	private String code;

	private String invaddcode;
	private String name;
	private String specs;

	@Column(name = "ref_sale_price")
	private Float refSalePrice;

	@Column(name = "bottom_sale_price")
	private Float bottomSalePrice;

	@Column(name = "start_date")
	private Date startDate;

	@Column(name = "end_date")
	private Date endDate;

	private Date modifyDate;

	@Column(name = "unitgroup_type")
	private Integer unitgroupType;

	@Column(name = "unitgroup_code")
	private String unitgroupCode;

	@Column(name = "saunit_code")
	private String saunitCode;

	@Column(name = "puunit_code")
	private String puunitCode;

	@Column(name = "stunit_code")
	private String stunitCode;

	@OneToOne()
	@JoinColumn(name = "main_measure", referencedColumnName = "code")
	private MeasurementUnit mainMeasure;

	@Column(name = "puunit_name")
	private String puunitName;

	@Column(name = "puunit_ichangrate")
	private Float puunitIchangrate;

	@Column(name = "saunit_name")
	private String saunitName;

	@Column(name = "saunit_ichangrate")
	private Float saunitIchangrate;

	@Column(name = "stunit_name")
	private String stunitName;

	@Column(name = "stunit_ichangrate")
	private Float stunitIchangrate;

	private String defwarehouse;

	private String defwarehousename;

	private String isupplytype;

	private String drawtype;

	private Float iimptaxrate;

	@Column(name = "tax_rate")
	private Float taxRate;

	public Inventory() {

	}

	@Column(name = "self_define1")
	private String selfDefine1;

	@Column(name = "self_define2")
	private String selfDefine2;

	@Column(name = "self_define3")
	private String selfDefine3;

	@Column(name = "selfDefine4")
	private String selfDefine4;

	@Column(name = "selfDefine5")
	private String selfDefine5;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getInvaddcode() {
		return invaddcode;
	}

	public void setInvaddcode(String invaddcode) {
		this.invaddcode = invaddcode;
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

	@JsonProperty("ref_sale_price")
	public Float getRefSalePrice() {
		return refSalePrice;
	}

	public void setRefSalePrice(Float refSalePrice) {
		this.refSalePrice = refSalePrice;
	}

	@JsonProperty("bottom_sale_price")
	public Float getBottomSalePrice() {
		return bottomSalePrice;
	}

	public void setBottomSalePrice(Float bottomSalePrice) {
		this.bottomSalePrice = bottomSalePrice;
	}

	@JsonProperty("start_date")
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@JsonProperty("end_date")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@JsonProperty("modify_date")
	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	@JsonProperty("unit_group_type")
	public Integer getUnitgroupType() {
		return unitgroupType;
	}

	public void setUnitgroupType(Integer unitgroupType) {
		this.unitgroupType = unitgroupType;
	}

	@JsonProperty("unit_group_code")
	public String getUnitgroupCode() {
		return unitgroupCode;
	}

	public void setUnitgroupCode(String unitgroupCode) {
		this.unitgroupCode = unitgroupCode;
	}

	@JsonProperty("saunit_code")
	public String getSaunitCode() {
		return saunitCode;
	}

	public void setSaunitCode(String saunit_code) {
		this.saunitCode = saunit_code;
	}

	@JsonProperty("puunit_code")
	public String getPuunitCode() {
		return puunitCode;
	}

	public void setPuunitCode(String puunit_code) {
		this.puunitCode = puunit_code;
	}

	@JsonProperty("stunit_code")
	public String getStunitCode() {
		return stunitCode;
	}

	public void setStunitCode(String stunit_code) {
		this.stunitCode = stunit_code;
	}

	@JsonProperty("main_measure")
	public MeasurementUnit getMainMeasure() {
		return mainMeasure;
	}

	public void setMainMeasure(MeasurementUnit main_measure) {
		this.mainMeasure = main_measure;
	}

	@JsonProperty("puunit_name")
	public String getPuunitName() {
		return puunitName;
	}

	public void setPuunitName(String puunit_name) {
		this.puunitName = puunit_name;
	}

	public Float getPuunitIchangrate() {
		return puunitIchangrate;
	}

	public void setPuunit_ichangrate(Float puunit_ichangrate) {
		this.puunitIchangrate = puunit_ichangrate;
	}

	public String getSaunitName() {
		return saunitName;
	}

	public void setSaunitName(String saunit_name) {
		this.saunitName = saunit_name;
	}

	@JsonProperty("saunit_ichangrate")
	public Float getSaunitIchangrate() {
		return saunitIchangrate;
	}

	public void setSaunitIchangrate(Float saunitIchangrate) {
		this.saunitIchangrate = saunitIchangrate;
	}

	@JsonProperty("stunit_name")
	public String getStunitName() {
		return stunitName;
	}

	public void setStunitName(String stunitName) {
		this.stunitName = stunitName;
	}

	@JsonProperty("stunit_ichangrate")
	public Float getStunitIchangrate() {
		return stunitIchangrate;
	}

	public void setStunitIchangrate(Float stunitIchangrate) {
		this.stunitIchangrate = stunitIchangrate;
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

	public String getiSupplyType() {
		return isupplytype;
	}

	public void setiSupplyType(String iSupplyType) {
		this.isupplytype = iSupplyType;
	}

	public String getDrawtype() {
		return drawtype;
	}

	public void setDrawtype(String drawtype) {
		this.drawtype = drawtype;
	}

	public Float getIimptaxrate() {
		return iimptaxrate;
	}

	public void setIimptaxrate(Float iimptaxrate) {
		this.iimptaxrate = iimptaxrate;
	}

	@JsonProperty("tax_rate")
	public Float getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Float taxRate) {
		this.taxRate = taxRate;
	}

	public String getSelfDefine1() {
		return selfDefine1;
	}

	public void setSelfDefine1(String selfDefine1) {
		this.selfDefine1 = selfDefine1;
	}

	public String getSelfDefine2() {
		return selfDefine2;
	}

	public void setSelfDefine2(String selfDefine2) {
		this.selfDefine2 = selfDefine2;
	}

	public String getSelfDefine3() {
		return selfDefine3;
	}

	public void setSelfDefine3(String selfDefine3) {
		this.selfDefine3 = selfDefine3;
	}

	public String getSelfDefine4() {
		return selfDefine4;
	}

	public void setSelfDefine4(String selfDefine4) {
		this.selfDefine4 = selfDefine4;
	}

	public String getSelfDefine5() {
		return selfDefine5;
	}

	public void setSelfDefine5(String selfDefine5) {
		this.selfDefine5 = selfDefine5;
	}

	public String getSelfDefine6() {
		return selfDefine6;
	}

	public void setSelfDefine6(String selfDefine6) {
		this.selfDefine6 = selfDefine6;
	}

	public String getFree1() {
		return free1;
	}

	public void setFree1(String free1) {
		this.free1 = free1;
	}

	public String getFree2() {
		return free2;
	}

	public void setFree2(String free2) {
		this.free2 = free2;
	}

	public String getFree3() {
		return free3;
	}

	public void setFree3(String free3) {
		this.free3 = free3;
	}

	public String getFree4() {
		return free4;
	}

	public void setFree4(String free4) {
		this.free4 = free4;
	}

	public String getFree5() {
		return free5;
	}

	public void setFree5(String free5) {
		this.free5 = free5;
	}

	public String getFree6() {
		return free6;
	}

	public void setFree6(String free6) {
		this.free6 = free6;
	}

	@Column(name = "self_define6")
	private String selfDefine6;

	private String free1;
	private String free2;
	private String free3;
	private String free4;
	private String free5;
	private String free6;

}
