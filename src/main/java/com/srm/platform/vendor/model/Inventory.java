package com.srm.platform.vendor.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
	private float refSalePrice;

	@Column(name = "bottom_sale_price")
	private float bottomSalePrice;

	@Column(name = "start_date")
	private Date startDate;

	@Column(name = "end_date")
	private Date endDate;

	private Date modifyDate;

	@Column(name = "unitgroup_type")
	private float unitgroupType;

	@Column(name = "unitgroup_code")
	private String unitgroupCode;

	@Column(name = "saunitCode")
	private String saunit_code;

	@Column(name = "puunitCode")
	private String puunit_code;

	@Column(name = "stunitCode")
	private String stunit_code;

	@Column(name = "mainMeasure")
	private String main_measure;

	@Column(name = "puunitName")
	private String puunit_name;

	@Column(name = "puunitIchangrate")
	private float puunit_ichangrate;

	@Column(name = "saunitName")
	private String saunit_name;

	@Column(name = "saunit_ichangrate")
	private float saunitIchangrate;

	@Column(name = "stunit_name")
	private String stunitName;

	@Column(name = "stunit_ichangrate")
	private float stunitIchangrate;

	private String defwarehouse;

	private String defwarehousename;

	private String iSupplyType;

	private String drawtype;

	private float iimptaxrate;

	@Column(name = "tax_rate")
	private float tax_rate;

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
	public float getRefSalePrice() {
		return refSalePrice;
	}

	public void setRefSalePrice(float refSalePrice) {
		this.refSalePrice = refSalePrice;
	}

	@JsonProperty("bottom_sale_price")
	public float getBottomSalePrice() {
		return bottomSalePrice;
	}

	public void setBottomSalePrice(float bottomSalePrice) {
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
	public float getUnitgroupType() {
		return unitgroupType;
	}

	public void setUnitgroupType(float unitgroupType) {
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
	public String getSaunit_code() {
		return saunit_code;
	}

	public void setSaunit_code(String saunit_code) {
		this.saunit_code = saunit_code;
	}

	@JsonProperty("puunit_code")
	public String getPuunit_code() {
		return puunit_code;
	}

	public void setPuunit_code(String puunit_code) {
		this.puunit_code = puunit_code;
	}

	@JsonProperty("stunit_code")
	public String getStunit_code() {
		return stunit_code;
	}

	public void setStunit_code(String stunit_code) {
		this.stunit_code = stunit_code;
	}

	@JsonProperty("main_measure")
	public String getMain_measure() {
		return main_measure;
	}

	public void setMain_measure(String main_measure) {
		this.main_measure = main_measure;
	}

	@JsonProperty("puunit_name")
	public String getPuunit_name() {
		return puunit_name;
	}

	public void setPuunit_name(String puunit_name) {
		this.puunit_name = puunit_name;
	}

	public float getPuunit_ichangrate() {
		return puunit_ichangrate;
	}

	public void setPuunit_ichangrate(float puunit_ichangrate) {
		this.puunit_ichangrate = puunit_ichangrate;
	}

	public String getSaunit_name() {
		return saunit_name;
	}

	public void setSaunit_name(String saunit_name) {
		this.saunit_name = saunit_name;
	}

	@JsonProperty("saunit_ichangrate")
	public float getSaunitIchangrate() {
		return saunitIchangrate;
	}

	public void setSaunitIchangrate(float saunitIchangrate) {
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
	public float getStunitIchangrate() {
		return stunitIchangrate;
	}

	public void setStunitIchangrate(float stunitIchangrate) {
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
		return iSupplyType;
	}

	public void setiSupplyType(String iSupplyType) {
		this.iSupplyType = iSupplyType;
	}

	public String getDrawtype() {
		return drawtype;
	}

	public void setDrawtype(String drawtype) {
		this.drawtype = drawtype;
	}

	public float getIimptaxrate() {
		return iimptaxrate;
	}

	public void setIimptaxrate(float iimptaxrate) {
		this.iimptaxrate = iimptaxrate;
	}

	@JsonProperty("tax_rate")
	public float getTax_rate() {
		return tax_rate;
	}

	public void setTax_rate(float tax_rate) {
		this.tax_rate = tax_rate;
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
