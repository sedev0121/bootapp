package com.srm.platform.vendor.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.DeliveryDetail;
import com.srm.platform.vendor.model.DeliveryMain;
import com.srm.platform.vendor.model.Notice;
import com.srm.platform.vendor.model.PurchaseInDetail;
import com.srm.platform.vendor.model.StatementDetail;
import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.model.VenPriceAdjustDetail;
import com.srm.platform.vendor.model.VenPriceAdjustMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.saveform.DeliverySaveForm;
import com.srm.platform.vendor.saveform.VenPriceSaveForm;
import com.srm.platform.vendor.searchitem.InquerySearchResult;
import com.srm.platform.vendor.searchitem.VenPriceDetailItem;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.U8InvoicePostData;
import com.srm.platform.vendor.utility.U8InvoicePostEntry;
import com.srm.platform.vendor.utility.UploadFileHelper;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/delivery")
//@PreAuthorize("hasRole('ROLE_VENDOR') or hasAuthority('询价管理-查看列表')")
public class DeliveryController extends CommonController {

	// 查询列表
	@GetMapping({ "", "/" })
	public String index() {
		return "delivery/index";
	}

	// 新建
//	@PreAuthorize("hasAuthority('询价管理-新建/发布') or hasRole('ROLE_VENDOR')")
	@GetMapping({ "/add" })
	public String add(Model model) {
		DeliveryMain main = new DeliveryMain();
		model.addAttribute("main", main);
		return "delivery/edit";
	}

	// 详细
	@GetMapping({ "/{code}/edit" })
	public String edit(@PathVariable("code") String code, Model model) {
		DeliveryMain main = deliveryMainRepository.findOneByCode(code);
		if (main == null)
			show404();

		// checkVendor(main.getVendor());

		model.addAttribute("main", main);
		return "delivery/edit";
	}

	@RequestMapping(value = "/{code}/details", produces = "application/json")
	public @ResponseBody List<DeliveryDetail> details_ajax(@PathVariable("code") String code) {
		List<DeliveryDetail> list = deliveryDetailRepository.findDetailsByCode(code);

		return list;
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
	@GetMapping("/{id}/delete")
	@Transactional
	public @ResponseBody Boolean delete_ajax(@PathVariable("id") Long id) {
		DeliveryMain main = deliveryMainRepository.findOneById(id);
		if (main != null) {
			deliveryDetailRepository.DeleteByMainId(id);
			deliveryMainRepository.delete(main);
		}

		return true;
	}

	// 查询列表API
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<DeliveryMain> list_ajax(@RequestParam Map<String, String> requestParams) {
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
				return deliveryMainRepository.findBySearchTermForVendor(code, vendorStr, state, request);
			}else {
				return deliveryMainRepository.findBySearchTermForVendor(code, vendorStr, request);
			}
		} else {
			if (state > 0) {
				return deliveryMainRepository.findBySearchTerm(code, vendorStr, state, request);
			}else {
				return deliveryMainRepository.findBySearchTerm(code, vendorStr, request);
			}
		}
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
		
		main.setCode(form.getCode());
		main.setVendor(vendorRepository.findOneByCode(form.getVendor()));
		main.setCompany(companyRepository.findOneById(form.getCompany()));
		main.setStore(storeRepository.findOneById(form.getStore()));
		
		main.setEstimatedArrivalDate(Utils.parseDate(form.getEstimated_arrival_date()));
		main.setCreateDate(new Date());
		Account account = this.getLoginAccount();
		main.setCreater(account);

		deliveryMainRepository.save(main);
		
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

}
