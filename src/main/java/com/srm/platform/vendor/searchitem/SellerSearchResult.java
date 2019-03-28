package com.srm.platform.vendor.searchitem;

import java.io.Serializable;

public class SellerSearchResult implements Serializable {

	private static final long serialVersionUID = -7774864748081640759L;

	String id;
	String username;
	String name;
	String abbrname;
	String email;
	String phone;
	String mobile;
	String state;

	public SellerSearchResult(String id, String username, String name, String abbrname, String email, String phone,
			String mobile, String state) {

		this.id = id;
		this.username = username;
		this.name = name;
		this.abbrname = abbrname;
		this.email = email;
		this.phone = phone;
		this.mobile = mobile;
		this.state = state;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbbrname() {
		return abbrname;
	}

	public void setAbbrname(String abbrname) {
		this.abbrname = abbrname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
