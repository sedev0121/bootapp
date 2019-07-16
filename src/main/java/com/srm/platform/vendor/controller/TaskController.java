package com.srm.platform.vendor.controller;

import java.io.File;
import java.math.BigInteger;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Box;
import com.srm.platform.vendor.model.DeliveryDetail;
import com.srm.platform.vendor.model.DeliveryMain;
import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.model.PurchaseOrderDetail;
import com.srm.platform.vendor.model.PurchaseOrderMain;
import com.srm.platform.vendor.model.VenPriceAdjustMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.saveform.DeliverySaveForm;
import com.srm.platform.vendor.searchitem.BoxExportResult;
import com.srm.platform.vendor.searchitem.DeliverySearchResult;
import com.srm.platform.vendor.u8api.RestApiClient;
import com.srm.platform.vendor.u8api.RestApiResponse;
import com.srm.platform.vendor.utility.AccountPermission;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/task")
public class TaskController extends CommonController {

	private static Long LIST_FUNCTION_ACTION_ID = 18L;
	private static Long CONFIRM_FUNCTION_ACTION_ID = 19L;

	@Override
	protected String getOperationHistoryType() {
		return "task";
	};

	// 查询列表
	@GetMapping({ "", "/" })
	public String index() {
		return "task/index";
	}

	// 新建
	@PreAuthorize("hasRole('ROLE_VENDOR')")
	@GetMapping({ "/add" })
	public String add(Model model) {
		DeliveryMain main = new DeliveryMain();
		main.setVendor(getLoginAccount().getVendor());
		model.addAttribute("main", main);
		return "task/edit";
	}

	// 查询列表API
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<DeliverySearchResult> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");

		String vendorStr = requestParams.getOrDefault("vendor", "");
		String stateStr = requestParams.getOrDefault("state", "0");
		String code = requestParams.getOrDefault("code", "");

		Integer state = Integer.parseInt(stateStr);

		switch (order) {
		case "vendor.name":
			order = "b.name";
			break;
		}

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		String selectQuery = "select a.*, b.name vendor_name, b.contact, c.name company_name, d.name store_name, d.address store_address ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = " FROM delivery_main a left join vendor b on a.vendor_code=b.code left join company c on a.company_id=c.id left join store d on a.store_id=d.id where 1=1 ";

		Map<String, Object> params = new HashMap<>();

		if (isVendor()) {
			Vendor vendor = this.getLoginAccount().getVendor();
			bodyQuery += " and b.code= :vendor";
			params.put("vendor", vendor.getCode());

		} else {
			String subWhere = " 1=0 ";
			AccountPermission accountPermission = this.getPermissionScopeOfFunction(LIST_FUNCTION_ACTION_ID);
			List<Long> allowedCompanyIdList = accountPermission.getCompanyList();
			if (!(allowedCompanyIdList == null || allowedCompanyIdList.size() == 0)) {
				subWhere += " or a.company_id in :companyList";
				params.put("companyList", allowedCompanyIdList);
			}

			List<String> allowedVendorCodeList = accountPermission.getVendorList();
			if (!(allowedVendorCodeList == null || allowedVendorCodeList.size() == 0)) {
				subWhere += " or a.vendor_code in :vendorList";
				params.put("vendorList", allowedVendorCodeList);
			}

			List<Long> allowedStoreIdList = accountPermission.getStoreList();
			if (!(allowedStoreIdList == null || allowedStoreIdList.size() == 0)) {
				subWhere += " or a.store_id in :storeList";
				params.put("storeList", allowedStoreIdList);
			}

			List<Long> allowedAccountIdList = accountPermission.getAccountList();
			if (!(allowedAccountIdList == null || allowedAccountIdList.size() == 0)) {
				subWhere += " or a.confirmer_id in :accountList";
				params.put("accountList", allowedAccountIdList);
			}

			bodyQuery += " and (" + subWhere + ") ";
			bodyQuery += " and a.state>=" + Constants.DELIVERY_STATE_SUBMIT;
		}

		if (!code.trim().isEmpty()) {
			bodyQuery += " and a.code like CONCAT('%',:code, '%') ";
			params.put("code", code.trim());
		}

		if (!vendorStr.trim().isEmpty()) {
			bodyQuery += " and (b.code like CONCAT('%',:vendor, '%') or b.name like CONCAT('%',:vendor, '%') or b.abbrname like CONCAT('%',:vendor, '%'))";
			params.put("vendor", vendorStr.trim());
		}

		if (state > 0) {
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
		q = em.createNativeQuery(selectQuery, "DeliverySearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<DeliverySearchResult>(list, request, totalCount.longValue());

	}

	// 更新API
	@Transactional
	@PostMapping("/update")
	public @ResponseBody GenericJsonResponse<DeliveryMain> update_ajax(DeliverySaveForm form, Principal principal) {

		DeliveryMain main = new DeliveryMain();		

		GenericJsonResponse<DeliveryMain> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null,
				main);

		return jsonResponse;
	}

	
}
