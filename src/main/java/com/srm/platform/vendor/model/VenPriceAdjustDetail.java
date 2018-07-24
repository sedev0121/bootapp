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
	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "mainid", referencedColumnName = "ccode")
	VenPriceAdjustMain main;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "cinvcode", referencedColumnName = "code")
	Inventory inventory;

	Float iunitprice;

	Float itaxrate;

	Float itaxunitprice;

	Float fminquantity;

	Float fmaxquantity;

	Date dstartdate;
	Date denddate;

	Integer ivalid;

	Integer rowno;
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

	public Integer getRowno() {
		return rowno;
	}

	public void setRowno(Integer rowNo) {
		this.rowno = rowNo;
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

	public Float getIunitprice() {
		return iunitprice;
	}

	public void setIunitprice(Float iunitprice) {
		this.iunitprice = iunitprice;
	}

	public Float getItaxrate() {
		return itaxrate;
	}

	public void setItaxrate(Float itaxrate) {
		this.itaxrate = itaxrate;
	}

	public Float getItaxunitprice() {
		return itaxunitprice;
	}

	public void setItaxunitprice(Float itaxunitprice) {
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
