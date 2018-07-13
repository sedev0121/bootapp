package com.srm.platform.vendor.controller;

import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
import com.srm.platform.vendor.utility.InventorySearchItem;

//商品档案表
@Controller
@RequestMapping(path = "/inventory")
@PreAuthorize("hasRole('ROLE_BUYER')")
public class InventoryController {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private InventoryRepository inventoryRepository;

	// 查询列表
	@GetMapping({ "/", "" })
	public String index() {
		return "inventory/index";
	}

	// 详细
	@GetMapping("/{code}/edit")
	public String edit(@PathVariable("code") String code, Model model) {
		Inventory data = new Inventory();
		data.setCode(code);
		Example<Inventory> example = Example.of(data);
		Optional<Inventory> result = inventoryRepository.findOne(example);
		model.addAttribute("data", result.isPresent() ? result.get() : new Inventory());
		return "inventory/edit";
	}

	// 查询列表API
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<Inventory> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);
		Page<Inventory> result = inventoryRepository.findBySearchTerm(search, request);

		return result;
	}

	@RequestMapping(value = "/select", produces = "application/json")
	public @ResponseBody Page<InventorySearchItem> inventory_list_for_select_ajax(
			@RequestParam(value = "inv") String invName, @RequestParam(value = "vendor") String vendorCode) {
		PageRequest request = PageRequest.of(0, 15, Direction.ASC, "name");
		Page<InventorySearchItem> list = inventoryRepository.findSelectListBySearchTerm(vendorCode, invName, request);

		return list;
	}

}
