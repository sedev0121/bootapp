package com.srm.platform.vendor.form;

import javax.validation.constraints.NotNull;

import com.srm.platform.vendor.model.Account;

public class AccountForm {

	@NotNull

	private String username = "";

	@NotNull
	private String password;

	public String getUsername() {
		return username;
	}

	public void setEmail(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Account createAccount() {
		return new Account(getUsername(), getPassword(), "ROLE_BUYER");
	}
}
