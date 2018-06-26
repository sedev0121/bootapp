package com.srm.platform.vendor.utility;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface VendorSearchItem {

	@JsonProperty("id")
	Long getCode();

	@JsonProperty("title")
	String getName();

}
