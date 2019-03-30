package com.srm.platform.vendor.controller;

import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.srm.platform.vendor.model.PurchaseOrderDetail;
import com.srm.platform.vendor.model.PurchaseOrderMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.PurchaseOrderDetailRepository;
import com.srm.platform.vendor.repository.PurchaseOrderMainRepository;
import com.srm.platform.vendor.saveform.ExportShipForm;
import com.srm.platform.vendor.searchitem.InquerySearchResult;
import com.srm.platform.vendor.searchitem.PurchaseOrderDetailSearchResult;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.UploadFileHelper;
import com.srm.platform.vendor.utility.Utils;
import com.srm.platform.vendor.view.ExcelShipReportView;

@Controller
@RequestMapping(path = "/ship")
@PreAuthorize("hasRole('ROLE_VENDOR') or hasAuthority('出货看板-查看列表')")
public class ShipController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PurchaseOrderMainRepository purchaseOrderMainRepository;

	@Autowired
	private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

	// 查询列表
	@PreAuthorize("hasRole('ROLE_VENDOR')")
	@GetMapping({ "/index" })
	public String index() {
		return "ship/index";
	}

	// 出货看板
	@GetMapping({ "/view" })
	public String view() {
		return "ship/index";
	}

	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<PurchaseOrderDetailSearchResult> list_ajax(Principal principal,
			@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");
		String vendorStr = requestParams.getOrDefault("vendor", "");
		String inventory = requestParams.getOrDefault("inventory", "");
		String code = requestParams.getOrDefault("code", "");
		String arrive_date = requestParams.getOrDefault("arrive_date", null);
		String confirm_date = requestParams.getOrDefault("confirm_date", null);

		Date arriveDate = Utils.parseDate(arrive_date);
		Date confirmDate = Utils.parseDate(confirm_date);

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
		case "lastshipdate":
			order = "a.last_ship_date";
			break;
		case "specs":
			order = "c.specs";
			break;
		case "unitname":
			order = "e.name";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order, "rowno");

		String selectQuery = "select a.*, d.code vendorcode, (a.quantity-ifnull(a.shipped_quantity,0)) remain_quantity, d.name vendorname, c.name inventoryname, c.specs, e.name unitname ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "from purchase_order_detail a left join purchase_order_main b on a.code = b.code "
				+ "left join inventory c on a.inventorycode=c.code left join vendor d on b.vencode=d.code "
				+ "left join measurement_unit e on c.main_measure=e.code where b.srmstate=2 ";

		Map<String, Object> params = new HashMap<>();

		if (!code.trim().isEmpty()) {
			bodyQuery += " and a.code like CONCAT('%',:code, '%') ";
			params.put("code", code.trim());
		}

		if (isVendor()) {
			Vendor vendor = this.getLoginAccount().getVendor();
			vendorStr = vendor == null ? "0" : vendor.getCode();
			bodyQuery += " and d.code= :vendor";
			params.put("vendor", vendorStr);
		} else {
//			List<String> vendorList = this.getVendorListOfUser();
//			
//			if (vendorList.size() == 0) {
//				return new PageImpl<PurchaseOrderDetailSearchResult>(new ArrayList(), request, 0);
//			}
//			
//			bodyQuery += " and d.code in :vendorList";
//			params.put("vendorList", vendorList);
//			if (!vendorStr.trim().isEmpty()) {
//				bodyQuery += " and (d.name like CONCAT('%',:vendor, '%') or d.code like CONCAT('%',:vendor, '%')) ";
//				params.put("vendor", vendorStr.trim());
//			}

		}

		if (!inventory.trim().isEmpty()) {
			bodyQuery += " and (c.name like CONCAT('%',:inventory, '%') or c.code like CONCAT('%',:inventory, '%')) ";
			params.put("inventory", inventory.trim());
		}

		if (arriveDate != null) {
			bodyQuery += " and a.arrivedate=:arrivedate";
			params.put("arrivedate", arriveDate);
		}

		if (confirmDate != null) {
			bodyQuery += " and a.confirmdate=:confirmdate";
			params.put("confirmdate", confirmDate);
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "PurchaseOrderDetailSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<PurchaseOrderDetailSearchResult>(list, request, totalCount.longValue());

	}

	@PostMapping("/export")
	public ModelAndView export_file(@RequestParam(value = "export_data") String exportData, Principal principal) {

		List<PurchaseOrderDetail> exportList = new ArrayList<>();

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			ExportShipForm shipForm = objectMapper.readValue(exportData, new TypeReference<ExportShipForm>() {
			});

			String query = "select a.*, d.code vendorcode, (a.quantity-ifnull(a.shipped_quantity,0)) remain_quantity, d.name vendorname, c.name inventoryname, c.specs, e.name unitname "
					+ "from purchase_order_detail a left join purchase_order_main b on a.code = b.code "
					+ "left join inventory c on a.inventorycode=c.code left join vendor d on b.vencode=d.code "
					+ "left join measurement_unit e on c.main_measure=e.code where a.id in :idList ";

			List<Long> idList = shipForm.getList();

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
			q.setParameter("idList", idList);

			exportList = q.getResultList();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		return new ModelAndView(new ExcelShipReportView(), "exportList", exportList);
	}

	@Transactional
	@RequestMapping("/import")
	public String import_file(@RequestParam("import_file") MultipartFile excelFile, 
			RedirectAttributes redirectAttributes, Principal principal) {
		File file = UploadFileHelper.simpleUpload(excelFile, true, Constants.PATH_UPLOADS_SHIP);

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
				List<Integer> valueList = Arrays.asList(1, 11, 16);
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

			if (vendor != null && importList != null) {

				for (ArrayList<String> row : importList) {

					PurchaseOrderMain main = purchaseOrderMainRepository.findOneByCode(row.get(0));
					if (main.getVendor().getCode().equals(vendor.getCode())) {

						PurchaseOrderDetail detail = purchaseOrderDetailRepository
								.findOneById((long) Float.parseFloat(row.get(2)));

						if (detail != null) {
							if (row.get(1) != null) {
								float quantity = detail.getShippedQuantity() == null ? 0 : detail.getShippedQuantity();

								detail.setShippedQuantity(quantity + Float.parseFloat(row.get(1)));
								detail.setLastShipDate(new Date());
								detail = purchaseOrderDetailRepository.save(detail);

								List<Account> toList = new ArrayList<>();
								toList.add(main.getDeployer());
								String title = String.format("订单【%s】已由【%s】订单出货，请及时查阅和处理！", main.getCode(),
										account.getRealname());
								this.sendmessage(title, toList, String.format("/purchaseorder/%s/read", main.getCode()));

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
		return "redirect:/ship/index";
	}

	@RequestMapping("/save")
	@Transactional
	public @ResponseBody Boolean save(@RequestParam Map<String, String> requestParams) {

		for (Entry<String, String> entry : requestParams.entrySet()) {
			if (entry.getKey().equals("_csrf"))
				continue;
			PurchaseOrderDetail detail = purchaseOrderDetailRepository.findOneById(Long.valueOf(entry.getKey()));
			if (detail != null && entry.getValue() != null && !entry.getValue().isEmpty()) {
				float quantity = detail.getShippedQuantity() == null ? 0 : detail.getShippedQuantity();
				detail.setShippedQuantity(quantity + Long.valueOf(entry.getValue()));
				detail.setLastShipDate(new Date());
				purchaseOrderDetailRepository.save(detail);

				PurchaseOrderMain main = purchaseOrderMainRepository.findOneByCode(detail.getMain().getCode());
				List<Account> toList = new ArrayList<>();
				toList.add(main.getDeployer());
				String title = String.format("订单【%s】已由【%s】订单出货，请及时查阅和处理！", main.getCode(),
						this.getLoginAccount().getRealname());
				this.sendmessage(title, toList, String.format("/purchaseorder/%s/read", main.getCode()));
			}
		}

		return true;
	}
}
