package com.srm.platform.vendor.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.srm.platform.vendor.searchitem.PurchaseOrderSearchResult;
import com.srm.platform.vendor.searchitem.TaskSearchResult;
import com.srm.platform.vendor.utility.Utils;

@Entity

@SqlResultSetMapping(name = "TaskSearchResult", classes = {
	@ConstructorResult(targetClass = TaskSearchResult.class, columns = {
		@ColumnResult(name = "id", type = Long.class), 
		@ColumnResult(name = "code", type = String.class), 
		@ColumnResult(name = "statement_date", type = Date.class),
		@ColumnResult(name = "make_date", type = Date.class),
		@ColumnResult(name = "maker", type = String.class),
	}) 
})

@Table(name = "task")
public class Task implements Serializable {
	private static final long serialVersionUID = 5855332316773551036L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String code;

	@JsonProperty("make_date")
	private Date makeDate;

	@JsonProperty("statement_date")
	private Date statementDate;
	
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "make_id")
	@ManyToOne()
	private Account maker;

	public Task() {
		this.makeDate = new Date();
		this.code = Utils.generateTaskId();
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

	public Date getMakeDate() {
		return makeDate;
	}

	public void setMakeDate(Date makeDate) {
		this.makeDate = makeDate;
	}

	public Date getStatementDate() {
		return statementDate;
	}

	public void setStatementDate(Date statementDate) {
		this.statementDate = statementDate;
	}

	public Account getMaker() {
		return maker;
	}

	public void setMaker(Account maker) {
		this.maker = maker;
	}	
	
}
