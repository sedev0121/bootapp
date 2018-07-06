package com.srm.platform.vendor.utility;

import java.util.Date;

public interface PurchaseOrderDetailSearchItem {

	String getCode();

	Float getQuantity();

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

}
