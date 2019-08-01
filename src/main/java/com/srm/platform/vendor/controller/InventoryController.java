package com.srm.platform.vendor.controller;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
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

import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.searchitem.InventoryCheckItem;
import com.srm.platform.vendor.utility.GenericJsonResponse;

//商品档案表
@Controller
@RequestMapping(path = "/inventory")
public class InventoryController extends CommonController {

	@PersistenceContext
	private EntityManager em;


	@GetMapping({ "/", "" })
	@PreAuthorize("hasAuthority('基础资料-查看列表')")
	public String index() {
		return "inventory/index";
	}

	@GetMapping("/{code}/edit")
	@PreAuthorize("hasAuthority('基础资料-查看列表')")
	public String edit(@PathVariable("code") String code, Model model) {
		Inventory main = inventoryRepository.findOneByCode(code);
		if (main == null)
			show404();
		
		model.addAttribute("data", main);
		return "inventory/edit";
	}

	@PreAuthorize("hasAuthority('基础资料-查看列表')")
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<Inventory> list_ajax(@RequestParam Map<String, String> requestParams) {
		
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String inventory = requestParams.getOrDefault("inventory", "");
		String inventoryClass = requestParams.getOrDefault("inventory_class", "");
		String boxClass = requestParams.getOrDefault("box_class", "");

		if ("inventoryClass.name".equals(order)) {
			order = "b.name";
		}else if ("boxClass.name".equals(order)) {
			order = "c.name";
		}
		
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);
		
		Page<Inventory> result;
		if (boxClass.isEmpty()) {
			result = inventoryRepository.findBySearchTerm(inventory, inventoryClass, request);
		} else {
			result = inventoryRepository.findBySearchTerm(inventory, inventoryClass, boxClass, request);	
		}
		

		return result;
	}
	
	@Transactional
	@PreAuthorize("hasAuthority('基础资料-查看列表')")
	@PostMapping("/update")
	public @ResponseBody GenericJsonResponse<Inventory> update_ajax(@RequestParam Map<String, String> requestParams) {

		String code = requestParams.get("code");
		String boxIdStr = requestParams.get("box");
		String countPerBoxStr = requestParams.get("count_per_box");
		String extraRateStr = requestParams.get("extra_rate");
		
		Inventory main = inventoryRepository.findOneByCode(code);

		GenericJsonResponse<Inventory> jsonResponse;
		
		
		if (main == null) {
			jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, null, main);
		} else {
			main.setBoxClass(boxClassRepository.findOneById(Long.parseLong(boxIdStr)));
			main.setCountPerBox(Integer.parseInt(countPerBoxStr));
			main.setExtraRate(Double.valueOf(extraRateStr));
			main = inventoryRepository.save(main);
			jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, main);
		}		

		return jsonResponse;
	}
	
	@RequestMapping(value = "/check", produces = "application/json")
	public @ResponseBody List<InventoryCheckItem> check(@RequestParam Map<String, String> requestParams) {
		
		String inventoryStr = requestParams.get("codeList");
		String[] inventoryList = inventoryStr.split(",");
		List<InventoryCheckItem> result = inventoryRepository.checkCodes(inventoryList);		

		return result;
	}

}
