package com.srm.platform.vendor.u8api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "u8api")
public class AppProperties {
	private String from_account;

	private String app_key;
	private String system_token;
	private String vendor_get;
	private int error_code_success;

	public int getError_code_success() {
		return error_code_success;
	}

	public void setError_code_success(int error_code_success) {
		this.error_code_success = error_code_success;
	}

	public String getSystem_token() {
		return system_token;
	}

	public void setSystem_token(String system_token) {
		this.system_token = system_token;
	}

	public String getVendor_get() {
		return vendor_get;
	}

	public void setVendor_get(String vendor_get) {
		this.vendor_get = vendor_get;
	}

	public String getApp_key() {
		return app_key;
	}

	public void setApp_key(String app_key) {
		this.app_key = app_key;
	}

	public String getFrom_account() {
		return from_account;
	}

	public void setFrom_account(String from_account) {
		this.from_account = from_account;
	}

}
