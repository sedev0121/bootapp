package com.srm.platform.vendor.controller;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.searchitem.InventorySearchItem;
import com.srm.platform.vendor.utility.AccountPermission;

//商品档案表
@Controller
@RequestMapping(path = "/inventory")
@PreAuthorize("hasAuthority('商品管理-查看列表')")
public class InventoryController extends CommonController {

	private static Long LIST_FUNCTION_ACTION_ID = 31L;
	
	@PersistenceContext
	private EntityManager em;


	// 查询列表
	@GetMapping({ "/", "" })
	public String index() {
		return "inventory/index";
	}

	// 详细
	@GetMapping("/{code}/edit")
	public String edit(@PathVariable("code") String code, Model model) {
		Inventory main = inventoryRepository.findOneByCode(code);
		if (main == null)
			show404();
		
		//TODO: need to get inventory class code
//		checkPermission(main.getCode(), LIST_FUNCTION_ACTION_ID);
		
		model.addAttribute("data", main);
		return "inventory/edit";
	}

	// 查询列表API
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<Inventory> list_ajax(@RequestParam Map<String, String> requestParams) {
		AccountPermission accountPermission = this.getPermissionScopeOfFunction(LIST_FUNCTION_ACTION_ID);
		List<String> allowedInventoryClassCodeList = accountPermission.getInventoryClassList();
		
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");

		if ("inventoryClass.name".equals(order)) {
			order = "b.name";
		}
		
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);
		Page<Inventory> result = inventoryRepository.findBySearchTerm(search, allowedInventoryClassCodeList, request);

		return result;
	}
	
	private void checkPermission(String inventoryClassCode, Long functionActionId) {
		AccountPermission accountPermission = this.getPermissionScopeOfFunction(functionActionId);
		boolean result = accountPermission.checkInventoryClassPermission(inventoryClassCode);
		if (!result) {
			show403();
		}
	}

}
