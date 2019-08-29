package com.srm.platform.vendor.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "company")

public class Company {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String code;

	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "statement_company_id")
	@ManyToOne()
	private StatementCompany statementCompany;
	
	public Company() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public StatementCompany getStatementCompany() {
		return statementCompany;
	}

	public void setStatementCompany(StatementCompany statementCompany) {
		this.statementCompany = statementCompany;
	}	
}
