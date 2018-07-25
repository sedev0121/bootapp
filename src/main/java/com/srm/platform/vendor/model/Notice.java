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
				@ColumnResult(name = "create_date", type = Date.class),
				@ColumnResult(name = "create_name", type = String.class),
				@ColumnResult(name = "create_unitname", type = String.class),
				@ColumnResult(name = "verify_date", type = Date.class),
				@ColumnResult(name = "verify_name", type = String.class),
				@ColumnResult(name = "state", type = Integer.class),
				@ColumnResult(name = "to_all_vendor", type = Integer.class),
				@ColumnResult(name = "to_unit_account", type = Integer.class),
				@ColumnResult(name = "attach_file_name", type = String.class) }) })

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
	private Account createAccount;

	private Date verifyDate;
	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "verify_account", referencedColumnName = "id")
	private Account verifyAccount;

	private Integer type;
	private Integer state = 1;
	private Integer toAllVendor;
	private Integer toUnitAccount;
	private String attachFileName;
	private String attachOriginalName;
	private String vendorCodeList;

	public Notice() {
		this.createDate = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVendorCodeList() {
		return vendorCodeList;
	}

	public void setVendorCodeList(String vendorCodeList) {
		this.vendorCodeList = vendorCodeList;
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

	public Account getCreateAccount() {
		return createAccount;
	}

	public void setCreateAccount(Account createAccount) {
		this.createAccount = createAccount;
	}

	public Date getVerifyDate() {
		return verifyDate;
	}

	public void setVerifyDate(Date verifyDate) {
		this.verifyDate = verifyDate;
	}

	public Account getVerifyAccount() {
		return verifyAccount;
	}

	public void setVerifyAccount(Account verifyAccount) {
		this.verifyAccount = verifyAccount;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getToAllVendor() {
		return toAllVendor;
	}

	public void setToAllVendor(Integer toAllVendor) {
		this.toAllVendor = toAllVendor;
	}

	public Integer getToUnitAccount() {
		return toUnitAccount;
	}

	public void setToUnitAccount(Integer toUnitAccount) {
		this.toUnitAccount = toUnitAccount;
	}

	public String getAttachFileName() {
		return attachFileName;
	}

	public void setAttachFileName(String attachFileName) {
		this.attachFileName = attachFileName;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getAttachOriginalName() {
		return attachOriginalName;
	}

	public void setAttachOriginalName(String attachOriginalName) {
		this.attachOriginalName = attachOriginalName;
	}

}
