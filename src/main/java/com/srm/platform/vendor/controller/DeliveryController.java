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
import com.srm.platform.vendor.model.DeliveryDetail;
import com.srm.platform.vendor.model.DeliveryMain;
import com.srm.platform.vendor.model.VenPriceAdjustMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.saveform.DeliverySaveForm;
import com.srm.platform.vendor.searchitem.DeliverySearchResult;
import com.srm.platform.vendor.utility.AccountPermission;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/delivery")
public class DeliveryController extends CommonController {

	private static Long LIST_FUNCTION_ACTION_ID = 18L;

	@Override
	protected String getOperationHistoryType() {
		return "delivery";
	};

	// 查询列表
	@GetMapping({ "", "/" })
	public String index() {
		return "delivery/index";
	}

	// 新建
	@PreAuthorize("hasRole('ROLE_VENDOR')")
	@GetMapping({ "/add" })
	public String add(Model model) {
		DeliveryMain main = new DeliveryMain();
		main.setVendor(getLoginAccount().getVendor());
		model.addAttribute("main", main);
		return "delivery/edit";
	}

	// 详细
	@GetMapping({ "/{code}/edit" })
	public String edit(@PathVariable("code") String code, Model model) {
		DeliveryMain main = deliveryMainRepository.findOneByCode(code);
		if (main == null)
			show404();

		checkPermission(main, LIST_FUNCTION_ACTION_ID);

		model.addAttribute("main", main);
		return "delivery/edit";
	}

	@RequestMapping(value = "/{code}/details", produces = "application/json")
	public @ResponseBody List<DeliveryDetail> details_ajax(@PathVariable("code") String code) {
		List<DeliveryDetail> list = deliveryDetailRepository.findDetailsByCode(code);

		return list;
	}

	@GetMapping({ "/{code}/read/{msgid}" })
	public String read(@PathVariable("code") String code, @PathVariable("msgid") Long msgid, Model model) {
		setReadDate(msgid);
		return "redirect:/delivery/" + code + "/edit";
	}

	@GetMapping("/{id}/deleteattach")
	// @PreAuthorize("hasAuthority('询价管理-新建/发布') or hasRole('ROLE_VENDOR')")
	public @ResponseBody Boolean deleteAttach(@PathVariable("id") Long id) {
		DeliveryMain main = deliveryMainRepository.findOneById(id);

		// File attach = new
		// File(UploadFileHelper.getUploadDir(Constants.PATH_UPLOADS_INQUERY) +
		// File.separator
		// + main.getAttachFileName());
		// if (attach.exists())
		// attach.delete();
		// main.setAttachFileName(null);
		// main.setAttachOriginalName(null);
		deliveryMainRepository.save(main);
		return true;
	}

	// 删除API
	// @PreAuthorize("hasAuthority('询价管理-删除') or hasRole('ROLE_VENDOR')")
	@GetMapping("/{code}/delete")
	@Transactional
	public @ResponseBody Boolean delete_ajax(@PathVariable("code") String code) {
		DeliveryMain main = deliveryMainRepository.findOneByCode(code);
		if (main != null) {
			deliveryDetailRepository.DeleteByCode(code);
			deliveryMainRepository.delete(main);
		}

		return true;
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

			bodyQuery += " and a.state>=" + Constants.STATEMENT_STATE_REVIEW;

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
		}

		if (!code.trim().isEmpty()) {
			bodyQuery += " and a.code like CONCAT('%',:code, '%') ";
			params.put("code", code.trim());
		}

		if (!vendorStr.trim().isEmpty()) {
			bodyQuery += " and (b.name like CONCAT('%',:vendor, '%') or b.abbrname like CONCAT('%',:vendor, '%'))";
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
		if (form.getId() != null) {
			DeliveryMain old = deliveryMainRepository.findOneById(form.getId());
			if (old != null) {
				main = old;
			}
		}

		Account account = this.getLoginAccount();

		if (form.getState() <= Constants.DELIVERY_STATE_SUBMIT) {
			main.setCode(form.getCode());
			main.setVendor(vendorRepository.findOneByCode(form.getVendor()));
			main.setCompany(companyRepository.findOneById(form.getCompany()));
			main.setStore(storeRepository.findOneById(form.getStore()));

			main.setEstimatedArrivalDate(Utils.parseDate(form.getEstimated_arrival_date()));
			main.setCreateDate(new Date());
			main.setCreater(account);
		} else {
			main.setConfirmDate(new Date());
			main.setConfirmer(account);
		}
		
		main.setState(form.getState());
		

		main = deliveryMainRepository.save(main);

		String action = null;
		List<Account> toList = new ArrayList<>();

		switch (form.getState()) {
		case Constants.DELIVERY_STATE_NEW:
			action = "保存";
			break;
		case Constants.DELIVERY_STATE_SUBMIT:
			action = "已发布";
			toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
			break;
		case Constants.DELIVERY_STATE_OK:
			action = "审批";
			toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
			break;
		case Constants.DELIVERY_STATE_CANCEL:
			action = "拒绝";
			toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
			break;
		case Constants.DELIVERY_STATE_PARTIAL_OK:
			action = "部分审批";
			toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
			break;
		}
		String title = String.format("预发货单【%s】已由【%s】%s，请及时查阅和处理！", main.getCode(), account.getRealname(), action);

		this.sendmessage(title, toList, String.format("/delivery/%s/read", main.getCode()));
		this.addOpertionHistory(main.getCode(), action, String.format("%s了预发货单", action));
		if (form.getState() <= Constants.DELIVERY_STATE_SUBMIT) {

			deliveryDetailRepository.deleteInBatch(deliveryDetailRepository.findDetailsByCode(main.getCode()));

			if (form.getTable() != null) {
				int rowNo = 1;
				for (Map<String, String> row : form.getTable()) {
					DeliveryDetail detail = new DeliveryDetail();
					detail.setMain(main);
					detail.setPurchaseOrderDetail(
							purchaseOrderDetailRepository.findOneById(Long.parseLong(row.get("po_detail_id"))));
					detail.setDeliveredQuantity(Double.parseDouble(row.get("delivered_quantity")));
					detail.setMemo(row.get("memo"));
					detail.setRowNo(rowNo);
					rowNo++;
					detail.setDeliverNumber(Utils.generateDeliveryNumber(form.getVendor()));

					if (form.getState() == Constants.DELIVERY_STATE_SUBMIT) {
						detail.setState(Constants.DELIVERY_ROW_STATE_OK);
					}
					detail = deliveryDetailRepository.save(detail);
				}
			}
		} else {
			if (form.getTable() != null) {
				boolean isAllOk = true;
				for (Map<String, String> row : form.getTable()) {
					DeliveryDetail detail = deliveryDetailRepository.findOneById(Long.parseLong(row.get("id")));
					detail.setBuyerMemo(row.get("buyer_memo"));

					Integer rowState = Integer.parseInt(row.get("state"));
					if (rowState == Constants.DELIVERY_ROW_STATE_CANCEL) {
						isAllOk = false;
					}

					if (form.getState() == Constants.DELIVERY_STATE_OK) {
						detail.setState(rowState);
					} else {
						detail.setState(Constants.DELIVERY_ROW_STATE_CANCEL);
					}

					detail = deliveryDetailRepository.save(detail);
				}

				if (!isAllOk) {
					main.setState(Constants.DELIVERY_STATE_PARTIAL_OK);
					main = deliveryMainRepository.save(main);
				}
			}
		}

		GenericJsonResponse<DeliveryMain> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null,
				main);

		return jsonResponse;
	}

	@GetMapping("/{ccode}/download")
	public ResponseEntity<Resource> download(@PathVariable("ccode") String ccode) {
		VenPriceAdjustMain main = venPriceAdjustMainRepository.findOneByCcode(ccode);
		if (main == null)
			show404();

		return download(Constants.PATH_UPLOADS_INQUERY + File.separator + main.getAttachFileName(),
				main.getAttachOriginalName());
	}

	private void checkPermission(DeliveryMain main, Long functionActionId) {
		if (this.isVendor()) {
			if (!main.getVendor().getCode().equals(this.getLoginAccount().getVendor().getCode())) {
				show403();
			}
		} else {

			AccountPermission accountPermission = this.getPermissionScopeOfFunction(functionActionId);
			List<Long> allowedCompanyIdList = accountPermission.getCompanyList();
			List<String> allowedVendorCodeList = accountPermission.getVendorList();
			List<Long> allowedStoreIdList = accountPermission.getStoreList();
			List<Long> allowedAccountIdList = accountPermission.getAccountList();
			
			boolean isValid = false;

			if (!(allowedCompanyIdList == null || allowedCompanyIdList.size() == 0)
					&& allowedCompanyIdList.contains(main.getCompany().getId())) {
				isValid = true;
			} else if (!(allowedVendorCodeList == null || allowedVendorCodeList.size() == 0)
					&& allowedVendorCodeList.contains(main.getVendor().getCode())) {
				isValid = true;
			} else if (!(allowedStoreIdList == null || allowedStoreIdList.size() == 0)
					&& allowedStoreIdList.contains(main.getStore().getId())) {
				isValid = true;
			} else if (!(allowedAccountIdList == null || allowedAccountIdList.size() == 0)
					&& main.getConfirmer() != null && allowedAccountIdList.contains(main.getConfirmer().getId())) {
				isValid = true;
			}

			if (!isValid) {
				show403();
			}
		}
	}
}
