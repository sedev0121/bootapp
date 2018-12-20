package com.srm.platform.vendor.controller;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.srm.platform.vendor.model.Price;
import com.srm.platform.vendor.repository.PriceRepository;
import com.srm.platform.vendor.utility.PriceSearchResult;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/baseprice")
@PreAuthorize("hasRole('ROLE_BUYER')")
public class PriceController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PriceRepository priceRepository;

	// 查询列表
	@GetMapping({ "", "/" })
	public String index() {
		return "price/index";
	}

	// 详细
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable("id") Long id, Model model) {

		Optional<Price> result = priceRepository.findById(id);

		model.addAttribute("data", result.isPresent() ? result.get() : new Price());
		return "price/edit";
	}

	// 查询列表API
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<PriceSearchResult> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "b.name");
		String vendorStr = requestParams.getOrDefault("vendor", "");
		String inventory = requestParams.getOrDefault("inventory", "");
		String dir = requestParams.getOrDefault("dir", "asc");
		String start_date = requestParams.getOrDefault("start_date", "");
		String end_date = requestParams.getOrDefault("end_date", "");

		Date startDate = Utils.parseDate(start_date);
		Date endDate = Utils.getNextDate(end_date);

		switch (order) {
		case "vendorname":
			order = "b.name";
			break;
		case "inventoryname":
			order = "c.name";
			break;
		case "createname":
			order = "d.name";
			break;
		}

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		String selectQuery = "SELECT distinct a.*, d.realname createname, b.name vendorname, b.code vendorcode, c.name inventoryname, c.code inventorycode ";
		String countQuery = "select count(DISTINCT a.id, d.realname , b.name, b.code, c.name, c.code) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "FROM price a left join vendor b on a.fsupplyno=b.code left join inventory c on a.cinvcode=c.code "
				+ "left join account d on a.createby=d.id WHERE b.code in :vendorList ";

		List<String> vendorList = this.getVendorListOfUser();
		Map<String, Object> params = new HashMap<>();

		params.put("vendorList", vendorList);
		if (!vendorStr.trim().isEmpty()) {
			bodyQuery += " and (b.name like CONCAT('%',:vendor, '%') or b.code like CONCAT('%',:vendor, '%')) ";
			params.put("vendor", vendorStr.trim());
		}

		if (!inventory.trim().isEmpty()) {
			bodyQuery += " and (c.name like CONCAT('%',:inventory, '%') or c.code like CONCAT('%',:inventory, '%')) ";
			params.put("inventory", inventory.trim());
		}

		if (startDate != null) {
			bodyQuery += " and a.cinvdate>=:startDate";
			params.put("startDate", startDate);
		}
		if (endDate != null) {
			bodyQuery += " and a.cinvdate<:endDate";
			params.put("endDate", endDate);
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "PriceSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<PriceSearchResult>(list, request, totalCount.longValue());

	}
}
