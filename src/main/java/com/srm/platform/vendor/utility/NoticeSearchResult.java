package com.srm.platform.vendor.utility;

import java.io.Serializable;

public class NoticeSearchResult implements Serializable {

	private static final long serialVersionUID = -7774864748081640759L;

	String id;

	String title;

	String content;

	String create_date;

	String create_name;

	String create_unitname;

	public NoticeSearchResult(String id, String title, String content, String create_date, String create_name,
			String create_unitname) {

		this.id = id;
		this.title = title;
		this.content = content;
		this.create_date = create_date;
		this.create_name = create_name;
		this.create_unitname = create_unitname;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
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

}
