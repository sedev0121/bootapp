package com.srm.platform.vendor.utility;

import java.io.Serializable;
import java.util.Date;

public class MessageSearchResult implements Serializable {

	private static final long serialVersionUID = 6043698744616771619L;

	String id;

	String title;

	String content;

	Date create_date;

	public MessageSearchResult(String id, String title, String content, Date create_date) {

		this.id = id;
		this.title = title;
		this.content = content;
		this.create_date = create_date;

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

	public Date getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

}
