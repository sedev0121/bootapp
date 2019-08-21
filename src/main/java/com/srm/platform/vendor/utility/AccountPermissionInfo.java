package com.srm.platform.vendor.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.StringUtils;

import com.srm.platform.vendor.searchitem.DimensionTargetItem;

public class AccountPermissionInfo {
	private Long accountId;
	private Long functionActionId;
	
	private List<AccountPermission> list = new ArrayList<AccountPermission>();

	public AccountPermissionInfo(Long accountId, Long functionActionId, List<DimensionTargetItem> scopeList) {
		this.accountId = accountId;
		this.functionActionId = functionActionId;
		
		long tempGroupId = -1;
		List<DimensionTargetItem> tempList = new ArrayList<DimensionTargetItem>();
		for(DimensionTargetItem item : scopeList) {
			if (item.getGroup_id() == tempGroupId) {
				tempList.add(item);
			} else {
				tempGroupId = item.getGroup_id();
				if (tempList.size() > 0) {
					list.add(new AccountPermission(accountId, functionActionId, tempList));	
				}
				tempList = new ArrayList<DimensionTargetItem>();
				tempList.add(item);
			}
		}
		
		if (tempList.size() > 0) {
			list.add(new AccountPermission(accountId, functionActionId, tempList));	
		}
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getFunctionActionId() {
		return functionActionId;
	}

	public void setFunctionActionId(Long functionActionId) {
		this.functionActionId = functionActionId;
	}

	public List<AccountPermission> getList() {
		return list;
	}

	public void setList(List<AccountPermission> list) {
		this.list = list;
	}
	
	public String toString() {
		String permission = "";
		for(AccountPermission temp : list) {
			permission += temp.toString() + "\n";
		}
		return permission;
	}
}
