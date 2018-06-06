package com.srm.platform.vendor.u8api;

public class Token {

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	private String id;
	private int expiresIn;
	private String appKey;
}
