package com.srm.platform.vendor.controller;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import com.srm.platform.vendor.model.Box;
import com.srm.platform.vendor.model.BoxClass;
import com.srm.platform.vendor.model.Store;
import com.srm.platform.vendor.searchitem.BoxSearchResult;
import com.srm.platform.vendor.searchitem.SearchItem;
import com.srm.platform.vendor.utility.AccountPermission;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.Utils;

// 供应商管理
@Controller
@RequestMapping(path = "/box")
// @PreAuthorize("hasAuthority('供应商管理-查看列表')")
@PreAuthorize("hasRole('ROLE_BUYER') and hasAuthority('箱码管理-查看列表')")
public class BoxController extends CommonController {

	@GetMapping({ "", "/" })
	public String index() {
		return "box/list";
	}

	@ResponseBody
	@RequestMapping(value = "/class/search", produces = "application/json")
	public Page<SearchItem> search_ajax(@RequestParam(value = "q") String search) {
		PageRequest request = PageRequest.of(0, Integer.MAX_VALUE, Direction.ASC, "name");
		return boxClassRepository.findForSelect(search, request);
	}
	
	@RequestMapping(value = "/class/list", produces = "application/json")
	public @ResponseBody List<BoxClass> list_class(@RequestParam Map<String, String> requestParams) {
		String search = requestParams.getOrDefault("search", "");
		return boxClassRepository.findBySearchTerm(search);
	}

	@RequestMapping(value = "/list/{classId}", produces = "application/json")
	public @ResponseBody Page<Box> list_ajax(@PathVariable("classId") Long classId,
			@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "code");
		String dir = requestParams.getOrDefault("dir", "asc");
		String code = requestParams.getOrDefault("code", "");
		String state = requestParams.getOrDefault("state", "-1");
		String used = requestParams.getOrDefault("used", "-1");

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);
		Page<Box> result = null;
		
		Integer boxState = Integer.parseInt(state);
		Integer usedState = Integer.parseInt(used);
		if (boxState > -1) {
			if (usedState > -1) {
				result = boxRepository.findBySearchUsedAndState(code, usedState, boxState, classId, request);	
			} else {
				result = boxRepository.findBySearchAndState(code, boxState, classId, request);	
			}
				
		} else {
			if (usedState > -1) {
				result = boxRepository.findBySearchAndUsed(code, usedState, classId, request);	
			} else {
				result = boxRepository.findBySearchTerm(code, classId, request);	
			}
		}
		
		return result;
	}

	@Transactional
	@PostMapping("/update")
	public @ResponseBody GenericJsonResponse<Box> update_ajax(@RequestParam Map<String, String> requestParams) {

		GenericJsonResponse<Box> jsonResponse;
		jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, null);
		
		String serialNumberStr = requestParams.get("serial_number");
		String countStr = requestParams.get("count");
		String classId = requestParams.get("class_id");

		if (classId == null) {
			jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, "请选择箱码分类！", null);
		} else {
			BoxClass boxClass = boxClassRepository.findOneById(Long.valueOf(classId));
			if (boxClass == null) {
				jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, "箱码分类不存在", null);
			} else {
				Integer count = Integer.valueOf(countStr);
				List<String> boxCodeList = Utils.generateStaticBoxCode(boxClass.getCode(), serialNumberStr, count);
				for(String code : boxCodeList) {
					Box box = new Box();
					box.setCode(code);
					box.setState(1);
					box.setUsed(0);
					box.setBoxClass(boxClass);
					box = boxRepository.save(box);		
				}
			}
		}

		return jsonResponse;
	}

	@Transactional
	@PostMapping("/update_state")
	public @ResponseBody GenericJsonResponse<Box> update_state(@RequestParam Map<String, String> requestParams) {

		Box box = new Box();
		String key = requestParams.get("key");
		String ids = requestParams.get("ids");

		List<String> boxIds = Arrays.asList(StringUtils.split(ids, ","));
		for (String id : boxIds) {
			box = boxRepository.findOneById(Long.valueOf(id));
			if ("enable".equals(key)) {
				box.setState(1);
			} else if ("disable".equals(key)) {
				box.setState(0);
			} else if ("empty".equals(key)) {
				box.setUsed(0);
				box.setBindDate(null);
				box.setBindProperty(null);
				box.setDelivery(null);
				box.setQuantity(null);
			}

			box = boxRepository.save(box);
		}

		GenericJsonResponse<Box> jsonResponse;

		jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, null);

		return jsonResponse;
	}

}
