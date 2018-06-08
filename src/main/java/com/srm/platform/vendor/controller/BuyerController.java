package com.srm.platform.vendor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/buyer")
public class BuyerController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// 价格管理->询价管理
	@GetMapping("/inquery")
	public String inquery() {
		return "buyer/inquery/index";
	}

	// 价格管理->报价管理
	@GetMapping("/quote")
	public String quote() {
		return "buyer/quote/index";
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

	// 订单管理
	@GetMapping("/purchaseorder")
	public String purchaseorder() {
		return "buyer/purchaseorder/index";
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

	// 外购入库单
	@GetMapping("/purchasein")
	public String purchasein() {
		return "buyer/purchasein/index";
	}

}
