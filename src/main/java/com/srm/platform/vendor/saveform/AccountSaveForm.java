package com.srm.platform.vendor.saveform;

import java.util.List;
import java.util.Map;

public class AccountSaveForm {

	private Long id;
	private String password;
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
	private Long company;
	private String employee_no;
	
	private List<Map<String, String>> permission_scope_list;
	private List<Map<String, Long>> permission_group_ids;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public String getEmployee_no() {
		return employee_no;
	}

	public void setEmployee_no(String employee_no) {
		this.employee_no = employee_no;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public List<Map<String, String>> getPermission_scope_list() {
		return permission_scope_list;
	}

	public void setPermission_scope_list(List<Map<String, String>> permission_scope_list) {
		this.permission_scope_list = permission_scope_list;
	}

	public List<Map<String, Long>> getPermission_group_ids() {
		return permission_group_ids;
	}

	public void setPermission_group_ids(List<Map<String, Long>> permission_group_ids) {
		this.permission_group_ids = permission_group_ids;
	}

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}

}
