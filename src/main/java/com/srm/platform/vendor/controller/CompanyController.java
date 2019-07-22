package com.srm.platform.vendor.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.Company;
import com.srm.platform.vendor.searchitem.SearchItem;
import com.srm.platform.vendor.searchitem.StoreSearchItem;

// 供应商管理
@Controller
@RequestMapping(path = "/company")
public class CompanyController extends CommonController {

	@ResponseBody
	@RequestMapping(value = "/search", produces = "application/json")
	public Page<SearchItem> search_ajax(@RequestParam(value = "q") String search) {
		PageRequest request = PageRequest.of(0, Integer.MAX_VALUE, Direction.ASC, "name");
		return companyRepository.findForSelect(request);
	}

	@PreAuthorize("hasAuthority('基础资料-查看列表')")
	@GetMapping({ "", "/" })
	public String index() {
		return "company/index";
	}

	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<Company> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		return companyRepository.findBySearchTerm(search, request);

	}
	
	@ResponseBody
	@RequestMapping(value = "/company/search", produces = "application/json")
	public Page<SearchItem> company_list(@RequestParam(value = "q") String search) {
		PageRequest request = PageRequest.of(0, 15, Direction.ASC, "name");
		return companyRepository.findForSelect(request);

	}
}
