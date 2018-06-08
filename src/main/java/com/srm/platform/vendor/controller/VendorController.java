package com.srm.platform.vendor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/vendor")
@PreAuthorize("hasRole('ROLE_VENDOR')")
public class VendorController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// 价格管理->询价管理
	@GetMapping({ "/inquery", "", "/" })
	public String inquery() {
		return "vendor/inquery/index";
	}

	@GetMapping("/inquery/add")
	public String inquery_add() {
		return "vendor/inquery/add";
	}

	// 价格管理->报价管理
	@GetMapping("/quote")
	public String quote() {
		return "vendor/quote/index";
	}

	// 订单管理->订单确认
	@GetMapping("/purchaseorder/confirm")
	public String purchaseorder_confirm() {
		return "vendor/purchaseorder/index";
	}

	// 订单管理->交期确认
	@GetMapping("/purchaseorder/delivery")
	public String purchaseorder_delivery() {
		return "vendor/purchaseorder/delivery";
	}

	// 订单管理->订单发货
	@GetMapping("/purchaseorder/ship")
	public String purchaseorder_ship() {
		return "vendor/purchaseorder/ship";
	}

	// 订单管理->订单发货->导入送货单
	@GetMapping("/purchaseorder/ship/import")
	public String purchaseorder_ship_import() {
		return "vendor/purchaseorder/ship_import";
	}

	// 出货看板
	@GetMapping("/shipment")
	public String shipment() {
		return "vendor/shipment/index";
	}

	// 对账单管理
	@GetMapping("/purinvoice")
	public String purinvoice() {
		return "vendor/purinvoice/index";
	}

	// 对账单管理->对账单明细
	@GetMapping("/purinvoice/edit")
	public String purinvoice_edit() {
		return "vendor/purinvoice/edit";
	}

}
