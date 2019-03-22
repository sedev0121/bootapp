package com.srm.platform.vendor.searchitem;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface InventorySearchItem {

	@JsonProperty("id")
	String getCode();

	@JsonProperty("title")
	String getName();

	String getSpecs();

	String getPuunitName();

	Date getStartDate();

	Date getEndDate();

	Integer getPrice();

}
