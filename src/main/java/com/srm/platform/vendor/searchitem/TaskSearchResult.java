package com.srm.platform.vendor.searchitem;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskSearchResult implements Serializable {

	private static final long serialVersionUID = 3692876780746931969L;

	private Long id;
	private String code;

	@JsonProperty("statement_date")
	private Date statementDate;
	
	@JsonProperty("make_date")
	private Date makeDate;
	
	private String maker;
	

	public TaskSearchResult(Long id, String code, Date statementDate, Date makeDate, String maker) {
		this.id = id;
		this.code = code;
		this.statementDate = statementDate;
		this.makeDate = makeDate;
		this.maker = maker;	
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public Date getStatementDate() {
		return statementDate;
	}


	public void setStatementDate(Date statementDate) {
		this.statementDate = statementDate;
	}


	public Date getMakeDate() {
		return makeDate;
	}


	public void setMakeDate(Date makeDate) {
		this.makeDate = makeDate;
	}


	public String getMaker() {
		return maker;
	}


	public void setMaker(String maker) {
		this.maker = maker;
	}

}
