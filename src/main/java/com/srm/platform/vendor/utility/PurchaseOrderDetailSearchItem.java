package com.srm.platform.vendor.utility;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface PurchaseOrderDetailSearchItem {

	Integer getRowno();

	String getCode();

	Float getQuantity();

	@JsonProperty("shipped_quantity")
	Float getShipped_quantity();

	String getInventoryname();

	String getInventorycode();

	String getVendorname();

	String getVendorcode();

	String getSpecs();

	String getUnitname();

	String getArrivedate();

	Date getConfirmdate();

	String getArrivenote();

	String getConfirmnote();

	String getRemain_quantity();

}
