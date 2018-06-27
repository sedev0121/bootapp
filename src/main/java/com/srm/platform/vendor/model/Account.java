package com.srm.platform.vendor.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "account")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String username;

	@JsonIgnore
	private String password;
	private String role = "ROLE_BUYER";
	private String realname;
	private String duty;

	private String address;
	@Column(name = "entry_time")
	private Instant entryTime;

	@JoinColumn(name = "unit_id")
	@ManyToOne()
	private Unit unit;

	@OneToOne()
	@JoinColumn(name = "vendor_code", referencedColumnName = "code")
	private Vendor vendor;

	private String email;
	private String tel;
	private String qq;
	private String skype;
	private String yahoo;
	private String gtalk;
	private String wangwang;
	private String mobile;
	@Column(name = "modify_time")
	private Instant modifyTime;

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getSkype() {
		return skype;
	}

	public void setSkype(String skype) {
		this.skype = skype;
	}

	public String getYahoo() {
		return yahoo;
	}

	public void setYahoo(String yahoo) {
		this.yahoo = yahoo;
	}

	public String getGtalk() {
		return gtalk;
	}

	public void setGtalk(String gtalk) {
		this.gtalk = gtalk;
	}

	public String getWangwang() {
		return wangwang;
	}

	public void setWangwang(String wangwang) {
		this.wangwang = wangwang;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public Account() {

	}

	public Account(String username, String password, String role) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.modifyTime = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public Instant getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(Instant entryTime) {
		this.entryTime = entryTime;
	}

	public Instant getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Instant modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRolename() {
		switch (this.role) {
		case "ROLE_BUYER":
			return "采购员";
		case "ROLE_VENDOR":
			return "供应商";
		case "ROLE_ADMIN":
			return "管理员";
		default:
			return "";
		}
	}

}
