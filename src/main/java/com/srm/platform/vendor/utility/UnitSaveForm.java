package com.srm.platform.vendor.utility;

import java.util.List;

public class UnitSaveForm {

	private Long id;
	private String name;

	private List<Long> provideclasses;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Long> getProvideclasses() {
		return provideclasses;
	}

	public void setProvideclasses(List<Long> provideclasses) {
		this.provideclasses = provideclasses;
	}



	
}
