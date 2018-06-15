package com.srm.platform.vendor.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "permission_group_function_action")
public class PermissionGroupFunctionAction {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "function_action_id")
	private Long functionActionId;

	@Column(name = "group_id")
	private Long groupId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFunctionActionId() {
		return functionActionId;
	}

	public void setFunctionActionId(Long functionActionId) {
		this.functionActionId = functionActionId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
}
