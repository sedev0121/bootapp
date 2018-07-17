package com.srm.platform.vendor.utility;

public interface StatementDetailItem {

	// statement_detail table info
	Long getPurchase_in_detail_id();

	Long getId();

	String getState();

	Float getClosed_quantity();

	Float getClosed_price();

	Float getClosed_money();

	Float getClosed_tax_price();

	Float getClosed_tax_money();

	// purchase_in_detail table info
	String getPurchase_in_code();

	String getQuantity();

	String getPrice();

	String getCost();

	String getTaxprice();

	String getTaxcost();

	String getTaxrate();

	String getUnitname();

	String getCmassunitname();

	String getAssitantunitname();

	String getIrate();

	String getNumber();

	Integer getRowno();

	String getPurchase_in_date();

	// inventory table info
	String getInventoryname();

	String getInventorycode();

	String getSpecs();

}
