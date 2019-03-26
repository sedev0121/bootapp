package com.srm.platform.vendor.controller;

import java.math.BigInteger;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.Query;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
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
import org.springframework.web.servlet.ModelAndView;

import com.srm.platform.vendor.model.PriceChangeReportItem;
import com.srm.platform.vendor.searchitem.PriceSearchResult;
import com.srm.platform.vendor.utility.Utils;
import com.srm.platform.vendor.view.ExcelIncomingReportView;
import com.srm.platform.vendor.view.ExcelPriceChangeReportView;

@Controller
@RequestMapping(path = "/pricechange")
@PreAuthorize("hasRole('ROLE_BUYER') and hasAuthority('报表中心-查看列表')")
public class PriceChangeController extends CommonController {
	@PreAuthorize("hasRole('ROLE_BUYER')")
	@GetMapping({ "/", "" })
	public String index() {
		return "report/pricechange";
	}

	// 查询列表API
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<PriceChangeReportItem> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "c.code");
		String vendorStr = requestParams.getOrDefault("vendor", "");
		String inventory = requestParams.getOrDefault("inventory", "");
		String start_inventory = requestParams.getOrDefault("start_date", "");
		String end_inventory = requestParams.getOrDefault("end_date", "");
		
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				Direction.ASC, order);

		String selectQuery = "SELECT d.realname createname, b.name vendorname, b.code vendorcode, c.name inventoryname, c.code inventorycode, e.name fauxunit, a.* ";
		String groupBy = " group by c.code";
		String orderBy = " order by " + order + " ";

		String bodyQuery = "FROM price a left join vendor b on a.fsupplyno=b.code left join inventory c on a.cinvcode=c.code "
						 + "left join account d on a.createby=d.id " 
						 + "left join measurement_unit e on e.code = c.main_measure "
						 + "WHERE 1=1 ";
		
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		String priceQuery = "SELECT a.fprice, MAX(a.createdate) ";
		String avgPriceQuery = "SELECT AVG(a.fprice) ";
		String lastYear = String.format(" and a.createdate between '%d/01/01' and '%d/12/31' ", currentYear - 1, currentYear - 1);
		String currentDate = String.format(" and a.createdate between '%d/01/01' and '%s' ", currentYear, new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime()));
		
		List<String> vendorList = this.getVendorListOfUser();
		Map<String, Object> params = new HashMap<>();

		if (vendorList.size() > 0) {
			bodyQuery += " and b.code in :vendorList ";
			params.put("vendorList", vendorList);	
		}
		
		
		if (!start_inventory.trim().isEmpty()) {
			bodyQuery += " and c.code>=:startCode";
			params.put("startCode", start_inventory.trim());
		}
		
		if (!end_inventory.trim().isEmpty()) {
			bodyQuery += " and c.code<=:endCode";
			params.put("endCode", end_inventory.trim());
		}
		
		if (!vendorStr.trim().isEmpty()) {
			bodyQuery += " and (b.name like CONCAT('%',:vendor, '%') or b.code like CONCAT('%',:vendor, '%')) ";
			params.put("vendor", vendorStr.trim());
		}
		
		if (!inventory.trim().isEmpty()) {
			bodyQuery += " and (c.name like CONCAT('%',:inventory, '%') or c.code like CONCAT('%',:inventory, '%')) ";
			params.put("inventory", inventory.trim());
		}

		Query q;
		selectQuery += bodyQuery + groupBy + orderBy;
		q = em.createNativeQuery(selectQuery, "PriceSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		int total = q.getResultList().size();
		List<PriceSearchResult> searchResult = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();
		List<PriceChangeReportItem> list = new ArrayList<>();
		
		for (PriceSearchResult searchItem : searchResult) {
			// last year query
			q = em.createNativeQuery(priceQuery + bodyQuery + lastYear + " and c.code= '" + searchItem.getInventorycode() + "'");
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				q.setParameter(entry.getKey(), entry.getValue());
			}
			Object priceResult = q.getSingleResult();
			float previousprice = this.getChildFloatValue(priceResult, 0);
			
			// current year query
			q = em.createNativeQuery(priceQuery + bodyQuery + currentDate + " and c.code= '" + searchItem.getInventorycode() + "'");
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				q.setParameter(entry.getKey(), entry.getValue());
			}
			priceResult = q.getSingleResult();
			float currentprice = this.getChildFloatValue(priceResult, 0);
			
			float changepercent = 0.0f;
			if (currentprice != 0) {
				changepercent = (currentprice - previousprice) / currentprice * 100.0f;
			}
			
			// average
			q = em.createNativeQuery(avgPriceQuery + bodyQuery + currentDate + " and c.code= '" + searchItem.getInventorycode() + "'");
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				q.setParameter(entry.getKey(), entry.getValue());
			}
			priceResult = q.getSingleResult();
			float averageprice = 0.0f;
			if (priceResult != null) {
				averageprice = new Float(priceResult.toString());
			}
			
			previousprice = (float) Utils.costRound(previousprice);
			currentprice = (float) Utils.costRound(currentprice);
			changepercent = (float) Utils.costRound(changepercent);
			averageprice = (float) Utils.costRound(averageprice);
			
			PriceChangeReportItem item = new PriceChangeReportItem(searchItem.getVendorname(), searchItem.getVendorcode(), 
					searchItem.getInventoryname(), searchItem.getInventorycode(), searchItem.getDescription(), searchItem.getFauxunit(),
					previousprice, currentprice, changepercent, averageprice);
			list.add(item);
		}
		
		return new PageImpl<PriceChangeReportItem>(list, request, total);
	}
	
	private float getChildFloatValue(Object parent, int index) {
		float result = 0;
		if (parent == null) {
			return result;
		}
		
		Object[] arrayObject = (Object[])parent;
		
		if (arrayObject[index] == null) {
			return result;
		}
		
		result = new Float(arrayObject[index].toString());
		return result;
	}
	
	@RequestMapping(value = "/export")
	public ModelAndView export_file(@RequestParam Map<String, String>  exportData, Principal principal) throws JSONException {
		Page<PriceChangeReportItem> price_list = this.list_ajax(exportData);
		return new ModelAndView(new ExcelPriceChangeReportView(), "exportList", price_list.getContent());
	}
	
	@RequestMapping(value = "/singlehistory", produces = "application/json")
	public @ResponseBody Map<String, Object> singlehistory_ajax(@RequestParam Map<String, String> requestParams) {
		String inventory = requestParams.getOrDefault("inventory", "");
		Map<String, Object> graphData = new HashMap<>();
		int lastYear, lastMonth;
		String graphTitle = "";
		
		lastMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		lastYear = Calendar.getInstance().get(Calendar.YEAR) - 1;
		if (lastMonth > 12) {
			lastMonth -= 12;
			lastYear += 1;
		}
		graphTitle = String.format("%s商品 %d-%02d至%d-%02d价格变动曲线 ", 
									inventory, lastYear, lastMonth,
									Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH));
		
		String selectQuery = String.format("SELECT AVG(fprice) FROM price where cinvcode = '%s'", inventory);

		Query q;
		Map<String, Object> params = new HashMap<>();
		List<Object> priceList = new ArrayList<Object>();
		List<String> monthList = new ArrayList<String>();
		
		for (int i = 0; i < 12; ++i) {
			String dateQuery = String.format(" and createdate between '%d/%02d/01' and '%d/%02d/31'", lastYear, lastMonth, lastYear, lastMonth);
			q = em.createNativeQuery(selectQuery + dateQuery);
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				q.setParameter(entry.getKey(), entry.getValue());
			}
			Object priceResult = q.getSingleResult();
			if (priceResult == null) {
				priceResult = "0";
			}
			
			priceList.add(priceResult);
			monthList.add(String.format("%d-%02d", lastYear, lastMonth));
			
			lastMonth++;
			if (lastMonth > 12) {
				lastMonth -= 12;
				lastYear++;
			}
		}
		
		graphData.put("data", priceList);
		graphData.put("month", monthList);
		graphData.put("title", graphTitle);
		
		return graphData;
	}
}
