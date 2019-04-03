package com.srm.platform.vendor.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.PurchaseInMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.PurchaseInDetailRepository;
import com.srm.platform.vendor.repository.PurchaseInMainRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.searchitem.InquerySearchResult;
import com.srm.platform.vendor.searchitem.PurchaseInDetailItem;
import com.srm.platform.vendor.searchitem.PurchaseInDetailResult;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/purchasein")
@PreAuthorize("hasRole('ROLE_BUYER') and hasAuthority('出入库单据-查看列表')")
public class PurchaseInController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;




	// 查询列表
	@GetMapping({ "/", "" })
	public String index() {
		return "purchasein/index";
	}

	@GetMapping({ "/{code}/edit" })
	public String edit(@PathVariable("code") String code, Model model) {
		PurchaseInMain main = this.purchaseInMainRepository.findOneByCode(code);
		if (main == null)
			show404();

//		checkVendor(main.getVendor());

		model.addAttribute("main", main);
		return "purchasein/edit";
	}

	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<PurchaseInDetailResult> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "code");
		String dir = requestParams.getOrDefault("dir", "asc");
		String vendor = requestParams.getOrDefault("vendor", "");
		String code = requestParams.getOrDefault("code", "");
		String inventory = requestParams.getOrDefault("inventory", "");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);
		String stateStr = requestParams.getOrDefault("state", "-1");

		Date startDate = Utils.parseDate(start_date);
		Date endDate = Utils.getNextDate(end_date);

		switch (order) {
		case "date":
			order = "b.date";
			break;
		case "vendorname":
			order = "v.name";
			break;
		case "vendorcode":
			order = "v.code";
			break;
		case "inventorycode":
			order = "c.code";
			break;
		case "inventoryname":
			order = "c.name";
			break;
		case "specs":
			order = "c.specs";
			break;
		case "unitname":
			order = "m.name";
			break;
		case "mainmemo":
			order = "b.memo";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order, "rowno");

		String selectQuery = "select a.*, m.name unitname, b.date, b.verify_date, c.name inventoryname,c.specs, v.name vendorname, v.code vendorcode, b.type, b.bredvouch, b.memo mainmemo ";
		String countQuery = "select count(a.id) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "from purchase_in_detail a left join purchase_in_main b on a.code=b.code left join inventory c on a.inventory_code=c.code "
				+ "left join measurement_unit m on c.main_measure=m.code left join vendor v on b.vendor_code=v.code  "
				+ "where v.code in :vendorList ";

		Map<String, Object> params = new HashMap<>();

//		List<String> vendorList = this.getVendorListOfUser();
//		
//		if (vendorList.size() == 0) {
//			return new PageImpl<PurchaseInDetailResult>(new ArrayList(), request, 0);
//		}
//		
//		params.put("vendorList", vendorList);

		if (!inventory.trim().isEmpty()) {
			bodyQuery += " and (c.name like CONCAT('%',:inventory, '%') or c.code like CONCAT('%',:inventory, '%')) ";
			params.put("inventory", inventory.trim());
		}

		if (!code.trim().isEmpty()) {
			bodyQuery += " and a.code like CONCAT('%',:code, '%')";
			params.put("code", code);
		}

		if (!vendor.trim().isEmpty()) {
			bodyQuery += " and (v.code like CONCAT('%',:vendor, '%') or v.name like CONCAT('%',:vendor, '%'))";
			params.put("vendor", vendor);
		}

		if (startDate != null) {
			bodyQuery += " and b.date>=:startDate";
			params.put("startDate", startDate);
		}

		if (endDate != null) {
			bodyQuery += " and b.date<:endDate";
			params.put("endDate", endDate);
		}

		Long state = Long.valueOf(stateStr);
		if (state >= 0) {
			bodyQuery += " and a.state=:state";
			params.put("state", state);
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "PurchaseInDetailResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<PurchaseInDetailResult>(list, request, totalCount.longValue());
	}

	@RequestMapping(value = "/{code}/details", produces = "application/json")
	public @ResponseBody List<PurchaseInDetailItem> details_ajax(@PathVariable("code") String code) {
		List<PurchaseInDetailItem> list = purchaseInDetailRepository.findDetailsByCode(code);

		return list;
	}

	@RequestMapping(value = "/select", produces = "application/json")
	public @ResponseBody Page<PurchaseInDetailResult> select_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "code");
		String dir = requestParams.getOrDefault("dir", "asc");
		String vendor = requestParams.getOrDefault("vendor", "");
		String code = requestParams.getOrDefault("code", "");
		String type = requestParams.getOrDefault("type", "普通采购");
		String dateStr = requestParams.getOrDefault("date", null);
		String inventory = requestParams.getOrDefault("inventory", "");

		Date date = Utils.parseDate(dateStr);

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
		case "inventoryname":
			order = "c.name";
			break;
		case "specs":
			order = "c.specs";
			break;
		case "unitname":
			order = "m.name";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order, "rowno");

		String selectQuery = "select a.*, m.name unitname, b.date, b.verify_date, c.name inventoryname,c.specs, null vendorname, vendor_code vendorcode, b.type, b.bredvouch, b.memo mainmemo ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "from purchase_in_detail a left join purchase_in_main b on a.code=b.code left join inventory c on a.inventory_code=c.code "
				+ "left join measurement_unit m on c.main_measure=m.code "
				+ "where a.state=0 and type=:type and b.vendor_code=:vendor ";

		Map<String, Object> params = new HashMap<>();

		params.put("vendor", vendor);
		params.put("type", type);

		if (!inventory.trim().isEmpty()) {
			bodyQuery += " and (c.name like CONCAT('%',:inventory, '%') or c.code like CONCAT('%',:inventory, '%')) ";
			params.put("inventory", inventory.trim());
		}

		if (!code.trim().isEmpty()) {
			bodyQuery += " and a.code like CONCAT('%',:code, '%')";
			params.put("code", code);
		}

		if (date != null) {
			bodyQuery += " and b.date=:date";
			params.put("date", date);
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "PurchaseInDetailResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<PurchaseInDetailResult>(list, request, totalCount.longValue());

	}

}
