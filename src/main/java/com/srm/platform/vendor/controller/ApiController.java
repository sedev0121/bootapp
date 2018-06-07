package com.srm.platform.vendor.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.srm.platform.vendor.u8api.ApiClient;

@RestController
@RequestMapping(path = "/api")
public class ApiController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ApiClient apiClient;

	// 供应商管理
	@RequestMapping(value = "/vendor/batch_get", produces = "application/json")
	public String vendor(@RequestParam Map<String, String> requestParams) {
		return apiClient.getBatchVendor(requestParams);
	}

	@RequestMapping(value = "/vendor/get/{id}", produces = "application/json")
	public String vendor_get(@PathVariable("id") String id) {
		return apiClient.getVendor(id);
	}

	// 价格管理
	@RequestMapping(value = "/venpriceadjust/batch_get", produces = "application/json")
	public String venpriceadjust(Model model) {
		return apiClient.getBatchVenPriceAdjust();
	}

	// 商品管理
	@RequestMapping(value = "/inventory/batch_get", produces = "application/json")
	public String inventory(Model model) {
		return apiClient.getBatchInventory();
	}

	// 外购入库单
	@RequestMapping(value = "/purchasein/batch_get", produces = "application/json")
	public String purchasein(Model model) {
		return apiClient.getBatchPurchaseIn();
	}

	// 对账单管理
	@RequestMapping(value = "/purinvoice/batch_get", produces = "application/json")
	public String purinvoice(Model model) {
		return apiClient.getBatchPurInvoice();
	}

	// 订单管理
	@RequestMapping(value = "/purchaseorder/batch_get", produces = "application/json")
	public String purchaseorder(Model model) {
		return apiClient.getBatchPurchaseOrder();
	}

	@RequestMapping(value = "/purchaseorder/get/{id}", produces = "application/json")
	public String purchaseorder_get(@PathVariable("id") String id) {
		return apiClient.getPurchaseOrder(id);
	}

	// 出货看板
	@RequestMapping(value = "/shipment/batch_get", produces = "application/json")
	public String shipment(@RequestParam Map<String, String> requestParams) {
		return apiClient.getBatchVendor(requestParams);
	}
}
