package com.srm.platform.vendor.utility;

public class PermissionScopeRecord {
	
	private Long group_id;
//	private Long dimension_id;
	private String target_id;
	
	public Long getGroup_id() {
		return group_id;
	}
	public void setGroup_id(Long group_id) {
		this.group_id = group_id;
	}

	public String getTarget_id() {
		return target_id;
	}
	public void setTarget_id(String target_id) {
		this.target_id = target_id;
	}
	
//	public String toString() {
//		return String.format("%d %d %s", this.getGroup_id(), this.getDimension_id(), this.getTarget_id());
//	}
//	public Long getDimension_id() {
//		return dimension_id;
//	}
//	public void setDimension_id(Long dimension_id) {
//		this.dimension_id = dimension_id;
//	}
	
}
