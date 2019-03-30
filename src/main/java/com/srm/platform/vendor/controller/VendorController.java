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

import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.searchitem.SearchItem;
import com.srm.platform.vendor.searchitem.VendorSearchItem;
import com.srm.platform.vendor.utility.AccountPermission;

// 供应商管理
@Controller
@RequestMapping(path = "/vendor")
@PreAuthorize("hasAuthority('供应商管理-查看列表')")
public class VendorController extends CommonController {

	private static Long LIST_FUNCTION_ACTION_ID = 5L;

	@Autowired
	private VendorRepository vendorRepository;

	// 查询列表
	@GetMapping({ "", "/" })
	public String index() {
		return "vendor/index";
	}

	// 修改
	@GetMapping("/{code}/edit")
	public String edit(@PathVariable("code") String code, Model model) {
		Vendor vendor = vendorRepository.findOneByCode(code);
		if (vendor == null)
			show404();

		checkPermission(code, LIST_FUNCTION_ACTION_ID);
		
		model.addAttribute("data", vendor);
		return "vendor/edit";
	}


	// 查询列表API
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<VendorSearchItem> list_ajax(@RequestParam Map<String, String> requestParams) {
		AccountPermission accountPermission = this.getPermissionScopeOfFunction(LIST_FUNCTION_ACTION_ID);
		List<String> allowedVendorCodeList = accountPermission.getVendorList();

		if (allowedVendorCodeList == null || allowedVendorCodeList.size() == 0) {
			return Page.empty();
		}
		
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		Page<VendorSearchItem> result = null;
		result = vendorRepository.findBySearchTerm(search, allowedVendorCodeList, request);
		return result;
	}

	// 查询列表API
	@RequestMapping(value = "/listall", produces = "application/json")
	public @ResponseBody Page<VendorSearchItem> list_all_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");

		if (order.equals("unitname")) {
			order = "b.name";
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		Page<VendorSearchItem> result = null;
		result = vendorRepository.findBySearchTerm(search, request);

		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/search", produces = "application/json")
	public Page<SearchItem> search_ajax(@RequestParam(value = "q") String search) {
		PageRequest request = PageRequest.of(0, 15, Direction.ASC, "name");
		return vendorRepository.findVendorNotCreatAccount(search, request);
	}
	
	private void checkPermission(String vendorCode, Long functionActionId) {
		AccountPermission accountPermission = this.getPermissionScopeOfFunction(functionActionId);
		boolean result = accountPermission.checkVendorPermission(vendorCode);
		if (!result) {
			show403();
		}
	}

}
