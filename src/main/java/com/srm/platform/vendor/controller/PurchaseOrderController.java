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
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/purchaseorder")
@PreAuthorize("hasRole('ROLE_VENDOR') or hasAuthority('订单管理-查看列表')")
public class PurchaseOrderController extends CommonController {

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

		// checkVendor(main.getVendor());

		model.addAttribute("main", main);
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

		String selectQuery = "SELECT a.*, b.abbrname vendorname, c.realname deployername, d.realname reviewername, e.prepay_money, e.money, e.sum ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "FROM purchase_order_main a left join vendor b on a.vencode=b.code left join account c on a.deployer=c.id left join account d on a.reviewer=d.id "
				+ "left join (select code, sum(prepay_money) prepay_money, sum(money) money, sum(sum) sum from purchase_order_detail group by code) e on a.code=e.code "
				+ "WHERE a.state='审核' ";

		Map<String, Object> params = new HashMap<>();

		if (isVendor()) {
			Vendor vendor = this.getLoginAccount().getVendor();
			vendorStr = vendor == null ? "0" : vendor.getCode();
			bodyQuery += " and b.code= :vendor";
			params.put("vendor", vendorStr);

			bodyQuery += " and a.srmstate>0 ";

		} else {
			// List<String> vendorList = this.getVendorListOfUser();
			//
			// if (vendorList.size() == 0) {
			// return new PageImpl<PurchaseOrderSearchResult>(new ArrayList(), request, 0);
			// }
			//
			// bodyQuery += " and b.code in :vendorList";
			// params.put("vendorList", vendorList);
			// if (!vendorStr.trim().isEmpty()) {
			// bodyQuery += " and (b.name like CONCAT('%',:vendor, '%') or b.code like
			// CONCAT('%',:vendor, '%')) ";
			// params.put("vendor", vendorStr.trim());
			// }
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
	public @ResponseBody PurchaseOrderMain update_ajax(PurchaseOrderSaveForm form) {

		Account account = this.getLoginAccount();
		PurchaseOrderMain main = purchaseOrderMainRepository.findOneByCode(form.getCode());
		main.setSrmstate(form.getState());
		if (form.getState() == Constants.PURCHASE_ORDER_STATE_DEPLOY) {
			main.setDeploydate(new Date());
			main.setDeployer(account);
		} else if (form.getState() == Constants.PURCHASE_ORDER_STATE_REVIEW
				|| form.getState() == Constants.PURCHASE_ORDER_STATE_CANCEL) {
			main.setReviewdate(new Date());
			main.setReviewer(account);
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
		case Constants.PURCHASE_ORDER_STATE_REVIEW:
			action = "确认";
			break;
		case Constants.PURCHASE_ORDER_STATE_CANCEL:
			action = "拒绝";
			break;
		}
		String title = String.format("订单【%s】已由【%s】%s，请及时查阅和处理！", main.getCode(), account.getRealname(), action);

		this.sendmessage(title, toList, String.format("/purchaseorder/%s/read", main.getCode()));
		this.addOpertionHistory(main.getCode(), String.format("%s了订单", action));

		if (form.getTable() != null) {
			for (Map<String, String> item : form.getTable()) {

				PurchaseOrderDetail detail = purchaseOrderDetailRepository.findOneById(Long.parseLong(item.get("id")));
				if (this.isVendor()) {
				} else {
					if (item.get("prepay_money") != null && !item.get("prepay_money").isEmpty())
						detail.setPrepayMoney(Double.parseDouble(item.get("prepay_money")));
					else
						detail.setPrepayMoney(null);
//					detail.setArriveNote(item.get("arrive_note"));
				}

				purchaseOrderDetailRepository.save(detail);
			}
		}

		return main;
	}

	@RequestMapping(value = "/details/search", produces = "application/json")
	public @ResponseBody Page<PurchaseOrderDetail> list_detail(@RequestParam Map<String, String> requestParams) {

		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");

		switch (order) {
		case "main.code":
			order = "code";
			break;
		}

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);
		Page<PurchaseOrderDetail> result = purchaseOrderDetailRepository.searchAll(search, request);

		return result;
	}
}
