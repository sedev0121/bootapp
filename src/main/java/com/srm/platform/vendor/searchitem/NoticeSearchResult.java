package com.srm.platform.vendor.searchitem;

import java.io.Serializable;
import java.util.Date;

public class NoticeSearchResult implements Serializable {

	private static final long serialVersionUID = -7774864748081640759L;

	String id;

	String title;

	String content;

	Date create_date;

	String create_name;

	String create_unitname;

	Date verify_date;

	String verify_name;

	Integer state;

	Integer to_all_vendor;

	Integer to_unit_account;

	String attach_file_name;

	Date read_date;
	
	String url;

	public NoticeSearchResult(String id, String title, String content, Date create_date, String create_name,
			String create_unitname, Date verify_date, String verify_name, Integer state, Integer to_all_vendor,
			Integer to_unit_account, String attach_file_name, Date read_date, String url) {

		this.id = id;
		this.title = title;
		this.content = content;
		this.create_date = create_date;
		this.create_name = create_name;
		this.create_unitname = create_unitname;
		this.verify_date = verify_date;
		this.verify_name = verify_name;
		this.state = state;
		this.to_all_vendor = to_all_vendor;
		this.to_unit_account = to_unit_account;
		this.attach_file_name = attach_file_name;
		this.read_date = read_date;
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getRead_date() {
		return read_date;
	}

	public void setRead_date(Date read_date) {
		this.read_date = read_date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

	public String getCreate_name() {
		return create_name;
	}

	public void setCreate_name(String create_name) {
		this.create_name = create_name;
	}

	public String getCreate_unitname() {
		return create_unitname;
	}

	public void setCreate_unitname(String create_unitname) {
		this.create_unitname = create_unitname;
	}

	public Date getVerify_date() {
		return verify_date;
	}

	public void setVerify_date(Date verify_date) {
		this.verify_date = verify_date;
	}

	public String getVerify_name() {
		return verify_name;
	}

	public void setVerify_name(String verify_name) {
		this.verify_name = verify_name;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getTo_all_vendor() {
		return to_all_vendor;
	}

	public void setTo_all_vendor(Integer to_all_vendor) {
		this.to_all_vendor = to_all_vendor;
	}

	public Integer getTo_unit_account() {
		return to_unit_account;
	}

	public void setTo_unit_account(Integer to_unit_account) {
		this.to_unit_account = to_unit_account;
	}

	public String getAttach_file_name() {
		return attach_file_name;
	}

	public void setAttach_file_name(String attach_file_name) {
		this.attach_file_name = attach_file_name;
	}

}
