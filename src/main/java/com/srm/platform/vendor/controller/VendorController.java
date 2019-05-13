package com.srm.platform.vendor.controller;

import java.util.Map;

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
import com.srm.platform.vendor.searchitem.SearchItem;
import com.srm.platform.vendor.searchitem.VendorSearchItem;

// 供应商管理
@Controller
@RequestMapping(path = "/vendor")

public class VendorController extends CommonController {

	// 查询列表
	@PreAuthorize("hasAuthority('基础资料-查看列表')")
	@GetMapping({ "", "/" })
	public String index() {
		return "vendor/index";
	}

	// 修改
	@PreAuthorize("hasAuthority('基础资料-查看列表')")
	@GetMapping("/{code}/edit")
	public String edit(@PathVariable("code") String code, Model model) {
		Vendor vendor = vendorRepository.findOneByCode(code);
		if (vendor == null)
			show404();

		model.addAttribute("data", vendor);
		return "vendor/edit";
	}


	// 查询列表API
	@PreAuthorize("hasAuthority('基础资料-查看列表')")
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<Vendor> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String vendor = requestParams.getOrDefault("vendor", "");
		String vendorClass = requestParams.getOrDefault("vendor_class", "");

		if (order.equals("vendorClass.code")) {
			order = "b.code";
		}		
		
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		Page<Vendor> result = null;
		result = vendorRepository.findVendorsBySearchTerm(vendor, vendorClass, request);
		return result;
	}

	// 查询列表API
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('基础资料-查看列表')")
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

	@PreAuthorize("hasAuthority('基础资料-查看列表')")
	@ResponseBody
	@RequestMapping(value = "/search", produces = "application/json")
	public Page<SearchItem> search_ajax(@RequestParam(value = "q") String search) {
		PageRequest request = PageRequest.of(0, 15, Direction.ASC, "name");
		return vendorRepository.findVendorNotCreatAccount(search, request);
	}

}
