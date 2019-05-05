package com.srm.platform.vendor.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "store")
public class Store {
	@Id
	private Long id;

	private String name;
	private String code;

	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "company_id")
	@ManyToOne()
	private Company company;
	
	private String address;
	
	@JsonProperty("is_accept_set")
	private Integer isAcceptSet;
	
	@JsonProperty("is_use_in_srm")
	private Integer isUseInSrm;
	
	private String memo;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getIsAcceptSet() {
		return isAcceptSet;
	}

	public void setIsAcceptSet(Integer isAcceptSet) {
		this.isAcceptSet = isAcceptSet;
	}

	public Integer getIsUseInSrm() {
		return isUseInSrm;
	}

	public void setIsUseInSrm(Integer isUseInSrm) {
		this.isUseInSrm = isUseInSrm;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}
