package com.srm.platform.vendor.searchitem;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ContractSearchItem {

	@JsonProperty("id")
	String getCode();

	@JsonProperty("title")
	String getName();
	
	Integer getPrice_type();
	
	Double getBase_price();
	
	Double getFloating_price();
}
