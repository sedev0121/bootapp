package com.srm.platform.vendor.searchitem;

import java.io.Serializable;
import java.util.Date;

public class ProvideClassSearchResult implements Serializable {

	private static final long serialVersionUID = -7774864748081640759L;

	String id;

	String code;

	String name;
	

	public ProvideClassSearchResult(String id, String code, String name) {

		this.id = id;
		this.code = code;
		this.name = name;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


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
	
	
}
