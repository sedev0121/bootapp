package com.srm.platform.vendor.controller;

import java.io.File;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
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
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.PurchaseOrderDetailRepository;
import com.srm.platform.vendor.repository.PurchaseOrderMainRepository;
import com.srm.platform.vendor.utility.ExportShipForm;
import com.srm.platform.vendor.utility.PurchaseOrderDetailSearchItem;
import com.srm.platform.vendor.utility.UploadFileHelper;
import com.srm.platform.vendor.view.ExcelShipReportView;

@Controller
@RequestMapping(path = "/ship")

public class ShipController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private PurchaseOrderMainRepository purchaseOrderMainRepository;

	@Autowired
	private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

	// 查询列表
	@PreAuthorize("hasRole('ROLE_VENDOR')")
	@GetMapping({ "/index" })
	public String index() {
		return "/ship/index";
	}

	// 出货看板
	@GetMapping({ "/view" })
	public String view() {
		return "/ship/index";
	}

	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<PurchaseOrderDetailSearchItem> list_ajax(Principal principal,
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
			order = "e.name";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order, "rowno");

		Page<PurchaseOrderDetailSearchItem> result = null;
		if (this.isVendor()) {
			result = purchaseOrderDetailRepository.findDetailsForShip(this.getLoginAccount().getVendor().getCode(),
					code, inventory, request);
		} else {
			result = purchaseOrderDetailRepository.findDetailsForBuyerShip(vendor, code, inventory, request);
		}

		return result;
	}

	@PostMapping("/export")
	public ModelAndView export_file(@RequestParam(value = "export_data") String exportData, Principal principal) {

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

	@RequestMapping("/import")
	public String import_file(@RequestParam("import_file") MultipartFile excelFile, HttpServletRequest request,
			RedirectAttributes redirectAttributes, Principal principal) {
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
				List<Integer> valueList = Arrays.asList(1, 2, 4, 8, 13);
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
					logger.info("row=" + row.toString());
					PurchaseOrderMain main = purchaseOrderMainRepository.findOneByCode(row.get(0));
					if (main.getVendor().getCode().equals(vendor.getCode())) {

						PurchaseOrderDetail detail = purchaseOrderDetailRepository
								.findOneById((long) Float.parseFloat(row.get(4)));
						logger.info(detail.getMain().getCode() + " " + detail.getRowno() + " "
								+ detail.getInventory().getCode());
						if (detail != null) {
							if (row.get(3) != null) {
								float quantity = detail.getShippedQuantity() == null ? 0 : detail.getShippedQuantity();

								detail.setShippedQuantity(quantity + Float.parseFloat(row.get(3)));
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
		return "redirect:/ship/index";
	}
}
