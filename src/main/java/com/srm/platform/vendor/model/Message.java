package com.srm.platform.vendor.model;

import java.util.Date;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import com.srm.platform.vendor.utility.MessageSearchResult;

@Entity
@Table(name = "message")

@SqlResultSetMapping(name = "MessageSearchResult", classes = {
		@ConstructorResult(targetClass = MessageSearchResult.class, columns = {
				@ColumnResult(name = "id", type = String.class), @ColumnResult(name = "title", type = String.class),
				@ColumnResult(name = "content", type = String.class),
				@ColumnResult(name = "create_date", type = Date.class) }) })

public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String content;
	private Date createDate;

	public Message() {
		this.createDate = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
