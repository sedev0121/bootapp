package com.srm.platform.vendor.searchitem;

import java.io.Serializable;
import java.util.Date;

public class BoxSearchResult implements Serializable {

	private static final long serialVersionUID = -7774864748081640759L;

	String id;
	String code;
	Date bind_date;
	String box_class_name;
	String bind_property;
	String vendor_code;
	String vendor_name;
	String inventory_code;
	String inventory_name;
	String inventory_spec;
	String delivery_code;
	String deliver_number;
	Double quantity;
	String state;
	String used;

	public BoxSearchResult(String id, String code, Date bind_date, String box_class_name, String bind_property,
			String vendor_code, String vendor_name, String inventory_code, String inventory_name, String inventory_spec,
			String delivery_code, String deliver_number, Double quantity, String state, String used) {

		this.id = id;
		this.code = code;
		this.bind_date = bind_date;
		this.box_class_name = box_class_name;
		this.bind_property = bind_property;
		this.vendor_code = vendor_code;
		this.vendor_name = vendor_name;
		this.inventory_code = inventory_code;
		this.inventory_name = inventory_name;
		this.inventory_spec = inventory_spec;
		this.delivery_code = delivery_code;
		this.deliver_number = deliver_number;
		this.quantity = quantity;
		this.state = state;
		this.used = used;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDelivery_code() {
		return delivery_code;
	}

	public void setDelivery_code(String delivery_code) {
		this.delivery_code = delivery_code;
	}

	public String getDeliver_number() {
		return deliver_number;
	}

	public void setDeliver_number(String deliver_number) {
		this.deliver_number = deliver_number;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getBind_date() {
		return bind_date;
	}

	public void setBind_date(Date bind_date) {
		this.bind_date = bind_date;
	}

	public String getBox_class_name() {
		return box_class_name;
	}

	public void setBox_class_name(String box_class_name) {
		this.box_class_name = box_class_name;
	}

	public String getBind_property() {
		return bind_property;
	}

	public void setBind_property(String bind_property) {
		this.bind_property = bind_property;
	}

	public String getVendor_code() {
		return vendor_code;
	}

	public void setVendor_code(String vendor_code) {
		this.vendor_code = vendor_code;
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

	public String getInventory_spec() {
		return inventory_spec;
	}

	public void setInventory_spec(String inventory_spec) {
		this.inventory_spec = inventory_spec;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUsed() {
		return used;
	}

	public void setUsed(String used) {
		this.used = used;
	}

}
