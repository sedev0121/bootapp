package com.srm.platform.vendor.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srm.platform.vendor.model.StockReportItem;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.StockReportRepository;
import com.srm.platform.vendor.searchitem.StockReportSearchItem;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.Utils;

// 库存报表
@Controller
@RequestMapping(path = "/stock")

public class StockReportController extends CommonController {

	@PersistenceContext
	private EntityManager em;

	// @Autowired
	// private StockReportRepository stockRepository;

	@PreAuthorize("hasRole('ROLE_VENDOR')")
	@GetMapping({ "/", "" })
	public String index() {
		return "report/stock";
	}

	// 查询列表API
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<StockReportItem> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String whcode_begin = requestParams.getOrDefault("whcode_begin", "");
		String whcode_end = requestParams.getOrDefault("whcode_end", "");
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "ASC");
		String vendorCode = this.getLoginAccount().getVendor().getCode();
		List<StockReportItem> resultList = new ArrayList();
		int totalResult = 0;
		Query q;

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);
		page_index++;
		String whcode_max = "";
		String whcode_min = "";

		if (whcode_min == "") {
			String selectMinQuery = "SELECT MIN(a.inventorycode) from purchase_order_detail a "
					+ "left join purchase_order_main b on b.code=a.code where b.vencode = '" + vendorCode + "'";
			q = em.createNativeQuery(selectMinQuery);
			if (q.getSingleResult() != null) {
				whcode_min = q.getSingleResult().toString();
			} else {
				return new PageImpl<StockReportItem>(resultList, request, totalResult);
			}
		}

		if (whcode_max == "") {
			String selectMaxQuery = "SELECT MAX(a.inventorycode) from purchase_order_detail a "
					+ "left join purchase_order_main b on b.code=a.code where b.vencode = '" + vendorCode + "'";
			q = em.createNativeQuery(selectMaxQuery);
			if (q.getSingleResult() != null) {
				whcode_max = q.getSingleResult().toString();
			} else {
				return new PageImpl<StockReportItem>(resultList, request, totalResult);
			}
		}

		if (whcode_begin == "") {
			whcode_begin = whcode_min;
		}

		if (whcode_end == "") {
			whcode_end = whcode_max;
		}

		if (whcode_begin.compareTo(whcode_min) < 0) {
			whcode_begin = whcode_min;
		}

		if (whcode_begin.compareTo(whcode_max) > 0) {
			whcode_begin = whcode_max;
		}

		if (whcode_end.compareTo(whcode_min) < 0) {
			whcode_end = whcode_min;
		}

		if (whcode_end.compareTo(whcode_max) > 0) {
			whcode_end = whcode_max;
		}

		ObjectMapper objectMapper = new ObjectMapper();
		List<LinkedHashMap<String, String>> entryList;
		Map<String, Object> map = new HashMap<>();

		try {
			map = new HashMap<>();
			Map<String, String> params = new HashMap<>();
			params.put("invcode_begin", whcode_begin);
			params.put("invcode_end", whcode_end);
			params.put("rows_per_page", String.format("%d", rows_per_page));
			params.put("page_index", String.format("%d", page_index));

			String response = apiClient.getCurrentStock(params);

			map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
			});

			String unitQuery = "";
			String measureUnit = "";

			int errorCode = Integer.parseInt((String) map.get("errcode"));
			if (errorCode == appProperties.getError_code_success()) {
				List<LinkedHashMap<String, String>> stockArray = (List<LinkedHashMap<String, String>>) map
						.get("currentstock");
				totalResult = Integer.parseInt((String) map.get("row_count"));

				for (LinkedHashMap<String, String> entryMap : stockArray) {
					measureUnit = entryMap.get("massunitname");

					if (measureUnit == "") {
						unitQuery = "SELECT a.name from measurement_unit a "
								+ "left join inventory b on b.main_measure=a.code " + "WHERE b.code='"
								+ entryMap.get("invcode") + "'";
						q = em.createNativeQuery(unitQuery);
						measureUnit = q.getSingleResult().toString();
					}

					StockReportItem stockItem = new StockReportItem(entryMap.get("invcode"), entryMap.get("invname"),
							entryMap.get("invstd"), measureUnit, entryMap.get("whname"), entryMap.get("qty"),
							entryMap.get("availqty"));
					resultList.add(stockItem);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
		}

		return new PageImpl<StockReportItem>(resultList, request, totalResult);
	}
	/*
	 * private Object[] appendValue(Object[] obj, Object newObj) { ArrayList<Object>
	 * temp = new ArrayList<Object>(Arrays.asList(obj)); temp.add(newObj); return
	 * temp.toArray(); }
	 */
}
