package com.srm.platform.vendor.searchitem;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface SearchStoreItem {

	@JsonProperty("id")
	String getCode();

	@JsonProperty("title")
	String getName();
	
	@JsonProperty("is_set")
	String getData();

}
