package com.srm.platform.vendor.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "venpriceadjust_detail")
public class VenPriceAdjustDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@JsonIgnore
	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "mainid", referencedColumnName = "ccode")
	VenPriceAdjustMain main;

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "cinvcode", referencedColumnName = "code")
	Inventory inventory;

	Long iunitprice;

	Integer itaxrate;

	Integer itaxunitprice;

	Float fminquantity;

	Float fmaxquantity;

	Date dstartdate;
	Date denddate;

	Integer ivalid;
	String cbodymemo;

	Long ipriceid;

	public VenPriceAdjustDetail() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public VenPriceAdjustMain getMain() {
		return main;
	}

	public void setMain(VenPriceAdjustMain main) {
		this.main = main;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public Long getIunitprice() {
		return iunitprice;
	}

	public void setIunitprice(Long iunitprice) {
		this.iunitprice = iunitprice;
	}

	public Integer getItaxrate() {
		return itaxrate;
	}

	public void setItaxrate(Integer itaxrate) {
		this.itaxrate = itaxrate;
	}

	public Integer getItaxunitprice() {
		return itaxunitprice;
	}

	public void setItaxunitprice(Integer itaxunitprice) {
		this.itaxunitprice = itaxunitprice;
	}

	public Float getFminquantity() {
		return fminquantity;
	}

	public void setFminquantity(Float fminquantity) {
		this.fminquantity = fminquantity;
	}

	public Float getFmaxquantity() {
		return fmaxquantity;
	}

	public void setFmaxquantity(Float fmaxquantity) {
		this.fmaxquantity = fmaxquantity;
	}

	public Date getDstartdate() {
		return dstartdate;
	}

	public void setDstartdate(Date dstartdate) {
		this.dstartdate = dstartdate;
	}

	public Date getDenddate() {
		return denddate;
	}

	public void setDenddate(Date denddate) {
		this.denddate = denddate;
	}

	public Integer getIvalid() {
		return ivalid;
	}

	public void setIvalid(Integer ivalid) {
		this.ivalid = ivalid;
	}

	public String getCbodymemo() {
		return cbodymemo;
	}

	public void setCbodymemo(String cbodymemo) {
		this.cbodymemo = cbodymemo;
	}

	public Long getIpriceid() {
		return ipriceid;
	}

	public void setIpriceid(Long ipriceid) {
		this.ipriceid = ipriceid;
	}

}
