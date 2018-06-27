package com.srm.platform.vendor.controller;

import java.security.Principal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.model.Price;
import com.srm.platform.vendor.model.VenPriceAdjustMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.PriceRepository;
import com.srm.platform.vendor.repository.VendorRepository;

@Controller
@RequestMapping(path = "/buyer")
@PreAuthorize("hasRole('ROLE_BUYER')")
public class BuyerController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private PriceRepository priceRepository;

	@Autowired
	private AccountRepository accountRepository;

	// Home
	@GetMapping({ "", "/" })
	public String home() {
		return "buyer/index";
	}

	// 价格管理->询价管理
	@GetMapping("/inquery")
	public String inquery() {
		return "buyer/inquery/index";
	}

	// 价格管理->询价管理->新建
	@GetMapping({ "/inquery/add" })
	public String inquery_add(Principal principal, Model model) {
		VenPriceAdjustMain main = new VenPriceAdjustMain(accountRepository);
		model.addAttribute("main", main);
		return "buyer/inquery/add";
	}

	// 价格管理->询价管理->新建
	@GetMapping({ "/inquery/{id}/edit" })
	public String inquery_edit() {
		return "buyer/inquery/add";
	}

	// 价格管理->报价管理
	@GetMapping("/quote")
	public String quote() {
		return "buyer/quote/index";
	}

	// 价格管理->报价管理->修改
	@GetMapping("/quote/{id}/edit")
	public String quote_edit() {
		return "buyer/quote/edit";
	}

	// 商品管理->商品档案表
	@GetMapping("/inventory")
	public String inventory() {
		return "buyer/inventory/index";
	}

	// 供应商管理->修改
	@GetMapping("/inventory/{code}/edit")
	public String inventory_edit(@PathVariable("code") String code, Model model) {
		Inventory data = new Inventory();
		data.setCode(code);
		Example<Inventory> example = Example.of(data);
		Optional<Inventory> result = inventoryRepository.findOne(example);
		model.addAttribute("data", result.isPresent() ? result.get() : new Inventory());
		return "buyer/inventory/edit";
	}

	// 商品管理->商品价格查询
	@GetMapping("/price")
	public String price() {
		return "buyer/price/index";
	}

	// 供应商管理->修改
	@GetMapping("/price/{id}/edit")
	public String price_edit(@PathVariable("id") Long id, Model model) {

		Optional<Price> result = priceRepository.findById(id);

		model.addAttribute("data", result.isPresent() ? result.get() : new Price());
		return "buyer/price/edit";
	}

	// 供应商管理
	@GetMapping("/vendor")
	public String vendor() {
		return "buyer/vendor/index";
	}

	// 报表中心
	@GetMapping("/report")
	public String report() {
		return "buyer/report/index";
	}

	// 供应商管理->修改
	@GetMapping("/vendor/{code}/edit")
	public String vendor_edit(@PathVariable("code") String code, Model model) {
		Vendor vendor = new Vendor();
		vendor.setCode(code);
		Example<Vendor> example = Example.of(vendor);
		Optional<Vendor> result = vendorRepository.findOne(example);
		model.addAttribute("data", result.isPresent() ? result.get() : new Vendor());
		return "buyer/vendor/edit";
	}

	// 订单管理
	@GetMapping("/purchaseorder")
	public String purchaseorder() {
		return "buyer/purchaseorder/index";
	}

	// 订单管理->明细
	@GetMapping("/purchaseorder/{id}/edit")
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
	@GetMapping("/purinvoice/add")
	public String purinvoice_add() {
		return "buyer/purinvoice/edit";
	}

	// 对账单管理
	@GetMapping("/purinvoice/{id}/edit")
	public String purinvoice_edit() {
		return "buyer/purinvoice/edit";
	}

	// 外购入库单
	@GetMapping("/purchasein")
	public String purchasein() {
		return "buyer/purchasein/index";
	}

}
