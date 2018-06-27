package com.srm.platform.vendor.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.model.Price;
import com.srm.platform.vendor.model.VenPriceAdjustMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.PriceRepository;
import com.srm.platform.vendor.repository.VenPriceAdjustDetailRepository;
import com.srm.platform.vendor.repository.VenPriceAdjustMainRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.utility.VenPriceAdjustSearchItem;

@Controller
@RequestMapping(path = "/buyer")
@PreAuthorize("hasRole('ROLE_BUYER')")
public class BuyerController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private PriceRepository priceRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private VenPriceAdjustMainRepository venPriceAdjustMainRepository;

	@Autowired
	private VenPriceAdjustDetailRepository venPriceAdjustDetailRepository;

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
	public String inquery_add(Model model) {
		VenPriceAdjustMain main = new VenPriceAdjustMain(accountRepository);
		model.addAttribute("main", main);
		return "buyer/inquery/edit";
	}

	// 价格管理->询价管理->新建
	@GetMapping({ "/inquery/{ccode}/edit" })
	public String inquery_edit(@PathVariable("ccode") String ccode, Model model) {
		model.addAttribute("main", this.venPriceAdjustMainRepository.findOneByCcode(ccode));
		return "buyer/inquery/edit";
	}

	public void test(@RequestParam Map<String, String> requestParams) {
		Query q = em.createNativeQuery("select id from users where username = :username");
		q.setParameter("username", "lt");
		List<String> values = q.getResultList();

	}

	@RequestMapping(value = "/inquery/list", produces = "application/json")
	public @ResponseBody Page<VenPriceAdjustSearchItem> inquery_list_ajax(
			@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");
		String vendor = requestParams.getOrDefault("vendor", "");
		String stateStr = requestParams.getOrDefault("state", "0");
		String inventory = requestParams.getOrDefault("inventory", "");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);

		Integer state = Integer.parseInt(stateStr);
		Date startDate = null, endDate = null;
		try {
			if (start_date != null && !start_date.isEmpty())
				startDate = dateFormatter.parse(start_date);
			if (end_date != null && !end_date.isEmpty()) {
				endDate = dateFormatter.parse(end_date);
				Calendar cal = Calendar.getInstance();
				cal.setTime(endDate);
				cal.add(Calendar.DATE, 1);
				endDate = cal.getTime();
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		switch (order) {
		case "vendorname":
			order = "c.name";
			break;
		case "vendorcode":
			order = "c.code";
			break;
		case "verifiername":
			order = "f.realname";
			break;
		case "makername":
			order = "e.realname";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		Page<VenPriceAdjustSearchItem> result = venPriceAdjustMainRepository.findBySearchTerm(vendor, inventory,
				request);

		return result;
	}

	@PostMapping("/inquery/update")
	public @ResponseBody VenPriceAdjustMain inquery_update_ajax(@RequestParam Map<String, String> requestParams) {
		String ccode = requestParams.get("ccode");
		String vendor = requestParams.get("vendor");
		String tax_rate = requestParams.get("tax_rate");
		String state = requestParams.get("state");
		String start_date = requestParams.get("start_date");
		String end_date = requestParams.get("end_date");
		String type = requestParams.get("type");
		String provide_type = requestParams.get("provide_type");
		String maker = requestParams.get("maker");
		String make_date = requestParams.get("make_date");

		VenPriceAdjustMain venPriceAdjustMain = new VenPriceAdjustMain();
		venPriceAdjustMain.setCcode(ccode);

		Example<VenPriceAdjustMain> example = Example.of(venPriceAdjustMain);
		Optional<VenPriceAdjustMain> result = venPriceAdjustMainRepository.findOne(example);
		if (result.isPresent())
			venPriceAdjustMain = result.get();

		venPriceAdjustMain.setType(Integer.parseInt(type));
		venPriceAdjustMain.setIsupplytype(Integer.parseInt(provide_type));
		venPriceAdjustMain.setItaxrate(Integer.parseInt(tax_rate));
		venPriceAdjustMain.setIverifystate(Integer.parseInt(state));

		try {
			venPriceAdjustMain.setDstartdate(dateFormatter.parse(start_date));
			venPriceAdjustMain.setDenddate(dateFormatter.parse(end_date));
			venPriceAdjustMain.setDmakedate(dateFormatter.parse(make_date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		venPriceAdjustMain.setVendor(vendorRepository.findOneByCode(vendor));
		venPriceAdjustMain.setMaker(accountRepository.findOneById(Long.parseLong(maker)));

		venPriceAdjustMain = venPriceAdjustMainRepository.save(venPriceAdjustMain);

		return venPriceAdjustMain;
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
