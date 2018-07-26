package com.srm.platform.vendor.utility;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public class ShipSaveForm {

	private List<SimpleEntry<Long, String>> rows;

	public List<SimpleEntry<Long, String>> getRows() {
		return rows;
	}

	public void setRows(List<SimpleEntry<Long, String>> rows) {
		this.rows = rows;
	}

}
