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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.DeliveryMain;
import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.model.PurchaseOrderDetail;
import com.srm.platform.vendor.model.PurchaseOrderMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.PurchaseOrderDetailRepository;
import com.srm.platform.vendor.repository.PurchaseOrderMainRepository;
import com.srm.platform.vendor.saveform.PurchaseOrderSaveForm;
import com.srm.platform.vendor.searchitem.PurchaseInDetailResult;
import com.srm.platform.vendor.searchitem.PurchaseOrderSearchResult;
import com.srm.platform.vendor.utility.AccountPermission;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/purchaseorder")
@PreAuthorize("hasRole('ROLE_VENDOR') or hasAuthority('订单管理-查看列表')")
public class PurchaseOrderController extends CommonController {

	private static Long LIST_FUNCTION_ACTION_ID = 14L;
	private static Long DEPLOY_FUNCTION_ACTION_ID = 15L;
	private static Long CLOSE_FUNCTION_ACTION_ID = 16L;
	private static Long CLOSE_ROW_FUNCTION_ACTION_ID = 17L;

	@PersistenceContext
	private EntityManager em;

	@Override
	protected String getOperationHistoryType() {
		return "order";
	};

	// 查询列表
	@GetMapping({ "/", "" })
	public String index() {
		return "purchaseorder/index";
	}

	@GetMapping({ "/{code}/edit" })
	public String edit(@PathVariable("code") String code, Model model) {
		PurchaseOrderMain main = this.purchaseOrderMainRepository.findOneByCode(code);
		if (main == null)
			show404();

		checkPermission(main, LIST_FUNCTION_ACTION_ID);
		
		model.addAttribute("main", main);
		model.addAttribute("canDeploy", hasPermission(main, DEPLOY_FUNCTION_ACTION_ID));
		model.addAttribute("canClose", hasPermission(main, CLOSE_FUNCTION_ACTION_ID));
		model.addAttribute("canCloseRow", hasPermission(main, CLOSE_ROW_FUNCTION_ACTION_ID));
		
		return "purchaseorder/edit";
	}

	@GetMapping({ "/{code}/read/{msgid}" })
	public String read(@PathVariable("code") String code, @PathVariable("msgid") Long msgid, Model model) {
		setReadDate(msgid);
		return "redirect:/purchaseorder/" + code + "/edit";
	}

	@RequestMapping(value = "/{code}/details", produces = "application/json")
	public @ResponseBody List<PurchaseOrderDetail> details_ajax(@PathVariable("code") String code) {
		List<PurchaseOrderDetail> list = purchaseOrderDetailRepository.findDetailsByCode(code);

		return list;
	}

	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<PurchaseOrderSearchResult> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "deploydate");
		String dir = requestParams.getOrDefault("dir", "desc");
		String vendorStr = requestParams.getOrDefault("vendor", "");
		String stateStr = requestParams.getOrDefault("state", "0");
		String code = requestParams.getOrDefault("code", "");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);

		Date startDate = Utils.parseDate(start_date);
		Date endDate = Utils.getNextDate(end_date);
		Integer state = Integer.parseInt(stateStr);

		switch (order) {
		case "vendorname":
			order = "b.abbrname";
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

		String selectQuery = "SELECT a.*, b.abbrname vendorname, f.name companyname, c.realname deployername, d.realname reviewername, e.prepay_money, e.money, e.sum ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "FROM purchase_order_main a left join vendor b on a.vencode=b.code left join account c on a.deployer=c.id left join account d on a.reviewer=d.id "
				+ "left join (select code, sum(prepay_money) prepay_money, sum(money) money, sum(sum) sum from purchase_order_detail group by code) e on a.code=e.code "
				+ "left join company f on a.company_id=f.id WHERE a.state='审核' and a.company_id is not null and a.vencode in (select vendor_code from account where vendor_code is not null) ";

		Map<String, Object> params = new HashMap<>();

		if (isVendor()) {
			Vendor vendor = this.getLoginAccount().getVendor();
			vendorStr = vendor == null ? "0" : vendor.getCode();
			bodyQuery += " and b.code= :vendor";
			params.put("vendor", vendorStr);

			bodyQuery += " and a.srmstate>0 ";

		} else {
			String subWhere = " 1=0 ";
			AccountPermission accountPermission = this.getPermissionScopeOfFunction(LIST_FUNCTION_ACTION_ID);
			List<String> allowedVendorCodeList = accountPermission.getVendorList();
			if (!(allowedVendorCodeList == null || allowedVendorCodeList.size() == 0)) {
				subWhere += " or a.vencode in :vendorList";
				params.put("vendorList", allowedVendorCodeList);
			}

			List<Long> allowedAccountIdList = accountPermission.getAccountList();
			if (!(allowedAccountIdList == null || allowedAccountIdList.size() == 0)) {
				subWhere += " or a.deployer in :accountList";
				params.put("accountList", allowedAccountIdList);
			}

			List<Long> allowedCompanyIdList = accountPermission.getCompanyList();
			if (!(allowedCompanyIdList == null || allowedCompanyIdList.size() == 0)) {
				subWhere += " or a.company_id in :companyList";
				params.put("companyList", allowedCompanyIdList);
			}
			
			bodyQuery += " and (" + subWhere + ") ";

		}

		if (!vendorStr.trim().isEmpty()) {
			bodyQuery += " and (b.name like CONCAT('%',:vendor, '%') or b.abbrname like CONCAT('%',:vendor, '%') or b.code like CONCAT('%',:vendor, '%')) ";
			params.put("vendor", vendorStr.trim());
		}

		if (!code.trim().isEmpty()) {
			bodyQuery += " and a.code like CONCAT('%',:code, '%') ";
			params.put("code", code.trim());
		}

		if (state >= 0) {
			bodyQuery += " and srmstate=:state";
			params.put("state", state);
		}

		if (startDate != null) {
			bodyQuery += " and a.deploydate>=:startDate";
			params.put("startDate", startDate);
		}
		if (endDate != null) {
			bodyQuery += " and a.deploydate<:endDate";
			params.put("endDate", endDate);
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "PurchaseOrderSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<PurchaseOrderSearchResult>(list, request, totalCount.longValue());

	}

	@Transactional
	@PostMapping("/update")
	public @ResponseBody GenericJsonResponse<PurchaseOrderMain> update_ajax(PurchaseOrderSaveForm form) {

		Account account = this.getLoginAccount();
		PurchaseOrderMain main = purchaseOrderMainRepository.findOneByCode(form.getCode());
		

		Account vendorAccount = accountRepository.findOneByUsername(main.getVendor().getCode());
		if (vendorAccount == null) {
			GenericJsonResponse<PurchaseOrderMain> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, "还未开通此供应商用户", null);
			return jsonResponse;			
		}
		
		if (form.getState() != Constants.PURCHASE_ORDER_STATE_CLOSE_ROW) {
			main.setSrmstate(form.getState());
		}

		if (form.getState() == Constants.PURCHASE_ORDER_STATE_DEPLOY) {
			main.setStore(storeRepository.findOneById(form.getStore()));
			main.setDeploydate(new Date());
			main.setDeployer(account);
			main.setContractCode(form.getContract_code());
			main.setBasePrice(form.getBase_price());
		} else if (form.getState() == Constants.PURCHASE_ORDER_STATE_CONFIRM) {
			main.setReviewdate(new Date());
			main.setReviewer(account);
		} else if (form.getState() == Constants.PURCHASE_ORDER_STATE_CLOSE) {
			
			Integer detailCountIsDelivering = deliveryDetailRepository.findDetailCountIsDelivering(main.getCode());
			if (detailCountIsDelivering > 0) {
				GenericJsonResponse<PurchaseOrderMain> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, detailCountIsDelivering + "个货品正在发货，不能关闭", null);
				return jsonResponse;
			}
			main.setClosedate(new Date());
			main.setCloser(account.getRealname());
		} else if (form.getState() == Constants.PURCHASE_ORDER_STATE_CLOSE_ROW) {			
			Integer detailCountIsDelivering = deliveryDetailRepository.findDetailCountIsDelivering(main.getCode());
			if (detailCountIsDelivering > 0) {
				GenericJsonResponse<PurchaseOrderMain> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, detailCountIsDelivering + "个货品正在发货，不能关闭", null);
				return jsonResponse;
			}
		}

		main = purchaseOrderMainRepository.save(main);

		String action = null;
		List<Account> toList = new ArrayList<>();
		toList.add(main.getDeployer());
		switch (form.getState()) {
		case Constants.PURCHASE_ORDER_STATE_DEPLOY:
			action = "发布";
			toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
			break;
		case Constants.PURCHASE_ORDER_STATE_CONFIRM:
			action = "确认";
			break;
		case Constants.PURCHASE_ORDER_STATE_CLOSE:
			action = "关闭";
			break;
		case Constants.PURCHASE_ORDER_STATE_CLOSE_ROW:
			action = "行关闭";
			break;
		}

		String title = String.format("订单【%s】已由【%s】%s，请及时查阅和处理！", main.getCode(), account.getRealname(), action);

		this.sendmessage(title, toList, String.format("/purchaseorder/%s/read", main.getCode()));
		this.addOpertionHistory(main.getCode(), action, form.getContent());

		if (form.getTable() != null) {
			for (Map<String, String> item : form.getTable()) {

				PurchaseOrderDetail detail = purchaseOrderDetailRepository.findOneById(Long.parseLong(item.get("id")));
				if (form.getState() == Constants.PURCHASE_ORDER_STATE_DEPLOY) {
					detail.setMemo(item.get("memo"));
					detail.setContractCode(item.get("contract_code"));
					detail.setPriceFrom(Integer.parseInt(item.get("price_from")));
					
					detail.setBasePrice(null);
					if (!Utils.isEmpty(item.get("base_price"))) {
						detail.setBasePrice(Double.parseDouble(item.get("base_price")));	
					}
					
					if (!Utils.isEmpty(item.get("price"))) {
						detail.setPrice(Double.parseDouble(item.get("price")));	
					}
					
					if (!Utils.isEmpty(item.get("tax_price"))) {
						detail.setTaxPrice(Double.parseDouble(item.get("tax_price")));	
					}
					
					if (!Utils.isEmpty(item.get("money"))) {
						detail.setMoney(Double.parseDouble(item.get("money")));	
					}
					
					if (!Utils.isEmpty(item.get("sum"))) {
						detail.setSum(Double.parseDouble(item.get("sum")));	
					}
					
					detail.setConfirmedDate(detail.getArriveDate());
					detail.setConfirmedQuantity(detail.getQuantity());
					detail.setCountPerBox(Integer.valueOf(item.get("count_per_box")));
					if (Integer.parseInt(item.get("close_state")) == Constants.PURCHASE_ORDER_ROW_CLOSE_STATE_YES) {
						if (detail.getCloseDate() == null) {
							detail.setCloseDate(new Date());
							detail.setCloserName(getLoginAccount().getRealname());
						}
					} else {
						detail.setCloseDate(null);
					}
				} else if (form.getState() == Constants.PURCHASE_ORDER_STATE_CONFIRM) {
					detail.setConfirmedDate(Utils.parseDate(item.get("confirmed_date")));
					detail.setConfirmedMemo(item.get("confirmed_memo"));
				} else if (form.getState() == Constants.PURCHASE_ORDER_STATE_CLOSE_ROW) {
					detail.setCloserName(this.getLoginAccount().getRealname());
					detail.setCloseDate(new Date());
				}

				purchaseOrderDetailRepository.save(detail);
			}
		}

		GenericJsonResponse<PurchaseOrderMain> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, main);
		return jsonResponse;
	}

	@RequestMapping(value = "/details/search", produces = "application/json")
	public @ResponseBody Page<PurchaseOrderDetail> list_detail(@RequestParam Map<String, String> requestParams) {

		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");
		String type = requestParams.getOrDefault("type", "");
		String company = requestParams.getOrDefault("company", null);
		String store = requestParams.getOrDefault("store", null);

		switch (order) {
		case "main.code":
			order = "code";
			break;
		case "inventory.code":
			order = "inventory_code";
			break;
		case "main.purchase_type_name":
			order = "b.purchase_type_name";
			break;
		}

		Vendor vendor = getLoginAccount().getVendor();
		if (vendor == null || company == null || store == null) {
			return Page.empty();
		}
		
		Long companyId = Long.parseLong(company);
		Long storeId = Long.parseLong(store);

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);
		Page<PurchaseOrderDetail> result = purchaseOrderDetailRepository.searchAllOfOneVendor(search, vendor.getCode(), companyId, storeId, 
				type, request);

		return result;
	}
	
	private void checkPermission(PurchaseOrderMain main, Long functionActionId) {
		if (this.isVendor()) {
			if (!main.getVendor().getCode().equals(this.getLoginAccount().getVendor().getCode())) {
				show403();
			}
		} else {
			if (!hasPermission(main, functionActionId)) {
				show403();
			}
		}
	}
	
	private boolean hasPermission(PurchaseOrderMain main, Long functionActionId) {
		AccountPermission accountPermission = this.getPermissionScopeOfFunction(functionActionId);
		List<String> allowedVendorCodeList = accountPermission.getVendorList();
		List<Long> allowedAccountIdList = accountPermission.getAccountList();
		List<Long> allowedCompanyIdList = accountPermission.getCompanyList();
		
		boolean isValid = false;

		if (!(allowedVendorCodeList == null || allowedVendorCodeList.size() == 0)
				&& main.getVendor() != null
				&& allowedVendorCodeList.contains(main.getVendor().getCode())) {
			isValid = true;
		} else if (!(allowedAccountIdList == null || allowedAccountIdList.size() == 0) 
				&& main.getDeployer() != null 
				&& allowedAccountIdList.contains(main.getDeployer().getId())) {
			isValid = true;
		} else if (!(allowedCompanyIdList == null || allowedCompanyIdList.size() == 0)
				&& main.getCompany() != null && allowedCompanyIdList.contains(main.getCompany().getId())) {
			isValid = true;
		}

		return isValid;
	}
}
