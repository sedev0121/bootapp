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

import com.srm.platform.vendor.utility.NoticeReadSearchResult;

@Entity
@Table(name = "notice_read")

@SqlResultSetMapping(name = "NoticeReadSearchResult", classes = {
		@ConstructorResult(targetClass = NoticeReadSearchResult.class, columns = {
				@ColumnResult(name = "realname", type = String.class),
				@ColumnResult(name = "vendorname", type = String.class),
				@ColumnResult(name = "unitname", type = String.class),
				@ColumnResult(name = "read_date", type = Date.class) }) })

public class NoticeRead {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "notice_id", referencedColumnName = "id")
	private Notice notice;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "to_account_id", referencedColumnName = "id")
	private Account account;

	private Date readDate;

	public NoticeRead() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Notice getNotice() {
		return notice;
	}

	public void setNotice(Notice notice) {
		this.notice = notice;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Date getReadDate() {
		return readDate;
	}

	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}

}
