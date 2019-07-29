package com.srm.platform.vendor.searchitem;

public class ShipSearchResult {

	private String company_name;
	private String code;
	private String row_no;
	private String orderdate;
	private String vencode;
	private String vendor_name;
	private String inventory_code;
	private String inventory_name;
	private String specs;
	private String unit_name;
	private String arrive_date;
	private String quantity;
	private String package_quantity;
	private String box_class_name;
	private String count_per_box;
	private String arrived_quantity;
	private String delivered_quantity;
	private String backed_quantity;
	private String invoiced_quantity;
	private String close_date;
	private String memo;
	private String confirmed_memo;
	private String price;
	private String money;
	private String tax_rate;
	private String tax_price;
	private String sum;

	public ShipSearchResult(String company_name, String code, String row_no, String orderdate, String vencode,
			String vendor_name, String inventory_code, String inventory_name, String specs, String unit_name,
			String arrive_date, String quantity, String package_quantity, String box_class_name, String count_per_box,
			String arrived_quantity, String delivered_quantity, String backed_quantity, String invoiced_quantity,
			String close_date, String memo, String confirmed_memo, String price, String money, String tax_rate,
			String tax_price, String sum) {

		this.company_name = company_name;
		this.code = code;
		this.row_no = row_no;
		this.orderdate = orderdate;
		this.vencode = vencode;
		this.vendor_name = vendor_name;
		this.inventory_code = inventory_code;
		this.inventory_name = inventory_name;
		this.specs = specs;
		this.unit_name = unit_name;
		this.arrive_date = arrive_date;
		this.quantity = quantity;
		this.package_quantity = package_quantity;
		this.arrived_quantity = arrived_quantity;
		this.box_class_name = box_class_name;
		this.count_per_box = count_per_box;
		this.delivered_quantity = delivered_quantity;
		this.backed_quantity = backed_quantity;
		this.invoiced_quantity = invoiced_quantity;
		this.close_date = close_date;
		this.memo = memo;
		this.confirmed_memo = confirmed_memo;
		this.price = price;
		this.money = money;
		this.tax_rate = tax_rate;
		this.tax_price = tax_price;
		this.sum = sum;

	}

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getSum() {
		return sum;
	}

	public void setSum(String sum) {
		this.sum = sum;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getTax_rate() {
		return tax_rate;
	}

	public void setTax_rate(String tax_rate) {
		this.tax_rate = tax_rate;
	}

	public String getTax_price() {
		return tax_price;
	}

	public void setTax_price(String tax_price) {
		this.tax_price = tax_price;
	}

	public String getArrived_quantity() {
		return arrived_quantity;
	}

	public void setArrived_quantity(String arrived_quantity) {
		this.arrived_quantity = arrived_quantity;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRow_no() {
		return row_no;
	}

	public void setRow_no(String row_no) {
		this.row_no = row_no;
	}

	public String getOrderdate() {
		return orderdate;
	}

	public void setOrderdate(String orderdate) {
		this.orderdate = orderdate;
	}

	public String getVencode() {
		return vencode;
	}

	public void setVencode(String vencode) {
		this.vencode = vencode;
	}

	public String getVendor_name() {
		return vendor_name;
	}

	public void setVendor_name(String vendor_name) {
		this.vendor_name = vendor_name;
	}

	public String getInventory_code() {
		return inventory_code;
	}

	public void setInventory_code(String inventory_code) {
		this.inventory_code = inventory_code;
	}

	public String getInventory_name() {
		return inventory_name;
	}

	public void setInventory_name(String inventory_name) {
		this.inventory_name = inventory_name;
	}

	public String getSpecs() {
		return specs;
	}

	public void setSpecs(String specs) {
		this.specs = specs;
	}

	public String getUnit_name() {
		return unit_name;
	}

	public void setUnit_name(String unit_name) {
		this.unit_name = unit_name;
	}

	public String getArrive_date() {
		return arrive_date;
	}

	public void setArrive_date(String arrive_date) {
		this.arrive_date = arrive_date;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getPackage_quantity() {
		return package_quantity;
	}

	public void setPackage_quantity(String package_quantity) {
		this.package_quantity = package_quantity;
	}

	public String getBox_class_name() {
		return box_class_name;
	}

	public void setBox_class_name(String box_class_name) {
		this.box_class_name = box_class_name;
	}

	public String getCount_per_box() {
		return count_per_box;
	}

	public void setCount_per_box(String count_per_box) {
		this.count_per_box = count_per_box;
	}

	public String getDelivered_quantity() {
		return delivered_quantity;
	}

	public void setDelivered_quantity(String delivered_quantity) {
		this.delivered_quantity = delivered_quantity;
	}

	public String getBacked_quantity() {
		return backed_quantity;
	}

	public void setBacked_quantity(String backed_quantity) {
		this.backed_quantity = backed_quantity;
	}

	public String getInvoiced_quantity() {
		return invoiced_quantity;
	}

	public void setInvoiced_quantity(String invoiced_quantity) {
		this.invoiced_quantity = invoiced_quantity;
	}

	public String getClose_date() {
		return close_date;
	}

	public void setClose_date(String close_date) {
		this.close_date = close_date;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getConfirmed_memo() {
		return confirmed_memo;
	}

	public void setConfirmed_memo(String confirmed_memo) {
		this.confirmed_memo = confirmed_memo;
	}

}
