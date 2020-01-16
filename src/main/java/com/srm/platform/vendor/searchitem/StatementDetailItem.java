package com.srm.platform.vendor.searchitem;

public interface StatementDetailItem {

	// statement_detail table info
	Long getId();
	Long getRow_no();
	Long getPi_detail_id();
	String getType();
	String getAdjust_tax_cost();
	
	// purchase_in_detail table info
	String getPi_code();
	String getPi_date();
	String getPi_quantity();
	String getPi_auto_id();
	Integer getPi_state();
	Integer getPi_erp_changed();
	String getPi_price();
	String getPi_tax_price();
	String getPi_cost();
	String getPi_tax_cost();
	String getPi_tax();
	
	String getPi_store_code();
	String getPi_tax_rate();
	String getPi_vendor_code();
	
	String getTax_rate();
	String getTax();
	String getPrice();
	String getCost();
	String getTax_price();
	String getTax_cost();


	// inventory table info
	String getInventory_name();
	String getInventory_code();
	String getSpecs();
	String getUnitname();
	
	//purchase_order info
	String getConfirmed_memo();
	String getPo_code();
	String getPo_row_no();

	
	//delivery info
	String getDelivery_code();
	String getDelivered_quantity();
	String getDelivery_row_no();

}
