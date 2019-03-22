package com.srm.platform.vendor.saveform;

import java.util.List;
import java.util.Map;

import com.srm.platform.vendor.utility.PermissionRecord;

public class AccountSaveForm {

	private Long id;
	private String username;
	private String realname;
	private Long unit;
	private String mobile;
	private String tel;
	private String email;
	private String role;
	private String duty;
	private String vendor;
	private Integer state;

	private List<PermissionRecord> permissions;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public Long getUnit() {
		return unit;
	}

	public void setUnit(Long unit) {
		this.unit = unit;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public List<PermissionRecord> getPermission() {
		return permissions;
	}

	public void setPermission(List<PermissionRecord> permissions) {
		this.permissions = permissions;
	}

}
