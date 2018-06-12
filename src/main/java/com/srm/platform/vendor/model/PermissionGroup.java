package com.srm.platform.vendor.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "permission_group")
public class PermissionGroup {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private String name;

	private String description;

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

	protected PermissionGroup() {

	}

	public PermissionGroup(String name, String description) {
		this.name = name;
		this.description = description;
	}
}
