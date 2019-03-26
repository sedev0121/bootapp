package com.srm.platform.vendor.searchitem;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface SearchItem {

	@JsonProperty("id")
	String getCode();

	@JsonProperty("title")
	String getName();

}
