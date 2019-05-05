package com.srm.platform.vendor.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import com.srm.platform.vendor.model.Store;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.model.VendorClass;
import com.srm.platform.vendor.repository.StoreRepository;
import com.srm.platform.vendor.repository.VendorClassRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.searchitem.SearchItem;
import com.srm.platform.vendor.searchitem.StoreSearchItem;
import com.srm.platform.vendor.searchitem.VendorSearchItem;
import com.srm.platform.vendor.utility.AccountPermission;
import com.srm.platform.vendor.utility.Utils;

// 供应商管理
@Controller
@RequestMapping(path = "/store")
public class StoreController extends CommonController {

	@PreAuthorize("hasAuthority('基础资料-查看列表')")
	@GetMapping({ "", "/" })
	public String index() {
		return "store/index";
	}

	@PreAuthorize("hasAuthority('基础资料-查看列表')")
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable("id") Long id, Model model) {
		Store store = storeRepository.findOneById(id);
		if (store == null)
			show404();

		model.addAttribute("data", store);
		return "store/edit";
	}


	// 查询列表API
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<Store> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");
		String used = requestParams.getOrDefault("used", "-1");
		String company = requestParams.getOrDefault("company", "-1");

		if (Utils.isEmpty(company)) {
			company = "-1";
		}
		
		if (order.equals("company.name")) {
			order = "b.name";
		}
		
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		Page<Store> result = null;
		
		Integer usedState = Integer.parseInt(used);
		Long companyId = Long.parseLong(company);
		
		if (usedState > -1) {			
			if (companyId > -1) {
				result = storeRepository.findByUsedAndCompany(search, usedState, companyId, request);	
			} else {
				result = storeRepository.findBySearchTerm(search, usedState, request);	
			}
			
		} else {			
			if (companyId > -1) {
				result = storeRepository.findBySearchTermAndCompany(search, companyId, request);	
			} else {
				result = storeRepository.findBySearchTerm(search, request);	
			}
		}
		
		
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/search", produces = "application/json")
	public Page<SearchItem> search_ajax(@RequestParam(value = "q") String search) {
		PageRequest request = PageRequest.of(0, Integer.MAX_VALUE, Direction.ASC, "name");
		return storeRepository.findForSelect(search, request);
	}
	
	@ResponseBody
	@RequestMapping(value = "/search/{company_id}", produces = "application/json")
	public Page<SearchItem> searchOfCompany(@PathVariable("company_id") Long companyId, @RequestParam(value = "q") String search) {
		PageRequest request = PageRequest.of(0, Integer.MAX_VALUE, Direction.ASC, "name");
		return storeRepository.findForSelectOfCompany(companyId, search, request);
	}
	

}
