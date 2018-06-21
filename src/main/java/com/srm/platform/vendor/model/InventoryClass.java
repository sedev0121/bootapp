package com.srm.platform.vendor.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "inventory_class")
public class InventoryClass {
	@Id
	private String code;

	private String name;

	private int rank;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name = "end_rank_flag")
	private boolean endRankFlag;

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

	public boolean isEndRankFlag() {
		return endRankFlag;
	}

	public void setEndRankFlag(boolean endRankFlag) {
		this.endRankFlag = endRankFlag;
	}

}
