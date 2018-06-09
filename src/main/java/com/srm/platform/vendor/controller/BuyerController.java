package com.srm.platform.vendor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/buyer")
@PreAuthorize("hasRole('ROLE_BUYER')")
public class BuyerController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// Home
	@GetMapping({ "", "/" })
	public String home() {
		return "redirect:/buyer/inquery";
	}

	// 价格管理->询价管理
	@GetMapping("/inquery")
	public String inquery() {
		return "buyer/inquery/index";
	}

	// 价格管理->询价管理->新建
	@GetMapping({ "/inquery/add" })
	public String inquery_add() {
		return "buyer/inquery/add";
	}

	// 价格管理->报价管理
	@GetMapping("/quote")
	public String quote() {
		return "buyer/quote/index";
	}

	// 价格管理->报价管理->修改
	@GetMapping("/quote/edit")
	public String quote_edit() {
		return "buyer/quote/edit";
	}

	// 商品管理->商品档案表
	@GetMapping("/inventory")
	public String inventory() {
		return "buyer/inventory/index";
	}

	// 商品管理->商品价格查询
	@GetMapping("/price")
	public String price() {
		return "buyer/price/index";
	}

	// 供应商管理
	@GetMapping("/vendor")
	public String vendor() {
		return "buyer/vendor/index";
	}

	// 供应商管理->修改
	@GetMapping("/vendor/edit")
	public String vendor_edit() {
		return "buyer/vendor/edit";
	}

	// 订单管理
	@GetMapping("/purchaseorder")
	public String purchaseorder() {
		return "buyer/purchaseorder/index";
	}

	// 订单管理->明细
	@GetMapping("/purchaseorder/edit")
	public String purchaseorder_edit() {
		return "buyer/purchaseorder/edit";
	}

	// 出货看板
	@GetMapping("/shipment")
	public String shipment() {
		return "buyer/shipment/index";
	}

	// 对账单管理
	@GetMapping("/purinvoice")
	public String purinvoice() {
		return "buyer/purinvoice/index";
	}

	// 对账单管理
	@GetMapping("/purinvoice/edit")
	public String purinvoice_edit() {
		return "buyer/purinvoice/edit";
	}

	// 外购入库单
	@GetMapping("/purchasein")
	public String purchasein() {
		return "buyer/purchasein/index";
	}

}
