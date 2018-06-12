package com.srm.platform.vendor.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "account")
public class Account {

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private String username;

	private String password;

	private String role = "ROLE_USER";

	private String to_account;
	private String token_id;

	private long expire_time;

	private String real_name;

	private String address;
	private Instant create_time;
	private Instant update_time;

	private String email;
	private String tel;
	private String qq;
	private Instant entry_time;
	private String skype;
	private String yahoo;
	private String gtalk;
	private String wangwang;
	private String mobile;

	public String getReal_name() {
		return real_name;
	}

	public void setReal_name(String real_name) {
		this.real_name = real_name;
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

	public Instant getEntry_time() {
		return entry_time;
	}

	public void setEntry_time(Instant entry_time) {
		this.entry_time = entry_time;
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

	public Instant getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Instant create_time) {
		this.create_time = create_time;
	}

	public Instant getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Instant update_time) {
		this.update_time = update_time;
	}

	protected Account() {

	}

	public Account(String username, String password, String role) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.create_time = Instant.now();
		this.update_time = Instant.now();
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

	public String getTo_account() {
		return to_account;
	}

	public void setTo_account(String to_account) {
		this.to_account = to_account;
	}

	public String getToken_id() {
		return token_id;
	}

	public void setToken_id(String token_id) {
		this.token_id = token_id;
	}

	public long getExpire_time() {
		return expire_time;
	}

	public void setExpire_time(long expire_time) {
		this.expire_time = expire_time;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
