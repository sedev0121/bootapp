package com.srm.platform.vendor.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.StringUtils;

import com.srm.platform.vendor.searchitem.DimensionTargetItem;

public class AccountPermission {
	private Long accountId;
	private Long functionActionId;
	private List<Long> companyList;
	private List<Long> accountList;
	private List<Long> storeList;
	private List<String> vendorList;
	private List<String> inventoryClassList;

	public AccountPermission(Long accountId, Long functionActionId, List<DimensionTargetItem> scopeList) {
		this.accountId = accountId;
		this.functionActionId = functionActionId;
		for (int i=0; i<scopeList.size(); i++) {
			DimensionTargetItem temp = scopeList.get(i);
			if (temp.getDimension_id() == Constants.PERMISSION_DIMENSION_COMPANY) {
				if (temp.getTargets() != null && temp.getTargets().length() > 0) {
					List<String> idList = Arrays.asList(StringUtils.split(temp.getTargets(), ","));
					this.companyList = new ArrayList<Long>();
					for (int j=0; j<idList.size(); j++) {
						this.companyList.add(Long.valueOf(idList.get(j)));
					}
				}
				continue;
			}
			if (temp.getDimension_id() == Constants.PERMISSION_DIMENSION_STORE) {
				if (temp.getTargets() != null && temp.getTargets().length() > 0) {
					List<String> idList = Arrays.asList(StringUtils.split(temp.getTargets(), ","));
					this.storeList = new ArrayList<Long>();
					for (int j=0; j<idList.size(); j++) {
						this.storeList.add(Long.valueOf(idList.get(j)));
					}
				}
				continue;
			}
			if (temp.getDimension_id() == Constants.PERMISSION_DIMENSION_ACCOUNT) {
				if (temp.getTargets() != null && temp.getTargets().length() > 0) {
					List<String> idList = Arrays.asList(StringUtils.split(temp.getTargets(), ","));
					this.accountList = new ArrayList<Long>();
					for (int j=0; j<idList.size(); j++) {
						this.accountList.add(Long.valueOf(idList.get(j)));
					}
				}
				continue;
			}
			if (temp.getDimension_id() == Constants.PERMISSION_DIMENSION_VENDOR) {
				if (temp.getTargets() != null && temp.getTargets().length() > 0) {
					this.vendorList = Arrays.asList(StringUtils.split(temp.getTargets(), ","));
				}
				continue;
			}
			if (temp.getDimension_id() == Constants.PERMISSION_DIMENSION_INVENTORY) {
				if (temp.getTargets() != null && temp.getTargets().length() > 0) {
					this.inventoryClassList = Arrays.asList(StringUtils.split(temp.getTargets(), ","));
				}
				continue;
			}
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

	public List<Long> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<Long> companyList) {
		this.companyList = companyList;
	}

	public List<Long> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<Long> accountList) {
		this.accountList = accountList;
	}

	public List<Long> getStoreList() {
		return storeList;
	}

	public void setStoreList(List<Long> storeList) {
		this.storeList = storeList;
	}

	public List<String> getVendorList() {
		return vendorList;
	}

	public void setVendorList(List<String> vendorList) {
		this.vendorList = vendorList;
	}

	public List<String> getInventoryClassList() {
		return inventoryClassList;
	}

	public void setInventoryClassList(List<String> inventoryClassList) {
		this.inventoryClassList = inventoryClassList;
	}
	
	public boolean checkCompanyPermission(Long companyId) {
		if (this.companyList == null || this.companyList.size() == 0) {
			return false;
		} else {
			return this.companyList.contains(companyId);
		}
	}
	
	public boolean checkAccountPermission(Long accountId) {
		if (this.accountList == null || this.accountList.size() == 0) {
			return false;
		} else {
			return this.accountList.contains(accountId);
		}
	}
	
	public boolean checkStorePermission(Long storeId) {
		if (this.storeList == null || this.storeList.size() == 0) {
			return false;
		} else {
			return this.storeList.contains(storeId);
		}
	}
	
	public boolean checkVendorPermission(String vendorCode) {
		if (this.vendorList == null || this.vendorList.size() == 0) {
			return false;
		} else {
			return this.vendorList.contains(vendorCode);
		}
	}
	
	public boolean checkCompanyPermission(String inventoryClassCode) {
		if (this.inventoryClassList == null || this.inventoryClassList.size() == 0) {
			return false;
		} else {
			return this.inventoryClassList.contains(inventoryClassCode);
		}
	}

}
