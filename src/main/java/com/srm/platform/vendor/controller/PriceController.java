package com.srm.platform.vendor.controller;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.srm.platform.vendor.model.Price;
import com.srm.platform.vendor.repository.PriceRepository;

@Controller
@RequestMapping(path = "/price")
@PreAuthorize("hasRole('ROLE_BUYER')")
public class PriceController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PriceRepository priceRepository;

	// 查询列表
	@GetMapping({ "", "/" })
	public String index() {
		return "price/index";
	}

	// 详细
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable("id") Long id, Model model) {

		Optional<Price> result = priceRepository.findById(id);

		model.addAttribute("data", result.isPresent() ? result.get() : new Price());
		return "price/edit";
	}

	// 查询列表API
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<Price> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search_vendor = requestParams.getOrDefault("vendor", "");
		String search_inventory = requestParams.getOrDefault("inventory", "");
		String startDate = requestParams.getOrDefault("start", "");
		String endDate = requestParams.getOrDefault("end", "");

		switch (order) {
		case "vendor.name":
			order = "b.name";
			break;
		case "cinvcode.name":
			order = "c.name";
			break;
		}
		logger.info(startDate + " ~ " + endDate);
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);
		Page<Price> result = priceRepository.findBySearchTerm(search_vendor, search_inventory, startDate, endDate,
				request);

		return result;
	}
}
