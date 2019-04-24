package com.srm.platform.vendor.controller;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
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
import com.srm.platform.vendor.model.NegotiationDetail;
import com.srm.platform.vendor.model.NegotiationMain;
import com.srm.platform.vendor.model.PurchaseOrderMain;
import com.srm.platform.vendor.model.VenPriceAdjustMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.saveform.DeliverySaveForm;
import com.srm.platform.vendor.saveform.NegotiationSaveForm;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/negotiation")
//@PreAuthorize("hasRole('ROLE_VENDOR') or hasAuthority('询价管理-查看列表')")
public class NegotiationController extends CommonController {

	@Override
	protected String getOperationHistoryType() {
		return "negotiation";
	};
	
	// 查询列表
	@GetMapping({ "", "/" })
	public String index() {
		return "negotiation/index";
	}

	// 新建
//	@PreAuthorize("hasRole('ROLE_VENDOR')")
	@GetMapping({ "/{orderCode}/add" })
	public String add(@PathVariable("orderCode") String orderCode,Model model) {
		NegotiationMain main = new NegotiationMain();
		
		PurchaseOrderMain order = purchaseOrderMainRepository.findOneByCode(orderCode);
		
		if (order == null)
			show404();
		
		main.setVendor(order.getVendor());
		main.setOrderCode(orderCode);
		main.setMaker(getLoginAccount());
		
		model.addAttribute("main", main);
		return "negotiation/edit";
	}

	// 详细
	@GetMapping({ "/{code}/edit" })
	public String edit(@PathVariable("code") String code, Model model) {
		NegotiationMain main = negotiationMainRepository.findOneByCode(code);
		if (main == null)
			show404();

		// checkVendor(main.getVendor());

		model.addAttribute("main", main);
		return "negotiation/edit";
	}

	@RequestMapping(value = "/{code}/details/{orderCode}", produces = "application/json")
	public @ResponseBody List<NegotiationDetail> details_ajax(@PathVariable("code") String code, @PathVariable("orderCode") String orderCode) {
		List<NegotiationDetail> list = negotiationDetailRepository.findDetailsByCode(code);
		
		if (list.isEmpty()) {
			list = negotiationDetailRepository.findDetailsByOrderCode(code, orderCode);
		}

		return list;
	}

	@GetMapping({ "/{code}/read/{msgid}" })
	public String read(@PathVariable("code") String code, @PathVariable("msgid") Long msgid, Model model) {
		setReadDate(msgid);
		return "redirect:/delivery/" + code + "/edit";
	}
	
	@GetMapping("/{id}/deleteattach")
//	@PreAuthorize("hasAuthority('询价管理-新建/发布') or hasRole('ROLE_VENDOR')")
	public @ResponseBody Boolean deleteAttach(@PathVariable("id") Long id) {
		DeliveryMain main = deliveryMainRepository.findOneById(id);

//		File attach = new File(UploadFileHelper.getUploadDir(Constants.PATH_UPLOADS_INQUERY) + File.separator
//				+ main.getAttachFileName());
//		if (attach.exists())
//			attach.delete();
//		main.setAttachFileName(null);
//		main.setAttachOriginalName(null);
		deliveryMainRepository.save(main);
		return true;
	}

	// 删除API
//	@PreAuthorize("hasAuthority('询价管理-删除') or hasRole('ROLE_VENDOR')")
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
	public @ResponseBody Page<NegotiationMain> list_ajax(@RequestParam Map<String, String> requestParams) {
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

		if (isVendor()) {
			Vendor vendor = this.getLoginAccount().getVendor();
			vendorStr = vendor == null ? "0" : vendor.getCode();
			
			if (state > 0) {
				return negotiationMainRepository.findBySearchTermForVendor(code, vendorStr, state, request);
			}else {
				return negotiationMainRepository.findBySearchTermForVendor(code, vendorStr, request);
			}
		} else {
			if (state > 0) {
				return negotiationMainRepository.findBySearchTerm(code, vendorStr, state, request);
			}else {
				return negotiationMainRepository.findBySearchTerm(code, vendorStr, request);
			}
		}
	}

	// 更新API
	@Transactional
	@PostMapping("/update")
	public @ResponseBody GenericJsonResponse<NegotiationMain> update_ajax(NegotiationSaveForm form, Principal principal) {
		
		NegotiationMain main = new NegotiationMain();
		if (form.getCode() != null) {
			NegotiationMain old = negotiationMainRepository.findOneByCode(form.getCode());
			if (old != null) {
				main = old;
			}
		}
		
		main.setState(form.getState());
		main.setCode(form.getCode());
		PurchaseOrderMain purchaseOrderMain = purchaseOrderMainRepository.findOneByCode(form.getOrder_code());
		
		main.setVendor(purchaseOrderMain.getVendor());
		main.setOrderCode(purchaseOrderMain.getCode());
		main.setMakeDate(new Date());
		main.setMaker(getLoginAccount());

		main = negotiationMainRepository.save(main);
		
		String action = null;
		List<Account> toList = new ArrayList<>();
		
		switch (form.getState()) {
		case Constants.NEGOTIATION_STATE_NEW:
			action = "保存";			
			break;
		case Constants.NEGOTIATION_STATE_SUBMIT:
			action = "已发布";
			toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
			break;
		case Constants.NEGOTIATION_STATE_CONFIRM:
			action = "已报价";
			toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
			break;
		case Constants.NEGOTIATION_STATE_CANCEL:
			action = "退回";
			toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
			break;
		case Constants.NEGOTIATION_STATE_DONE:
			action = "已完成";
			toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
			break;
		}
		String title = String.format("议价单【%s】已由【%s】%s，请及时查阅和处理！", main.getCode(), getLoginAccount().getRealname(), action);

		this.sendmessage(title, toList, String.format("/negotiation/%s/read", main.getCode()));
		this.addOpertionHistory(main.getCode(), action, String.format("%s了议价单", action));
		
		if (form.getState() <= Constants.NEGOTIATION_STATE_SUBMIT) {

			negotiationDetailRepository.deleteInBatch(negotiationDetailRepository.findDetailsByCode(main.getCode()));
			
			if (form.getTable() != null) {				
				for (Map<String, String> row : form.getTable()) {
					NegotiationDetail detail = new NegotiationDetail();
					detail.setMain(main);	
					detail.setInventory(inventoryRepository.findOneByCode(row.get("inventory_code")));	
					detail.setMaxQuantity(Double.parseDouble(row.get("max_quantity")));
					detail.setPrice(Double.parseDouble(row.get("price")));
					detail.setTaxPrice(Double.parseDouble(row.get("tax_price")));
					detail.setTaxRate(Double.parseDouble(row.get("tax_rate")));
					detail.setStartDate(Utils.parseDate(row.get("start_date")));
					detail.setEndDate(Utils.parseDate(row.get("end_date")));
					detail.setValid(Integer.parseInt(row.get("valid")));
					detail.setMemo(row.get("memo"));				
					
					detail = negotiationDetailRepository.save(detail);
				}
			}
		} else {
			
		}
		
		GenericJsonResponse<NegotiationMain> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null,
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

}
