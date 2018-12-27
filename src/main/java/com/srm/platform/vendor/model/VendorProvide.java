package com.srm.platform.vendor.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "vendor_provide")
public class VendorProvide implements Serializable {

	private static final long serialVersionUID = 5097604484563401362L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "provide_id")
	private Long provideId;

	@Column(name = "vendor_code")
	private String vendorCode;

	@Column(name = "unit_id")
	private Long unitId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public String getVendorCode() {
		return vendorCode;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	public Long getProvideId() {
		return provideId;
	}

	public void setProvideId(Long provideId) {
		this.provideId = provideId;
	}

	public String getUnitId() {
		return vendorCode;
	}

	public void setUnitId(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	public VendorProvide() {

	}

	public VendorProvide(Long provideId, String vendorCode, Long unitId) {
		this.provideId = provideId;
		this.vendorCode = vendorCode;
		this.unitId = unitId;
	}
}
