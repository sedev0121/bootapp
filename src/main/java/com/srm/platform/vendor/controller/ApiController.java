package com.srm.platform.vendor.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	public String venpriceadjust(@RequestParam Map<String, String> requestParams) {
		return apiClient.getBatchVenPriceAdjust(requestParams);
	}

	// 商品管理
	@RequestMapping(value = "/inventory/batch_get", produces = "application/json")
	public String inventory(@RequestParam Map<String, String> requestParams) {
		return apiClient.getBatchInventory(requestParams);
	}

	// 外购入库单
	@RequestMapping(value = "/purchasein/batch_get", produces = "application/json")
	public String purchasein(@RequestParam Map<String, String> requestParams) {
		return apiClient.getBatchPurchaseIn(requestParams);
	}

	// 对账单管理
	@RequestMapping(value = "/purinvoice/batch_get", produces = "application/json")
	public String purinvoice(@RequestParam Map<String, String> requestParams) {
		return apiClient.getBatchPurInvoice(requestParams);
	}

	// 订单管理
	@RequestMapping(value = "/purchaseorder/batch_get", produces = "application/json")
	public String purchaseorder(@RequestParam Map<String, String> requestParams) {
		return apiClient.getBatchPurchaseOrder(requestParams);
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
