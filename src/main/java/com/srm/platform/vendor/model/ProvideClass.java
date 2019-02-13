package com.srm.platform.vendor.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srm.platform.vendor.utility.NoticeSearchResult;
import com.srm.platform.vendor.utility.ProvideClassSearchResult;

@Entity
@Table(name = "provide_class")

@SqlResultSetMapping(name = "ProvideClassSearchResult", classes = {
		@ConstructorResult(targetClass = ProvideClassSearchResult.class, columns = {
				@ColumnResult(name = "id", type = String.class), 
				@ColumnResult(name = "code", type = String.class),
				@ColumnResult(name = "name", type = String.class),
			}) 
		})

public class ProvideClass implements Serializable {

	private static final long serialVersionUID = 5097604481233401362L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Column(name = "code")
	private Long code;

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

	public Long getCode() {
		return code;
	}

	public void setCode(Long code) {
		this.code = code;
	}

	public ProvideClass() {

	}

	public ProvideClass(String name, Long code) {
		this.name = name;
		this.code = code;
	}
}
