package com.srm.platform.vendor.controller;

import java.io.File;
import java.io.InputStream;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.model.PurchaseOrderDetail;
import com.srm.platform.vendor.model.PurchaseOrderMain;
import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.model.VenPriceAdjustDetail;
import com.srm.platform.vendor.model.VenPriceAdjustMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.PurchaseOrderDetailRepository;
import com.srm.platform.vendor.repository.PurchaseOrderMainRepository;
import com.srm.platform.vendor.repository.StatementDetailRepository;
import com.srm.platform.vendor.repository.StatementMainRepository;
import com.srm.platform.vendor.repository.VenPriceAdjustDetailRepository;
import com.srm.platform.vendor.repository.VenPriceAdjustMainRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.ExportShipForm;
import com.srm.platform.vendor.utility.PurchaseOrderDetailSearchItem;
import com.srm.platform.vendor.utility.PurchaseOrderSaveForm;
import com.srm.platform.vendor.utility.PurchaseOrderSearchItem;
import com.srm.platform.vendor.utility.StatementDetailItem;
import com.srm.platform.vendor.utility.StatementSaveForm;
import com.srm.platform.vendor.utility.StatementSearchItem;
import com.srm.platform.vendor.utility.UploadFileHelper;
import com.srm.platform.vendor.utility.VenPriceAdjustSearchItem;
import com.srm.platform.vendor.utility.VenPriceSaveForm;
import com.srm.platform.vendor.view.ExcelShipReportView;

@Controller
@RequestMapping(path = "/vendor")
@PreAuthorize("hasRole('ROLE_VENDOR')")
public class VendorController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private VenPriceAdjustMainRepository venPriceAdjustMainRepository;

	@Autowired
	private VenPriceAdjustDetailRepository venPriceAdjustDetailRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private PurchaseOrderMainRepository purchaseOrderMainRepository;

	@Autowired
	private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

	@Autowired
	private StatementMainRepository statementMainRepository;

	@Autowired
	private StatementDetailRepository statementDetailRepository;

	// Home
	@GetMapping({ "", "/" })
	public String home() {
		return "vendor/index";
	}

	// 价格管理->询价管理
	@GetMapping("/inquery")
	public String inquery() {
		return "buyer/inquery/index";
	}

	@GetMapping("/inquery/add")
	public String inquery_add(Model model, Principal principal) {
		VenPriceAdjustMain main = new VenPriceAdjustMain(accountRepository);
		Account account = accountRepository.findOneByUsername(principal.getName());
		main.setVendor(account.getVendor());
		model.addAttribute("main", main);

		return "buyer/inquery/edit";
	}

	@GetMapping("/inquery/{ccode}/edit")
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
				.findBySearchTerm(Constants.CREATE_TYPE_VENDOR, vendor, inventory, request);

		return result;
	}

	@PostMapping("/inquery/update")
	public @ResponseBody VenPriceAdjustMain inquery_update_ajax(VenPriceSaveForm form, Principal principal) {
		VenPriceAdjustMain venPriceAdjustMain = new VenPriceAdjustMain();
		venPriceAdjustMain.setCreatetype(Constants.CREATE_TYPE_VENDOR);
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

		venPriceAdjustMain.setIverifystate(form.getState());

		Account account = accountRepository.findOneByUsername(principal.getName());
		if (form.getState() == Constants.STATE_VERIFY) {
			venPriceAdjustMain.setVerifier(account);
			venPriceAdjustMain.setDverifydate(new Date());
		}
		if (form.getState() == Constants.STATE_PUBLISH) {
			venPriceAdjustMain.setPublisher(account);
			venPriceAdjustMain.setDpublishdate(new Date());
		}

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

		return venPriceAdjustMain;
	}

	// 价格管理->报价管理
	@GetMapping("/quote")
	public String quote() {
		return "buyer/quote/index";
	}

	@GetMapping("/quote/{ccode}/edit")
	public String quote_edit(@PathVariable("ccode") String ccode, Model model) {
		model.addAttribute("main", this.venPriceAdjustMainRepository.findOneByCcode(ccode));
		return "buyer/quote/edit";
	}

	@PostMapping("/quote/update")
	public @ResponseBody VenPriceAdjustMain quote_update_ajax(VenPriceSaveForm form, Principal principal) {
		String ccode = form.getCcode();
		Integer state = form.getState();

		VenPriceAdjustMain venPriceAdjustMain = venPriceAdjustMainRepository.findOneByCcode(ccode);
		venPriceAdjustMain.setIverifystate(state);

		if (state == Constants.STATE_CONFIRM || state == Constants.STATE_CANCEL || state == Constants.STATE_PASS) {
			Account account = accountRepository.findOneByUsername(principal.getName());
			venPriceAdjustMain.setReviewer(account);
			venPriceAdjustMain.setDreviewdate(new Date());
		}

		venPriceAdjustMain = venPriceAdjustMainRepository.save(venPriceAdjustMain);

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

		return venPriceAdjustMain;
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

		Account account = accountRepository.findOneByUsername(principal.getName());

		Page<VenPriceAdjustSearchItem> result = venPriceAdjustMainRepository.findBySearchTermForVendor(
				Constants.CREATE_TYPE_BUYER, account.getVendor().getCode(), inventory, request);

		return result;
	}

	// 订单管理->明细
	@GetMapping({ "/purchaseorder/{code}/edit" })
	public String purchaseorder_edit(@PathVariable("code") String code, Model model) {
		model.addAttribute("main", this.purchaseOrderMainRepository.findOneByCode(code));
		return "buyer/purchaseorder/edit";
	}

	@RequestMapping(value = "/purchaseorder/list", produces = "application/json")
	public @ResponseBody Page<PurchaseOrderSearchItem> purchaseorder_list_ajax(Principal principal,
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

		Account account = accountRepository.findOneByUsername(principal.getName());
		Page<PurchaseOrderSearchItem> result = purchaseOrderMainRepository.findBySearchTermForVendor(code,
				account.getVendor().getCode(), request);

		return result;
	}

	@PostMapping("/purchaseorder/update")
	public @ResponseBody PurchaseOrderMain purchaseorder_update_ajax(PurchaseOrderSaveForm form, Principal principal) {

		Account account = accountRepository.findOneByUsername(principal.getName());
		PurchaseOrderMain main = purchaseOrderMainRepository.findOneByCode(form.getCode());
		main.setSrmstate(form.getState());
		main.setReviewdate(new Date());
		main.setReviewer(account);
		purchaseOrderMainRepository.save(main);

		if (form.getTable() != null) {
			for (Map<String, String> item : form.getTable()) {
				PurchaseOrderDetail detail = purchaseOrderDetailRepository.findOneById(Long.parseLong(item.get("id")));
				if (item.get("confirmdate") != null && !item.get("confirmdate").isEmpty()) {
					SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
					try {
						detail.setConfirmdate(dateFormatter.parse(item.get("confirmdate")));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					detail.setConfirmdate(null);
				}

				detail.setConfirmnote(item.get("confirmnote"));
				purchaseOrderDetailRepository.save(detail);
			}
		}

		return main;
	}

	// 订单管理->订单确认
	@GetMapping({ "/purchaseorder" })
	public String purchaseorder() {
		return "buyer/purchaseorder/index";
	}

	// 订单管理->订单发货
	@GetMapping({ "/purchaseorder/ship", "/ship" })
	public String purchaseorder_ship() {
		return "vendor/purchaseorder/ship";
	}

	@RequestMapping(value = "/purchaseorder/ship/list", produces = "application/json")
	public @ResponseBody Page<PurchaseOrderDetailSearchItem> purchaseorder_ship_list_ajax(Principal principal,
			@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");
		String inventory = requestParams.getOrDefault("inventory", "");
		String code = requestParams.getOrDefault("code", "");

		switch (order) {
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

		Account account = accountRepository.findOneByUsername(principal.getName());
		Page<PurchaseOrderDetailSearchItem> result = purchaseOrderDetailRepository
				.findDetailsForShip(account.getVendor().getCode(), code, inventory, request);

		return result;
	}

	@PostMapping("/purchaseorder/ship/export")
	public ModelAndView purchaseorder_ship_export(@RequestParam(value = "export_data") String exportData,
			Principal principal) {

		logger.info(exportData);
		List<PurchaseOrderDetail> exportList = new ArrayList<>();
		Account account = accountRepository.findOneByUsername(principal.getName());
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			ExportShipForm shipForm = objectMapper.readValue(exportData, new TypeReference<ExportShipForm>() {
			});

			String query = "select a.* from purchase_order_detail a left join purchase_order_main b on a.code = b.code ";

			String subWhere = "";
			for (List<String> row : shipForm.getList()) {
				if (subWhere.length() != 0) {
					subWhere += " or ";
				}
				subWhere += "(a.code='" + row.get(0) + "' and a.rowno=" + row.get(1) + " and a.inventorycode='"
						+ row.get(2) + "') ";

			}
			String where = "where b.srmstate=2 and " + subWhere;
			query += where;

			String order = "code";
			switch (shipForm.getOrder()) {
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

			query += "order by " + order + " " + shipForm.getDir();
			Query q = em.createNativeQuery(query, PurchaseOrderDetail.class);

			exportList = q.getResultList();

			logger.info(shipForm.getList().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		return new ModelAndView(new ExcelShipReportView(), "exportList", exportList);
	}

	@RequestMapping("/purchaseorder/ship/import")
	public String purchaseorder_ship_import(@RequestParam("import_file") MultipartFile excelFile,
			HttpServletRequest request, RedirectAttributes redirectAttributes, Principal principal) {
		File file = UploadFileHelper.simpleUpload(excelFile, request, true, "uploads");
		logger.info(file.getAbsolutePath());
		List<ArrayList<String>> importList = new ArrayList<>();

		int importCount = 0;

		try {

			InputStream excelFileStream = excelFile.getInputStream();
			Workbook workbook = new HSSFWorkbook(excelFileStream);
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();

			int index = 0;
			while (iterator.hasNext()) {
				Row currentRow = iterator.next();
				index++;
				if (index == 1)
					continue;

				ArrayList<String> row = new ArrayList<>();
				List<Integer> valueList = Arrays.asList(1, 2, 4, 8);
				for (int column : valueList) {

					Cell currentCell = currentRow.getCell(column);
					if (currentCell == null) {
						row.add(null);
					} else if (currentCell.getCellTypeEnum() == CellType.STRING) {
						row.add(currentCell.getStringCellValue());
					} else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
						row.add(String.valueOf(currentCell.getNumericCellValue()));
					}
				}
				importList.add(row);
			}
			workbook.close();

			file.delete();

			Account account = accountRepository.findOneByUsername(principal.getName());
			Vendor vendor = account.getVendor();

			if (vendor != null) {

				for (ArrayList<String> row : importList) {
					PurchaseOrderMain main = purchaseOrderMainRepository.findOneByCode(row.get(0));
					if (main.getVendor().getCode().equals(vendor.getCode())) {
						PurchaseOrderDetail detail = new PurchaseOrderDetail();
						detail.setMain(main);
						detail.setRowno((int) Double.parseDouble(row.get(1)));
						Inventory inventory = inventoryRepository.findByCode(row.get(2));
						detail.setInventory(inventory);

						Example<PurchaseOrderDetail> example = Example.of(detail);
						Optional<PurchaseOrderDetail> result = purchaseOrderDetailRepository.findOne(example);
						if (result.isPresent()) {
							detail = result.get();
							if (row.get(3) != null) {
								detail.setShippedQuantity(detail.getShippedQuantity() + Float.parseFloat(row.get(3)));
								purchaseOrderDetailRepository.save(detail);
								importCount++;
							}
						}
					}
				}
			}

		} catch (

		Exception e) {
			e.printStackTrace();
		}

		redirectAttributes.addFlashAttribute("message", "成功导入" + importCount + "行数据！");
		return "redirect:/vendor/purchaseorder/ship";
	}

	// 对账单管理
	@GetMapping("/purinvoice")
	public String purinvoice() {
		return "vendor/purinvoice/index";
	}

	// 对账单管理->对账单明细
	@GetMapping("/purinvoice/{id}/edit")
	public String purinvoice_edit() {
		return "vendor/purinvoice/edit";
	}

	@GetMapping("/statement")
	public String statement() {
		return "buyer/statement/index";
	}

	@GetMapping({ "/statement/{code}/edit" })
	public String statement_edit(@PathVariable("code") String code, Model model) {
		model.addAttribute("main", this.statementMainRepository.findOneByCode(code));
		return "buyer/statement/edit";
	}

	@RequestMapping(value = "/statement/list", produces = "application/json")
	public @ResponseBody Page<StatementSearchItem> statement_list_ajax(@RequestParam Map<String, String> requestParams,
			Principal principal) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");
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

		Account account = accountRepository.findOneByUsername(principal.getName());

		Page<StatementSearchItem> result = statementMainRepository.findBySearchTermForVendor(code,
				account.getVendor().getCode(), request);

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
		if (result.isPresent()) {
			main = result.get();

			if (form.getInvoice_code() != null && !form.getInvoice_code().isEmpty()) {
				main.setInvoiceCode(form.getInvoice_code());
			} else {
				Account account = accountRepository.findOneByUsername(principal.getName());
				main.setVerifier(account);
				main.setVerifydate(new Date());
				main.setState(form.getState());
			}

			main = statementMainRepository.save(main);
		}

		return main;
	}

}
