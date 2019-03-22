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
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.srm.platform.vendor.searchitem.PriceSearchResult;

@Entity

@SqlResultSetMapping(name = "PriceSearchResult", classes = {
		@ConstructorResult(targetClass = PriceSearchResult.class, columns = {
				@ColumnResult(name = "cinvdate", type = String.class),
				@ColumnResult(name = "createdate", type = String.class),
				@ColumnResult(name = "description", type = String.class),
				@ColumnResult(name = "faddqty", type = String.class),
				@ColumnResult(name = "fauxunit", type = String.class),
				@ColumnResult(name = "vendorname", type = String.class),
				@ColumnResult(name = "vendorcode", type = String.class),
				@ColumnResult(name = "inventoryname", type = String.class),
				@ColumnResult(name = "inventorycode", type = String.class),
				@ColumnResult(name = "favdate", type = String.class),
				@ColumnResult(name = "fcanceldate", type = String.class),
				@ColumnResult(name = "fcancelno", type = String.class),
				@ColumnResult(name = "fprice", type = String.class), @ColumnResult(name = "ftax", type = String.class),
				@ColumnResult(name = "ftaxprice", type = String.class),
				@ColumnResult(name = "foldcheckdate", type = String.class),
				@ColumnResult(name = "fisoutside", type = String.class),
				@ColumnResult(name = "createname", type = String.class),
				@ColumnResult(name = "fnote", type = String.class) }) })

@Table(name = "price")
public class Price {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "fsupplyno", referencedColumnName = "code")

	private Vendor vendor;

	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "cinvcode", referencedColumnName = "code")
	private Inventory inventory;

	private String description;
	private String fauxunit;
	private String fnote;
	private Boolean fisoutside;

	private Float fprice;
	private Float ftax;
	private Float ftaxprice;
	private Float faddqty;

	private Long funitid;
	private Long fcancelno;
	private Long createby;

	private Date cinvdate;
	private Date foldcheckdate;
	private Date fcanceldate;
	private Date createdate;
	private Date fcheckdate;
	private Date foldavdate;
	private Date favdate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	@JsonProperty("cinvcode")
	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFauxunit() {
		return fauxunit;
	}

	public void setFauxunit(String fauxunit) {
		this.fauxunit = fauxunit;
	}

	public String getFnote() {
		return fnote;
	}

	public void setFnote(String fnote) {
		this.fnote = fnote;
	}

	public Boolean getFisoutside() {
		return fisoutside;
	}

	public void setFisoutside(Boolean fisoutside) {
		this.fisoutside = fisoutside;
	}

	public Float getFprice() {
		return fprice;
	}

	public void setFprice(Float fprice) {
		this.fprice = fprice;
	}

	public Float getFtax() {
		return ftax;
	}

	public void setFtax(Float ftax) {
		this.ftax = ftax;
	}

	public Float getFtaxprice() {
		return ftaxprice;
	}

	public void setFtaxprice(Float ftaxprice) {
		this.ftaxprice = ftaxprice;
	}

	public Float getFaddqty() {
		return faddqty;
	}

	public void setFaddqty(Float faddqty) {
		this.faddqty = faddqty;
	}

	public Long getFunitid() {
		return funitid;
	}

	public void setFunitid(Long funitid) {
		this.funitid = funitid;
	}

	public Long getFcancelno() {
		return fcancelno;
	}

	public void setFcancelno(Long fcancelno) {
		this.fcancelno = fcancelno;
	}

	public Long getCreateby() {
		return createby;
	}

	public void setCreateby(Long createby) {
		this.createby = createby;
	}

	public Date getcInvDate() {
		return cinvdate;
	}

	@JsonProperty("cinvdate")
	public void setcInvDate(Date cInvDate) {
		this.cinvdate = cInvDate;
	}

	public Date getFoldcheckdate() {
		return foldcheckdate;
	}

	public void setFoldcheckdate(Date foldcheckdate) {
		this.foldcheckdate = foldcheckdate;
	}

	public Date getFcanceldate() {
		return fcanceldate;
	}

	public void setFcanceldate(Date fcanceldate) {
		this.fcanceldate = fcanceldate;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public Date getFcheckdate() {
		return fcheckdate;
	}

	public void setFcheckdate(Date fcheckdate) {
		this.fcheckdate = fcheckdate;
	}

	public Date getFoldavdate() {
		return foldavdate;
	}

	public void setFoldavdate(Date foldavdate) {
		this.foldavdate = foldavdate;
	}

	public Date getFavdate() {
		return favdate;
	}

	public void setFavdate(Date favdate) {
		this.favdate = favdate;
	}

}
