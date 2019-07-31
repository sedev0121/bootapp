package com.srm.platform.vendor.searchitem;

public interface ContractDetailItem {

	Long getId();

	Integer getRow_no();
	
	String getName();

	String getCode();
	
	String getSpecs();
	
	String getMain_measure();
	
	Double getQuantity();
	
	Double getTax_price();
	
	String getMemo();
	
	Integer getFloating_direction();
	
	Double getFloating_price();

}
