package com.srm.platform.vendor.saveform;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExportShipForm {

	private String code;
	private Integer rowno;
	private String inventory;
	private String order;
	private String dir;
	private List<Long> list;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getRowno() {
		return rowno;
	}

	public void setRowno(Integer rowno) {
		this.rowno = rowno;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public List<Long> getList() {
		return list;
	}

	public void setList(List<Long> list) {
		this.list = list;
	}

	public String getInventory() {
		return inventory;
	}

	public void setInventory(String inventory) {
		this.inventory = inventory;
	}

}
