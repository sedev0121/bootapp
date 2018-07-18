package com.srm.platform.vendor.controller;

import java.math.BigInteger;
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
import com.srm.platform.vendor.repository.PurchaseInDetailRepository;
import com.srm.platform.vendor.repository.PurchaseInMainRepository;
import com.srm.platform.vendor.utility.PurchaseInDetailItem;
import com.srm.platform.vendor.utility.PurchaseInSearchResult;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/purchasein")
@PreAuthorize("hasRole('ROLE_BUYER') and hasAuthority('出入库单据-查看列表')")
public class PurchaseInController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private PurchaseInMainRepository purchaseInMainRepository;

	@Autowired
	private PurchaseInDetailRepository purchaseInDetailRepository;

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

		checkVendor(main.getVendor());

		model.addAttribute("main", main);
		return "purchasein/edit";
	}

	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<PurchaseInSearchResult> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "date");
		String dir = requestParams.getOrDefault("dir", "desc");
		String vendorStr = requestParams.getOrDefault("vendor", "");
		String stateStr = requestParams.getOrDefault("state", "0");
		String code = requestParams.getOrDefault("code", "");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);

		Integer state = Integer.parseInt(stateStr);
		Date startDate = Utils.parseDate(start_date);
		Date endDate = Utils.getNextDate(end_date);

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

		String selectQuery = "SELECT a.*, b.name vendorname  ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "FROM purchase_in_main a left join vendor b on a.vendorcode=b.code where 1=1 ";

		List<String> unitList = this.getDefaultUnitList();
		Map<String, Object> params = new HashMap<>();

		bodyQuery += " and b.unit_id in :unitList";
		params.put("unitList", unitList);

		if (!vendorStr.trim().isEmpty()) {
			bodyQuery += " and (b.name like CONCAT('%',:vendor, '%') or b.code like CONCAT('%',:vendor, '%')) ";
			params.put("vendor", vendorStr.trim());
		}

		if (!code.trim().isEmpty()) {
			bodyQuery += " and a.code like CONCAT('%',:code, '%') ";
			params.put("code", code.trim());
		}

		if (state > 0) {
			bodyQuery += " and state=:state";
			params.put("state", state);
		}

		if (startDate != null) {
			bodyQuery += " and a.date>=:startDate";
			params.put("startDate", startDate);
		}
		if (endDate != null) {
			bodyQuery += " and a.date<:endDate";
			params.put("endDate", endDate);
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "PurchaseInSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<PurchaseInSearchResult>(list, request, totalCount.longValue());
	}

	@RequestMapping(value = "/{code}/details", produces = "application/json")
	public @ResponseBody List<PurchaseInDetailItem> details_ajax(@PathVariable("code") String code) {
		List<PurchaseInDetailItem> list = purchaseInDetailRepository.findDetailsByCode(code);

		return list;
	}

	@RequestMapping(value = "/select", produces = "application/json")
	public @ResponseBody Page<PurchaseInDetailItem> select_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "code");
		String dir = requestParams.getOrDefault("dir", "asc");
		String vendor = requestParams.getOrDefault("vendor", "");
		String code = requestParams.getOrDefault("code", "");
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
		case "deployername":
			order = "c.realname";
			break;
		case "reviewername":
			order = "d.realname";
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
		case "closed_quantity":
			order = "d.closed_quantity";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order, "rowno");

		Page<PurchaseInDetailItem> result = purchaseInDetailRepository.findForSelect(vendor, code, inventory, request);

		return result;
	}

}
