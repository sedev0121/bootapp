package com.srm.platform.vendor.controller;

import java.util.ArrayList;
import java.util.HashMap;
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

import com.srm.platform.vendor.model.InventoryClass;
import com.srm.platform.vendor.repository.InventoryClassRepository;
import com.srm.platform.vendor.searchitem.VendorSearchItem;
import com.srm.platform.vendor.utility.AccountPermission;

// 供应商管理
@Controller
@RequestMapping(path = "/inventoryclass")
@PreAuthorize("hasAuthority('基础资料-查看列表')")
public class InevntoryClassController extends CommonController {

	// 查询列表
	@GetMapping({ "", "/" })
	public String index() {
		return "inventoryclass/index";
	}

	// 修改
	@GetMapping("/{code}/edit")
	public String edit(@PathVariable("code") String code, Model model) {
		InventoryClass inventoryClass = inventoryClassRepository.findOneByCode(code);
		if (inventoryClass == null)
			show404();

		model.addAttribute("data", inventoryClass);
		return "inventoryclass/edit";
	}

	// 查询列表API
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<VendorSearchItem> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		Page<VendorSearchItem> result = null;
		result = inventoryClassRepository.findBySearchTerm(search, request);
		return result;
	}


	@GetMapping("/{parent_code}/children")
	public @ResponseBody List<Map<String, Object>> list_ajax(@PathVariable("parent_code") String parentCode) {
		if ("0".equals(parentCode)) {
			parentCode = null;
		}
		
		List<InventoryClass> children = inventoryClassRepository.findByParentCode(parentCode);

		InventoryClass temp;
		List<InventoryClass> tempChildren;

		Map<String, Object> row = new HashMap<>();
		List<Map<String, Object>> response = new ArrayList<>();
		for (int i = 0; i < children.size(); i++) {

			temp = children.get(i);
			tempChildren = inventoryClassRepository.findByParentCode(temp.getCode());
			row = new HashMap<>();
			row.put("id", temp.getCode());
			row.put("name", temp.getName());
			row.put("text", String.format("(%s) %s", temp.getCode(), temp.getName()));
			row.put("children", tempChildren.size() > 0 ? true : false);
			response.add(row);
		}

		return response;
	}

}
