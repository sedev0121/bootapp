package com.srm.platform.vendor.controller;

import java.security.Principal;
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

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.model.Price;
import com.srm.platform.vendor.model.PurchaseOrderDetail;
import com.srm.platform.vendor.model.PurchaseOrderMain;
import com.srm.platform.vendor.model.StatementDetail;
import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.model.VenPriceAdjustDetail;
import com.srm.platform.vendor.model.VenPriceAdjustMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.PriceRepository;
import com.srm.platform.vendor.repository.PurchaseInDetailRepository;
import com.srm.platform.vendor.repository.PurchaseInMainRepository;
import com.srm.platform.vendor.repository.PurchaseOrderDetailRepository;
import com.srm.platform.vendor.repository.PurchaseOrderMainRepository;
import com.srm.platform.vendor.repository.StatementDetailRepository;
import com.srm.platform.vendor.repository.StatementMainRepository;
import com.srm.platform.vendor.repository.VenPriceAdjustDetailRepository;
import com.srm.platform.vendor.repository.VenPriceAdjustMainRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.PurchaseInDetailItem;
import com.srm.platform.vendor.utility.PurchaseInSearchItem;
import com.srm.platform.vendor.utility.PurchaseOrderDetailSearchItem;
import com.srm.platform.vendor.utility.PurchaseOrderSaveForm;
import com.srm.platform.vendor.utility.PurchaseOrderSearchItem;
import com.srm.platform.vendor.utility.StatementDetailItem;
import com.srm.platform.vendor.utility.StatementSaveForm;
import com.srm.platform.vendor.utility.StatementSearchItem;
import com.srm.platform.vendor.utility.VenPriceAdjustSearchItem;
import com.srm.platform.vendor.utility.VenPriceSaveForm;

@Controller
@RequestMapping(path = "/buyer")
@PreAuthorize("hasRole('ROLE_BUYER')")
public class BuyerController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private PurchaseOrderMainRepository purchaseOrderMainRepository;

	@Autowired
	private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

	@Autowired
	private PurchaseInMainRepository purchaseInMainRepository;

	@Autowired
	private PurchaseInDetailRepository purchaseInDetailRepository;

	@Autowired
	private PriceRepository priceRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private VenPriceAdjustMainRepository venPriceAdjustMainRepository;

	@Autowired
	private VenPriceAdjustDetailRepository venPriceAdjustDetailRepository;

	@Autowired
	private StatementMainRepository statementMainRepository;

	@Autowired
	private StatementDetailRepository statementDetailRepository;

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

	@GetMapping("/inquery/{ccode}/delete")
	public @ResponseBody Boolean inquery_delete(@PathVariable("ccode") String ccode) {
		VenPriceAdjustMain main = venPriceAdjustMainRepository.findOneByCcode(ccode);
		if (main != null)
			venPriceAdjustMainRepository.delete(main);
		return true;
	}

	@GetMapping({ "/test" })
	public void test(@RequestParam Map<String, String> requestParams) {
		Query q = em.createNativeQuery("select realname from account where username='lisisi'");
		// q.setParameter("username", "lisisi");
		List<String> values = q.getResultList();
		logger.info(values.toString());

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
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			if (start_date != null && !start_date.isEmpty())
				startDate = dateFormatter.parse(start_date);
			if (end_date != null && !end_date.isEmpty()) {
				dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
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

		Page<VenPriceAdjustSearchItem> result = venPriceAdjustMainRepository
				.findBySearchTerm(Constants.CREATE_TYPE_BUYER, vendor, inventory, request);

		return result;
	}

	@PostMapping("/inquery/update")
	public @ResponseBody VenPriceAdjustMain inquery_update_ajax(VenPriceSaveForm form, Principal principal) {
		VenPriceAdjustMain venPriceAdjustMain = new VenPriceAdjustMain();
		venPriceAdjustMain.setCreatetype(Constants.CREATE_TYPE_BUYER);
		venPriceAdjustMain.setCcode(form.getCcode());

		Example<VenPriceAdjustMain> example = Example.of(venPriceAdjustMain);
		Optional<VenPriceAdjustMain> result = venPriceAdjustMainRepository.findOne(example);
		if (result.isPresent())
			venPriceAdjustMain = result.get();

		if ((venPriceAdjustMain.getIverifystate() == null
				|| venPriceAdjustMain.getIverifystate() == Constants.STATE_NEW)
				&& form.getState() <= Constants.STATE_SUBMIT) {
			venPriceAdjustMain.setType(form.getType());
			venPriceAdjustMain.setIsupplytype(form.getProvide_type());
			venPriceAdjustMain.setItaxrate(form.getTax_rate());

			venPriceAdjustMain.setDstartdate(form.getStart_date());
			venPriceAdjustMain.setDenddate(form.getEnd_date());
			venPriceAdjustMain.setDmakedate(form.getMake_date());
			venPriceAdjustMain.setVendor(vendorRepository.findOneByCode(form.getVendor()));
			venPriceAdjustMain.setMaker(accountRepository.findOneById(form.getMaker()));
		}

		Account account = accountRepository.findOneByUsername(principal.getName());

		if (form.getState() == Constants.STATE_VERIFY || (venPriceAdjustMain.getIverifystate() != null
				&& venPriceAdjustMain.getIverifystate() == Constants.STATE_PASS
				&& form.getState() == Constants.STATE_CANCEL)) {
			venPriceAdjustMain.setVerifier(account);
			venPriceAdjustMain.setDverifydate(new Date());
		}
		if (form.getState() == Constants.STATE_PUBLISH) {
			venPriceAdjustMain.setPublisher(account);
			venPriceAdjustMain.setDpublishdate(new Date());
		}

		venPriceAdjustMain.setIverifystate(form.getState());
		venPriceAdjustMain = venPriceAdjustMainRepository.save(venPriceAdjustMain);

		if (form.getState() <= Constants.STATE_SUBMIT && form.getTable() != null) {
			venPriceAdjustDetailRepository
					.deleteInBatch(venPriceAdjustDetailRepository.findByMainId(venPriceAdjustMain.getCcode()));

			for (Map<String, String> row : form.getTable()) {
				VenPriceAdjustDetail detail = new VenPriceAdjustDetail();
				detail.setMain(venPriceAdjustMain);
				detail.setInventory(inventoryRepository.findByCode(row.get("cinvcode")));
				detail.setCbodymemo(row.get("cbodymemo"));
				detail.setIunitprice(Float.parseFloat(row.get("iunitprice")));
				String max = row.get("fmaxquantity");
				String min = row.get("fminquantity");
				if (max != null && !max.isEmpty())
					detail.setFmaxquantity(Float.parseFloat(max));

				if (min != null && !min.isEmpty())
					detail.setFminquantity(Float.parseFloat(min));

				if (row.get("ivalid") != null && !row.get("ivalid").isEmpty())
					detail.setIvalid(Integer.parseInt(row.get("ivalid")));
				try {
					String startDateStr = row.get("dstartdate");
					String endDateStr = row.get("denddate");
					SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
					if (startDateStr != null && !startDateStr.isEmpty()) {
						detail.setDstartdate(dateFormatter.parse(startDateStr));
					}

					if (endDateStr != null && !endDateStr.isEmpty()) {
						dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
						detail.setDenddate(dateFormatter.parse(endDateStr));
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				detail.setItaxrate(Float.parseFloat(row.get("itaxrate")));
				detail.setItaxunitprice(Float.parseFloat(row.get("itaxunitprice")));

				venPriceAdjustDetailRepository.save(detail);
			}
		}

		if (form.getState() == Constants.STATE_PUBLISH) {
			updatePriceTable(venPriceAdjustMain);
		}

		return venPriceAdjustMain;
	}

	private void updatePriceTable(VenPriceAdjustMain venPriceAdjustMain) {

		List<VenPriceAdjustDetail> list = venPriceAdjustDetailRepository.findByMainId(venPriceAdjustMain.getCcode());
		for (VenPriceAdjustDetail item : list) {
			Price price = new Price();
			price.setVendor(venPriceAdjustMain.getVendor());
			price.setInventory(item.getInventory());
			price.setCreateby(venPriceAdjustMain.getMaker().getId());
			price.setCreatedate(venPriceAdjustMain.getDmakedate());
			price.setFavdate(venPriceAdjustMain.getDstartdate());
			price.setFcanceldate(venPriceAdjustMain.getDenddate());
			price.setFnote(item.getCbodymemo());
			price.setFprice(item.getIunitprice());
			price.setFtax((float) item.getItaxrate());
			price.setFtaxprice(item.getItaxunitprice());
			price.setFisoutside(false);
			price.setFcheckdate(new Date());
			price.setDescription(item.getInventory().getSpecs());
			price.setFauxunit(item.getInventory().getPuunitName());
			priceRepository.save(price);
		}

	}

	// 价格管理->报价管理
	@GetMapping("/quote")
	public String quote() {
		return "buyer/quote/index";
	}

	// 价格管理->报价管理->修改
	@GetMapping("/quote/{ccode}/edit")
	public String quote_edit(@PathVariable("ccode") String ccode, Model model) {
		model.addAttribute("main", this.venPriceAdjustMainRepository.findOneByCcode(ccode));
		return "buyer/quote/edit";
	}

	@RequestMapping(value = "/quote/list", produces = "application/json")
	public @ResponseBody Page<VenPriceAdjustSearchItem> quote_list_ajax(Principal principal,
			@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");

		String stateStr = requestParams.getOrDefault("state", "0");
		String inventory = requestParams.getOrDefault("inventory", "");
		String vendor = requestParams.getOrDefault("vendor", "");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);

		Integer state = Integer.parseInt(stateStr);
		Date startDate = null, endDate = null;
		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			if (start_date != null && !start_date.isEmpty())
				startDate = dateFormatter.parse(start_date);
			if (end_date != null && !end_date.isEmpty()) {
				dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
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

		Page<VenPriceAdjustSearchItem> result = venPriceAdjustMainRepository
				.findBySearchTermForBuyer(Constants.CREATE_TYPE_VENDOR, vendor, inventory, request);

		return result;
	}

	@PostMapping("/quote/update")
	public @ResponseBody VenPriceAdjustMain quote_update_ajax(VenPriceSaveForm form, Principal principal) {
		String ccode = form.getCcode();
		Integer state = form.getState();

		VenPriceAdjustMain venPriceAdjustMain = venPriceAdjustMainRepository.findOneByCcode(ccode);

		Account account = accountRepository.findOneByUsername(principal.getName());
		if (state == Constants.STATE_VERIFY
				|| (venPriceAdjustMain.getIverifystate() == Constants.STATE_PASS && state == Constants.STATE_CANCEL)) {
			venPriceAdjustMain.setVerifier(account);
			venPriceAdjustMain.setDverifydate(new Date());
		}
		if (state == Constants.STATE_PUBLISH) {
			venPriceAdjustMain.setPublisher(account);
			venPriceAdjustMain.setDpublishdate(new Date());
		}

		venPriceAdjustMain.setIverifystate(state);
		venPriceAdjustMain = venPriceAdjustMainRepository.save(venPriceAdjustMain);

		if (state == Constants.STATE_PASS) {
			if (form.getTable() != null) {
				for (Map<String, String> row : form.getTable()) {
					Optional<VenPriceAdjustDetail> result = venPriceAdjustDetailRepository
							.findById(Long.parseLong(row.get("id")));
					if (result.isPresent()) {
						VenPriceAdjustDetail detail = result.get();
						detail.setIunitprice(Float.parseFloat(row.get("iunitprice")));
						detail.setItaxunitprice(Float.parseFloat(row.get("itaxunitprice")));
						detail.setCbodymemo(row.get("cbodymemo"));
						venPriceAdjustDetailRepository.save(detail);
					}

				}
			}
		}

		if (form.getState() == Constants.STATE_PUBLISH) {
			updatePriceTable(venPriceAdjustMain);
		}

		return venPriceAdjustMain;
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

	// 供应商管理列表查询
	@RequestMapping(value = "/price/list", produces = "application/json")
	public @ResponseBody Page<Price> price_list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search_vendor = requestParams.getOrDefault("vendor", "");
		String search_inventory = requestParams.getOrDefault("inventory", "");
		String startDate = requestParams.getOrDefault("start", "");
		String endDate = requestParams.getOrDefault("end", "");

		if (order.equals("vendor.name")) {
			order = "b.name";
		}

		if (order.equals("inventory.name")) {
			order = "c.name";
		}

		logger.info(startDate + " ~ " + endDate);
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);
		Page<Price> result = priceRepository.findBySearchTerm(search_vendor, search_inventory, startDate, endDate,
				request);

		return result;
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
	@GetMapping({ "/purchaseorder" })
	public String purchaseorder() {
		return "buyer/purchaseorder/index";
	}

	// 订单管理->明细
	@GetMapping({ "/purchaseorder/{code}/edit" })
	public String purchaseorder_edit(@PathVariable("code") String code, Model model) {
		model.addAttribute("main", this.purchaseOrderMainRepository.findOneByCode(code));
		return "buyer/purchaseorder/edit";
	}

	@RequestMapping(value = "/purchaseorder/list", produces = "application/json")
	public @ResponseBody Page<PurchaseOrderSearchItem> purchaseorder_list_ajax(
			@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");
		String vendor = requestParams.getOrDefault("vendor", "");
		String state = requestParams.getOrDefault("state", "0");
		String code = requestParams.getOrDefault("code", "");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);

		Date startDate = null, endDate = null;
		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			if (start_date != null && !start_date.isEmpty())
				startDate = dateFormatter.parse(start_date);
			if (end_date != null && !end_date.isEmpty()) {
				dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
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
			order = "b.name";
			break;
		case "deployername":
			order = "c.realname";
			break;
		case "reviewername":
			order = "d.realname";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		Page<PurchaseOrderSearchItem> result = purchaseOrderMainRepository.findBySearchTerm(code, vendor, request);

		return result;
	}

	@PostMapping("/purchaseorder/update")
	public @ResponseBody PurchaseOrderMain purchaseorder_update_ajax(PurchaseOrderSaveForm form, Principal principal) {

		Account account = accountRepository.findOneByUsername(principal.getName());
		PurchaseOrderMain main = purchaseOrderMainRepository.findOneByCode(form.getCode());
		main.setSrmstate(form.getState());
		main.setDeploydate(new Date());
		main.setDeployer(account);
		purchaseOrderMainRepository.save(main);

		if (form.getTable() != null) {
			for (Map<String, String> item : form.getTable()) {
				PurchaseOrderDetail detail = purchaseOrderDetailRepository.findOneById(Long.parseLong(item.get("id")));
				if (item.get("prepaymoney") != null && !item.get("prepaymoney").isEmpty())
					detail.setPrepaymoney(Float.parseFloat(item.get("prepaymoney")));
				else
					detail.setPrepaymoney(null);

				detail.setArrivenote(item.get("arrivenote"));
				purchaseOrderDetailRepository.save(detail);
			}
		}

		return main;
	}

	// 订单管理->订单发货
	@GetMapping({ "/ship" })
	public String ship() {
		return "buyer/ship/index";
	}

	@RequestMapping(value = "/ship/list", produces = "application/json")
	public @ResponseBody Page<PurchaseOrderDetailSearchItem> ship_list_ajax(Principal principal,
			@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");
		String vendor = requestParams.getOrDefault("vendor", "");
		String inventory = requestParams.getOrDefault("inventory", "");
		String code = requestParams.getOrDefault("code", "");

		switch (order) {
		case "remain_quantity":
			order = "remain_quantity";
			break;

		case "vendorname":
			order = "d.name";
			break;
		case "vendorcode":
			order = "d.code";
			break;
		case "inventoryname":
			order = "c.name";
			break;
		case "specs":
			order = "c.specs";
			break;
		case "unitname":
			order = "c.puunit_name";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order, "rowno");

		Page<PurchaseOrderDetailSearchItem> result = purchaseOrderDetailRepository.findDetailsForBuyerShip(vendor, code,
				inventory, request);

		return result;
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

	// 订单管理->明细
	@GetMapping({ "/purchasein/{code}/edit" })
	public String purchasein_edit(@PathVariable("code") String code, Model model) {
		model.addAttribute("main", this.purchaseInMainRepository.findOneByCode(code));
		return "buyer/purchasein/edit";
	}

	@RequestMapping(value = "/purchasein/list", produces = "application/json")
	public @ResponseBody Page<PurchaseInSearchItem> purchasein_list_ajax(
			@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");
		String vendor = requestParams.getOrDefault("vendor", "");
		String state = requestParams.getOrDefault("state", "0");
		String code = requestParams.getOrDefault("code", "");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);

		Date startDate = null, endDate = null;
		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			if (start_date != null && !start_date.isEmpty())
				startDate = dateFormatter.parse(start_date);
			if (end_date != null && !end_date.isEmpty()) {
				dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
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
			order = "b.name";
			break;
		case "deployername":
			order = "c.realname";
			break;
		case "reviewername":
			order = "d.realname";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		Page<PurchaseInSearchItem> result = purchaseInMainRepository.findBySearchTerm(code, vendor, request);

		return result;
	}

	@RequestMapping(value = "/purchasein/{code}/details", produces = "application/json")
	public @ResponseBody List<PurchaseInDetailItem> purchasein_detail_list_ajax(@PathVariable("code") String code) {
		List<PurchaseInDetailItem> list = purchaseInDetailRepository.findDetailsByCode(code);

		return list;
	}

	@RequestMapping(value = "/purchasein/select", produces = "application/json")
	public @ResponseBody Page<PurchaseInDetailItem> purchasein_select_ajax(
			@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "code");
		String dir = requestParams.getOrDefault("dir", "asc");
		String vendor = requestParams.getOrDefault("vendor", "");
		String code = requestParams.getOrDefault("code", "");
		String dateStr = requestParams.getOrDefault("date", null);
		String inventory = requestParams.getOrDefault("inventory", "");

		Date date;
		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			if (dateStr != null && !dateStr.isEmpty())
				date = dateFormatter.parse(dateStr);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		switch (order) {
		case "purchase_in_detail_id":
			order = "id";
			break;
		case "date":
			order = "b.date";
			break;
		case "vendorname":
			order = "b.name";
			break;
		case "deployername":
			order = "c.realname";
			break;
		case "reviewername":
			order = "d.realname";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order, "rowno");

		Page<PurchaseInDetailItem> result = purchaseInDetailRepository.findForSelect(vendor, code, inventory, request);

		return result;
	}

	@GetMapping("/statement")
	public String statement() {
		return "buyer/statement/index";
	}

	@GetMapping({ "/statement/add" })
	public String statement_add(Model model) {
		StatementMain main = new StatementMain(accountRepository);
		model.addAttribute("main", main);
		return "buyer/statement/edit";
	}

	@GetMapping({ "/statement/{code}/edit" })
	public String statement_edit(@PathVariable("code") String code, Model model) {
		model.addAttribute("main", this.statementMainRepository.findOneByCode(code));
		return "buyer/statement/edit";
	}

	@GetMapping("/statement/{code}/delete")
	public @ResponseBody Boolean statement_delete(@PathVariable("code") String code) {
		StatementMain main = statementMainRepository.findOneByCode(code);
		if (main != null)
			statementMainRepository.delete(main);
		return true;
	}

	@RequestMapping(value = "/statement/list", produces = "application/json")
	public @ResponseBody Page<StatementSearchItem> statement_list_ajax(
			@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");
		String vendor = requestParams.getOrDefault("vendor", "");
		String stateStr = requestParams.getOrDefault("state", "0");
		String code = requestParams.getOrDefault("code", "");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);

		Integer state = Integer.parseInt(stateStr);
		Date startDate = null, endDate = null;
		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			if (start_date != null && !start_date.isEmpty())
				startDate = dateFormatter.parse(start_date);
			if (end_date != null && !end_date.isEmpty()) {
				dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
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
		case "vendor_name":
			order = "b.name";
			break;
		case "vendor_code":
			order = "b.code";
			break;
		case "verifier":
			order = "d.realname";
			break;
		case "maker":
			order = "c.realname";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		Page<StatementSearchItem> result = statementMainRepository.findBySearchTerm(code, vendor, request);

		return result;
	}

	@RequestMapping(value = "/statement/{code}/details", produces = "application/json")
	public @ResponseBody List<StatementDetailItem> statement_detail_list_ajax(@PathVariable("code") String code) {
		List<StatementDetailItem> list = statementDetailRepository.findDetailsByCode(code);

		return list;
	}

	@PostMapping("/statement/update")
	public @ResponseBody StatementMain statement_update_ajax(StatementSaveForm form, Principal principal) {
		StatementMain main = new StatementMain();
		main.setCode(form.getCode());

		Example<StatementMain> example = Example.of(main);
		Optional<StatementMain> result = statementMainRepository.findOne(example);
		if (result.isPresent())
			main = result.get();

		if ((main.getState() == null || main.getState() == Constants.STATEMENT_STATE_NEW)
				&& form.getState() <= Constants.STATEMENT_STATE_SUBMIT) {

			main.setMakedate(new Date());
			main.setVendor(vendorRepository.findOneByCode(form.getVendor()));
			main.setMaker(accountRepository.findOneById(form.getMaker()));
		} else if (form.getState() >= Constants.STATEMENT_STATE_CONFIRM) {
			Account account = accountRepository.findOneByUsername(principal.getName());
			main.setVerifier(account);
			main.setVerifydate(new Date());
		}

		main.setRemark(form.getRemark());
		main.setState(form.getState());
		main = statementMainRepository.save(main);

		if (form.getState() <= Constants.STATEMENT_STATE_SUBMIT && form.getTable() != null) {
			statementDetailRepository.deleteInBatch(statementDetailRepository.findByCode(main.getCode()));

			for (Map<String, String> row : form.getTable()) {
				StatementDetail detail = new StatementDetail();
				detail.setCode(main.getCode());
				detail.setPurchaseInDetailId(Long.parseLong(row.get("purchase_in_detail_id")));

				String closedQuantity = row.get("closed_quantity");
				String closedPrice = row.get("closed_price");
				String closedMoney = row.get("closed_money");
				String closedTaxPrice = row.get("closed_tax_price");
				String closedTaxMoney = row.get("closed_tax_money");

				if (closedQuantity != null && !closedQuantity.isEmpty())
					detail.setClosedQuantity(Float.parseFloat(closedQuantity));

				if (closedPrice != null && !closedPrice.isEmpty())
					detail.setClosedPrice(Float.parseFloat(closedPrice));

				if (closedMoney != null && !closedMoney.isEmpty())
					detail.setClosedMoney(Float.parseFloat(closedMoney));

				if (closedTaxPrice != null && !closedTaxPrice.isEmpty())
					detail.setClosedTaxPrice(Float.parseFloat(closedTaxPrice));

				if (closedTaxMoney != null && !closedTaxMoney.isEmpty())
					detail.setClosedTaxMoney(Float.parseFloat(closedTaxMoney));

				statementDetailRepository.save(detail);
			}
		}

		return main;
	}

}
