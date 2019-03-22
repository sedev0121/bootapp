package com.srm.platform.vendor.utility;

import java.util.List;
import java.util.Map;

public class PermissionRecord {
	
	private Long permissionGroupId;
	private List<Map<Long, List<String>>> permissionScopeList;
	public Long getPermissionGroupId() {
		return permissionGroupId;
	}
	public void setPermissionGroupId(Long permissionGroupId) {
		this.permissionGroupId = permissionGroupId;
	}
	public List<Map<Long, List<String>>> getPermissionScopeList() {
		return permissionScopeList;
	}
	public void setPermissionScopeList(List<Map<Long, List<String>>> permissionScopeList) {
		this.permissionScopeList = permissionScopeList;
	}
	
}
