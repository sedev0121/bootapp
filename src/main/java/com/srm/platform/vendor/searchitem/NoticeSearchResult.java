package com.srm.platform.vendor.searchitem;

import java.io.Serializable;
import java.util.Date;

public class NoticeSearchResult implements Serializable {

	private static final long serialVersionUID = -7774864748081640759L;

	String id;

	String title;
	
	String class_name;

	String content;

	Date create_date;

	String create_name;

	Date verify_date;

	String verify_name;

	Integer state;

	String attach_file_name;

	Date read_date;
	
	String url;

	public NoticeSearchResult(String id, String title, String class_name, String content, Date create_date, String create_name,
			Date verify_date, String verify_name, Integer state, String attach_file_name, Date read_date, String url) {

		this.id = id;
		this.title = title;
		this.content = content;
		this.class_name = class_name;
		this.create_date = create_date;
		this.create_name = create_name;
		this.verify_date = verify_date;
		this.verify_name = verify_name;
		this.state = state;
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

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public Date getRead_date() {
		return read_date;
	}

	public void setRead_date(Date read_date) {
		this.read_date = read_date;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public String getAttach_file_name() {
		return attach_file_name;
	}

	public void setAttach_file_name(String attach_file_name) {
		this.attach_file_name = attach_file_name;
	}

}
