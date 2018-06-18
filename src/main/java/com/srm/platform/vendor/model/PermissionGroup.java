package com.srm.platform.vendor.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "permission_group")
public class PermissionGroup {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;
	private String description;

	@JsonIgnore
	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "permission_group_function_action", joinColumns = {
			@JoinColumn(name = "group_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "function_action_id", referencedColumnName = "id") })
	private List<FunctionAction> functionActions;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public List<FunctionAction> getFunctionActions() {
		return functionActions;
	}

	public void setFunctionActions(List<FunctionAction> functionActions) {
		this.functionActions = functionActions;
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

	public PermissionGroup() {

	}

	public PermissionGroup(String name, String description) {
		this.name = name;
		this.description = description;
	}
}
