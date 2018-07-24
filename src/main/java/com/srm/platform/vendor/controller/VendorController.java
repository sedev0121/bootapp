package com.srm.platform.vendor.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.thymeleaf.util.StringUtils;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.UnitRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.utility.SearchItem;
import com.srm.platform.vendor.utility.VendorSearchItem;

// 供应商管理
@Controller
@RequestMapping(path = "/vendor")

public class VendorController extends CommonController {

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private UnitRepository unitRepository;

	// 查询列表
	@PreAuthorize("hasRole('ROLE_BUYER') or hasRole('ROLE_ADMIN')")
	@GetMapping({ "", "/" })
	public String index() {
		return "vendor/index";
	}

	// 详细
	@PreAuthorize("hasRole('ROLE_BUYER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/{code}/edit")
	public String edit(@PathVariable("code") String code, Model model) {
		Vendor vendor = vendorRepository.findOneByCode(code);
		if (vendor == null)
			show404();

		if (!isAdmin()) {
			checkVendor(vendor);
		}

		List<Account> accountList = accountRepository.findAccountsByVendor(vendor.getCode());
		model.addAttribute("data", vendor);
		model.addAttribute("accounts", accountList);
		return "vendor/edit";
	}

	// 查询列表API
	@PreAuthorize("hasRole('ROLE_BUYER') or hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<VendorSearchItem> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");

		List<String> unitList = this.getDefaultUnitList();
		logger.info(unitList.toString());

		if (order.equals("unitname")) {
			order = "b.name";
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		Page<VendorSearchItem> result = null;
		if (isAdmin())
			result = vendorRepository.findBySearchTerm(search, request);
		else
			result = vendorRepository.findBySearchTerm(search, unitList, request);

		return result;
	}

	@PreAuthorize("hasRole('ROLE_BUYER') or hasRole('ROLE_VENDOR') or hasRole('ROLE_ADMIN')")
	@ResponseBody
	@RequestMapping(value = "/search", produces = "application/json")
	public Page<SearchItem> search_ajax(@RequestParam(value = "q") String search) {
		PageRequest request = PageRequest.of(0, 15, Direction.ASC, "name");

		if (isAdmin()) {
			return vendorRepository.findForSelect(search, request);
		} else {
			List<String> unitList = this.getDefaultUnitList();
			return vendorRepository.findForSelect(unitList, search, request);
		}

	}

	// 修改
	@Transactional
	@PostMapping("/update")
	public @ResponseBody Vendor update_ajax(@RequestParam Map<String, String> requestParams) {
		String code = requestParams.get("code");

		String unit = requestParams.get("unit");

		Vendor vendor = vendorRepository.findOneByCode(code);

		vendor.setUnit(unitRepository.findOneById(Long.parseLong(unit)));

		vendorRepository.save(vendor);

		return vendor;
	}

	// 修改
	@Transactional
	@PostMapping("/set_unit")
	public @ResponseBody Integer setUnit(@RequestParam Map<String, String> requestParams) {
		String vendorList = requestParams.get("vendor_list");

		String unitId = requestParams.get("unit_id");

		String[] vendorCodeList = StringUtils.split(vendorList, ",");

		for (String vendorCode : vendorCodeList) {
			Vendor vendor = vendorRepository.findOneByCode(vendorCode);
			vendor.setUnit(unitRepository.findOneById(Long.parseLong(unitId)));
			vendorRepository.save(vendor);
		}

		return 1;
	}
}
