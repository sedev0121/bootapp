package com.srm.platform.vendor.saveform;

import java.util.List;

import com.srm.platform.vendor.utility.PermissionScopeRecord;

public class AccountSaveForm {

	private Long id;
	private String username;
	private String realname;
	private String unitname;
	private Long unit;
	private String mobile;
	private String tel;
	private String email;
	private String role;
	private String duty;
	private String vendor;
	private Integer state;

	private PermissionScopeRecord test;
//	private List<PermissionScopeRecord> permission_scope_list;
//	private List<Long> permissiongroupids;

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

	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

//	public List<Long> getPermissiongroupids() {
//		return permissiongroupids;
//	}
//
//	public void setPermissiongroupids(List<Long> permissiongroupids) {
//		this.permissiongroupids = permissiongroupids;
//	}
//	
//	
//	public List<PermissionScopeRecord> getPermission_scope_list() {
//		return permission_scope_list;
//	}
//
//	public void setPermission_scope_list(List<PermissionScopeRecord> permission_scope_list) {
//		this.permission_scope_list = permission_scope_list;
//	}
//
//	public String toString() {
//		return String.format("%s", this.permissiongroupids.toString());
//	}

	public PermissionScopeRecord getTest() {
		return test;
	}

	public void setTest(PermissionScopeRecord test) {
		this.test = test;
	}
	
	
}
