package com.srm.platform.vendor.utility;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface PurchaseOrderSearchItem {

	String getCode();

	String getState();

	String getVendorname();

	String getDeployername();

	String getReviewername();

	Date getDeploydate();

	Date getReviewdate();

	String getMaker();

	String getMakedate();

	Float getSum();

	Float getMoney();

	Integer getSrmstate();

	@JsonProperty("purchase_type_name")
	String getPurchase_type_name();

}
