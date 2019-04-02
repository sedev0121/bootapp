package com.srm.platform.vendor.searchitem;

import java.io.Serializable;

public class BoxSearchResult implements Serializable {

	private static final long serialVersionUID = -7774864748081640759L;

	String id;
	String code;
	String spec;
	String memo;
	String state;
	String used;

	public BoxSearchResult(String id, String code, String spec, String memo, String state, String used) {

		this.id = id;
		this.code = code;
		this.spec = spec;
		this.memo = memo;
		this.state = state;
		this.used = used;
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

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUsed() {
		return used;
	}

	public void setUsed(String used) {
		this.used = used;
	}

}
