package com.srm.platform.vendor.searchitem;

public interface ContractOrderDetailItem {

	Long getId();

	Integer getRow_no();
	
	String getName();

	String getCode();
	
	String getSpecs();
	
	String getMain_measure();
	
	Double getQuantity();	
	
	Double getTax_rate();
	
	Double getContract_tax_price();
	
	Double getContract_floating_price();
}
