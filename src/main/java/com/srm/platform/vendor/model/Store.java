package com.srm.platform.vendor.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "store")
public class Store {
	@Id
	private Long id;

	private String name;

	private Long companyId;
	private String address;
	private Integer isAcceptSet;
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

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
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
