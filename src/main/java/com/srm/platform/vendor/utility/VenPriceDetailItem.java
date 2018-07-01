package com.srm.platform.vendor.utility;

import java.util.Date;

public interface VenPriceDetailItem {

	String getCinvcode();

	String getName();

	String getSpecs();

	String getPuunit_name();

	Float getFminquantity();

	Float getFmaxquantity();

	Integer getIunitprice();

	Integer getItaxrate();

	Integer getItaxunitprice();

	Date getDstartdate();

	Date getDenddate();

	Integer getIvalid();

	String getCbodymemo();

}
