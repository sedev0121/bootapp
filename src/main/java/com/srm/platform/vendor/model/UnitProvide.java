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
@Table(name = "unit_provide")
public class UnitProvide implements Serializable {

	private static final long serialVersionUID = 5097604481233401362L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "provide_id")
	private Long provideId;

	@Column(name = "unit_id")
	private Long unitId;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProvideId() {
		return provideId;
	}

	public void setProvideId(Long provideId) {
		this.provideId = provideId;
	}

	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	public UnitProvide() {

	}

	public UnitProvide(Long provideId, Long unitId) {
		this.provideId = provideId;
		this.unitId = unitId;
	}
}
