package com.srm.platform.vendor.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.model.PurchaseOrderDetail;
import com.srm.platform.vendor.model.Unit;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.PriceRepository;
import com.srm.platform.vendor.repository.PurchaseOrderDetailRepository;
import com.srm.platform.vendor.repository.PurchaseOrderMainRepository;
import com.srm.platform.vendor.repository.UnitRepository;
import com.srm.platform.vendor.repository.VenPriceAdjustDetailRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.u8api.ApiClient;
import com.srm.platform.vendor.utility.AccountSearchItem;
import com.srm.platform.vendor.utility.InventorySearchItem;
import com.srm.platform.vendor.utility.SearchItem;
import com.srm.platform.vendor.utility.UnitNode;
import com.srm.platform.vendor.utility.VenPriceDetailItem;

@RestController
@RequestMapping(path = "/api")
public class ApiController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ApiClient apiClient;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private UnitRepository unitRepository;

	@Autowired
	private PriceRepository priceRepository;

	@Autowired
	private VenPriceAdjustDetailRepository venPriceAdjustDetailRepository;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private PurchaseOrderMainRepository purchaseOrderMainRepository;

	@Autowired
	private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

	// 供应商管理列表查询
	@RequestMapping(value = "/vendor/batch_get", produces = "application/json")
	public Page<Vendor> vendor_list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);
		Page<Vendor> result = vendorRepository.findBySearchTerm(search, request);

		return result;
	}

	@RequestMapping(value = "/venpriceadjust/{mainId}/details", produces = "application/json")
	public List<VenPriceDetailItem> inventory_list_of_venpriceadjust_ajax(@PathVariable("mainId") String mainId) {
		List<VenPriceDetailItem> list = venPriceAdjustDetailRepository.findDetailsByMainId(mainId);

		return list;
	}

	@RequestMapping(value = "/purchaseorder/{code}/details", produces = "application/json")
	public List<PurchaseOrderDetail> inventory_list_of_purchaseorder_ajax(@PathVariable("code") String code) {
		List<PurchaseOrderDetail> list = purchaseOrderDetailRepository.findDetailsByCode(code);

		return list;
	}

	// 价格管理
	@RequestMapping(value = "/venpriceadjust/batch_get", produces = "application/json")
	public String venpriceadjust(@RequestParam Map<String, String> requestParams) {
		return apiClient.getBatchVenPriceAdjust(requestParams);
	}

	// 商品管理
	@RequestMapping(value = "/inventory/batch_get", produces = "application/json")
	public Page<Inventory> inventory(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);
		Page<Inventory> result = inventoryRepository.findBySearchTerm(search, request);

		return result;
	}

	@RequestMapping(value = "/inventory/select", produces = "application/json")
	public Page<InventorySearchItem> inventory_list_for_select_ajax(@RequestParam(value = "inv") String invName,
			@RequestParam(value = "vendor") String vendorCode) {
		PageRequest request = PageRequest.of(0, 15, Direction.ASC, "name");
		Page<InventorySearchItem> list = inventoryRepository.findSelectListBySearchTerm(vendorCode, invName, request);

		return list;
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

	@RequestMapping(value = "/account/{search}", produces = "application/json")
	public Page<AccountSearchItem> account_search(@PathVariable("search") String search) {
		PageRequest request = PageRequest.of(0, 15, Direction.ASC, "realname");

		return accountRepository.findForAutoComplete(search, request);
	}

	@RequestMapping(value = "/vendor/select", produces = "application/json")
	public Page<SearchItem> vendor_search(@RequestParam(value = "q") String search) {
		PageRequest request = PageRequest.of(0, 15, Direction.ASC, "name");

		return vendorRepository.findForSelect(search, request);
	}

	@RequestMapping(value = "/unit/tree", produces = "application/json")
	public UnitNode unit_tree() {
		List<Unit> units = unitRepository.findAll(Sort.by(Direction.ASC, "id"));

		UnitNode root = null, tempNode;
		Unit temp;
		for (int i = 0; i < units.size(); i++) {
			temp = units.get(i);
			tempNode = new UnitNode(temp.getId(), temp.getName(), temp.getParentId());
			if (i == 0) {
				root = tempNode;
			} else {
				root.addNode(tempNode);
			}
		}

		return root;
	}
}
