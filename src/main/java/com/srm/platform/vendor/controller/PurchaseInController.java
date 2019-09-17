package com.srm.platform.vendor.controller;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
import com.srm.platform.vendor.searchitem.PurchaseInDetailItem;
import com.srm.platform.vendor.searchitem.PurchaseInDetailResult;
import com.srm.platform.vendor.utility.AccountPermission;
import com.srm.platform.vendor.utility.AccountPermissionInfo;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/purchasein")
@PreAuthorize("hasRole('ROLE_VENDOR') or hasAuthority('对账单管理-查看列表')")
public class PurchaseInController extends CommonController {

	private static Long LIST_FUNCTION_ACTION_ID = 21L;

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

		// checkVendor(main.getVendor());

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
		String companyIdStr = requestParams.getOrDefault("company", "-1");
		String storeIdStr = requestParams.getOrDefault("store", "-1");
		String inventory = requestParams.getOrDefault("inventory", "");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);
		String stateStr = requestParams.getOrDefault("state", "-1");

		Date startDate = Utils.parseDate(start_date);
		Date endDate = Utils.getNextDate(end_date);

		switch (order) {
		case "code":
			order = "b.code";
			break;
		case "date":
			order = "b.date";
			break;
		case "vendorname":
			order = "v.name";
			break;
		case "vendorcode":
			order = "v.code";
			break;
		case "specs":
			order = "c.specs";
			break;
		case "unitname":
			order = "c.main_measure";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order, "rowno");

		String selectQuery = "select a.*, b.code, b.date, b.verify_date, c.main_measure unitname, c.name inventory_name,c.specs, com.name company_name, st.name store_name, "
				+ "v.name vendorname, v.code vendorcode, b.type, b.bredvouch, pom.code po_code, po.confirmed_memo confirmed_memo, dd.delivered_quantity ";
		String countQuery = "select count(a.id) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "from purchase_in_detail a " + "left join purchase_in_main b on a.main_id=b.id "
				+ "left join inventory c on a.inventory_code=c.code "
				+ "left join purchase_order_detail po on a.po_id=po.main_id and a.po_row_no=po.row_no "
				+ "left join purchase_order_main pom on po.main_id=pom.id "
				+ "left join account emp on pom.employee_no=emp.employee_no "
				+ "left join delivery_detail dd on a.delivery_code=dd.code and a.delivery_row_no=dd.row_no "
				+ "left join company com on b.company_code=com.code left join store st on b.store_code=st.code "
				+ "left join vendor v on b.vendor_code=v.code where b.vendor_code in (select vendor_code from account where role='ROLE_VENDOR' and vendor_code is not null)  ";

		Map<String, Object> params = new HashMap<>();

		if (isVendor()) {
			Vendor vendorObj = this.getLoginAccount().getVendor();
			String vendorStr = vendor == null ? "0" : vendorObj.getCode();
			bodyQuery += " and v.code= :vendor";
			params.put("vendor", vendorStr);

		} else {

			String subWhere = " 1=0 ";
			AccountPermissionInfo accountPermissionInfo = this.getPermissionScopeOfFunction(LIST_FUNCTION_ACTION_ID);
			if (accountPermissionInfo.isNoPermission()) {
				subWhere = " 1=0 ";
			} else if (accountPermissionInfo.isAllPermission()) {
				subWhere = " 1=1 ";
			} else {
				int index = 0;
				String key = "";
				for (AccountPermission accountPermission : accountPermissionInfo.getList()) {
					String tempSubWhere = " 1=1 ";
					List<String> allowedVendorCodeList = accountPermission.getVendorList();
					if (allowedVendorCodeList.size() > 0) {
						key = "vendorList" + index;
						tempSubWhere += " and v.code in :" + key;
						params.put(key, allowedVendorCodeList);
					}

					List<Long> allowedCompanyIdList = accountPermission.getCompanyList();
					if (allowedCompanyIdList.size() > 0) {
						key = "companyList" + index;
						tempSubWhere += " and com.id in :" + key;
						params.put(key, allowedCompanyIdList);
					}

					List<Long> allowedStoreIdList = accountPermission.getStoreList();
					if (allowedStoreIdList.size() > 0) {
						key = "storeList" + index;
						tempSubWhere += " and st.id in :" + key;
						params.put(key, allowedStoreIdList);
					}

					List<Long> allowedAccountIdList = accountPermission.getAccountList();
					if (allowedAccountIdList.size() > 0) {
						key = "accountList" + index;
						tempSubWhere += " and (emp.id in :" + key + ") ";
						params.put(key, allowedAccountIdList);
					}

					subWhere += " or (" + tempSubWhere + ") ";
					index++;
				}
			}

			bodyQuery += " and (" + subWhere + ") ";

		}

		if (!inventory.trim().isEmpty()) {
			bodyQuery += " and (c.name like CONCAT('%',:inventory, '%') or c.code like CONCAT('%',:inventory, '%')) ";
			params.put("inventory", inventory.trim());
		}

		if (!code.trim().isEmpty()) {
			bodyQuery += " and b.code like CONCAT('%',:code, '%')";
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

		Long companyId = Long.valueOf(companyIdStr);
		if (companyId >= 0) {
			bodyQuery += " and b.company_code=:company";
			params.put("company", companyId);
		}

		Long storeId = Long.valueOf(storeIdStr);
		if (storeId >= 0) {
			bodyQuery += " and b.store_code=:store";
			params.put("store", storeId);
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

		logger.info(selectQuery);
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
		String statementCompany = requestParams.getOrDefault("statement_company", "");
		String code = requestParams.getOrDefault("code", "");
		String type = requestParams.getOrDefault("type", "普通采购");
		String statementDateStr = requestParams.getOrDefault("statement_date", null);
		String dateStr = requestParams.getOrDefault("date", null);
		String inventory = requestParams.getOrDefault("inventory", "");

		Date statementDate = Utils.getNextDate(statementDateStr);
		Date date = Utils.parseDate(dateStr);

		switch (order) {
		case "code":
			order = "b.code";
			break;
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
			order = "c.main_measure";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order, "rowno");

		String selectQuery = "select a.*, b.code, b.date, b.verify_date, c.main_measure unitname, c.name inventory_name,c.specs, com.name company_name, st.name store_name, "
				+ "v.name vendorname, v.code vendorcode, b.type, b.bredvouch, pom.code po_code, po.confirmed_memo confirmed_memo, dd.delivered_quantity ";

		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "from purchase_in_detail a left join purchase_in_main b on a.main_id=b.id "
				+ "left join inventory c on a.inventory_code=c.code "
				+ "left join purchase_order_detail po on a.po_id=po.main_id and a.po_row_no=po.row_no "
				+ "left join purchase_order_main pom on po.main_id=pom.id "
				+ "left join delivery_detail dd on a.delivery_code=dd.code and a.delivery_row_no=dd.row_no "
				+ "left join company com on b.company_code=com.code left join store st on b.store_code=st.code "
				+ "left join vendor v on b.vendor_code=v.code where a.state=0 and type=:type and b.vendor_code=:vendor and com.statement_company_id=:company and b.date<:statementDate ";

		Map<String, Object> params = new HashMap<>();

		params.put("vendor", vendor);
		params.put("company", statementCompany);
		params.put("type", type);
		params.put("statementDate", statementDate);

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
