package com.srm.platform.vendor.saveform;

import java.util.List;

public class VendorSaveForm {

	private String code;
	private Integer state;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	private List<Long> provideclasses;


	public List<Long> getProvideclasses() {
		return provideclasses;
	}

	public void setProvideclasses(List<Long> provideclasses) {
		this.provideclasses = provideclasses;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}



	
}
