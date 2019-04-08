package com.srm.platform.vendor.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.searchitem.SearchItem;

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

}
