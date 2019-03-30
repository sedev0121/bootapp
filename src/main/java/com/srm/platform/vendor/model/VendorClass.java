package com.srm.platform.vendor.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vendor_class")
public class VendorClass {
	@Id
	private String code;
	private String name;
	private Integer rank;
	private Integer endRankFlag;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public Integer isEndRankFlag() {
		return endRankFlag;
	}

	public void setEndRankFlag(Integer endRankFlag) {
		this.endRankFlag = endRankFlag;
	}

}
