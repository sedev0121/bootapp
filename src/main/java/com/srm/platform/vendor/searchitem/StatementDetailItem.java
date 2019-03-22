package com.srm.platform.vendor.searchitem;

public interface StatementDetailItem {

	// statement_detail table info
	Long getPurchase_in_detail_id();

	Long getId();

	Float getClosed_quantity();

	Float getRemain_quantity();

	Double getClosed_price();

	Double getClosed_money();

	Double getClosed_tax_price();

	Double getClosed_tax_money();

	String getType();

	// purchase_in_detail table info
	String getPurchase_in_code();

	String getQuantity();

	String getPrice();

	String getCost();

	String getTax_price();

	String getTax_cost();

	String getTax_rate();

	String getUnitname();

	Integer getRowno();

	String getPurchase_in_date();

	// inventory table info
	String getInventoryname();

	String getInventorycode();

	String getSpecs();

	String getMaterial_quantity();

	String getMaterial_tax_price();

	String getReal_quantity();

	String getYuanci();

	String getYinci();

	String getUnit_weight();

	String getMemo();

	String getPo_code();

	String getNat_price();

	String getNat_tax_price();

	String getNat_tax_rate();

}
