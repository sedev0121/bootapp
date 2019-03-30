package com.srm.platform.vendor.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
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
//@PreAuthorize("hasAuthority('供应商管理-查看列表')")
public class InevntoryClassController extends CommonController {

	private static Long LIST_FUNCTION_ACTION_ID = 5L;

	@Autowired
	private InventoryClassRepository inventoryClassRepository;

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

		checkPermission(code, LIST_FUNCTION_ACTION_ID);
		
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

	private void checkPermission(String vendorCode, Long functionActionId) {
		AccountPermission accountPermission = this.getPermissionScopeOfFunction(functionActionId);
		boolean result = accountPermission.checkVendorPermission(vendorCode);
		if (!result) {
			//TODO:
//			show403();
		}
	}

}
