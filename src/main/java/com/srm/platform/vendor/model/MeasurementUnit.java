package com.srm.platform.vendor.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "measurement_unit")
public class MeasurementUnit {
	@Id
	private String code;

	private String name;

	@Column(name = "group_code")
	private String groupCode;

	private int changerate;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name = "main_flag")
	private boolean mainFlag;

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

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public int getChangerate() {
		return changerate;
	}

	public void setChangerate(int changerate) {
		this.changerate = changerate;
	}

	public boolean isMainFlag() {
		return mainFlag;
	}

	public void setMainFlag(boolean mainFlag) {
		this.mainFlag = mainFlag;
	}

}
