package com.srm.platform.vendor.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import com.srm.platform.vendor.utility.NoticeSearchResult;

@Entity
@Table(name = "notice")

@SqlResultSetMapping(name = "NoticeSearchResult", classes = {
		@ConstructorResult(targetClass = NoticeSearchResult.class, columns = {
				@ColumnResult(name = "id", type = String.class), @ColumnResult(name = "title", type = String.class),
				@ColumnResult(name = "content", type = String.class),
				@ColumnResult(name = "create_date", type = String.class),
				@ColumnResult(name = "create_name", type = String.class),
				@ColumnResult(name = "create_unitname", type = String.class) }) })

public class Notice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String content;
	private Date createDate;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "create_unit", referencedColumnName = "id")
	private Unit unit;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "create_account", referencedColumnName = "id")
	private Account account;

	public Notice() {
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

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

}
