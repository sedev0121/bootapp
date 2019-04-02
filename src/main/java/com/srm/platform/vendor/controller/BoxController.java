package com.srm.platform.vendor.controller;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Box;
import com.srm.platform.vendor.model.BoxClass;
import com.srm.platform.vendor.model.InventoryClass;
import com.srm.platform.vendor.model.PermissionGroupUser;
import com.srm.platform.vendor.model.PermissionUserScope;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.BoxClassRepository;
import com.srm.platform.vendor.repository.BoxRepository;
import com.srm.platform.vendor.repository.InventoryClassRepository;
import com.srm.platform.vendor.saveform.AccountSaveForm;
import com.srm.platform.vendor.searchitem.BoxSearchResult;
import com.srm.platform.vendor.searchitem.StatementSearchResult;
import com.srm.platform.vendor.searchitem.VendorSearchItem;
import com.srm.platform.vendor.utility.AccountPermission;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.Utils;

// 供应商管理
@Controller
@RequestMapping(path = "/box")
// @PreAuthorize("hasAuthority('供应商管理-查看列表')")
public class BoxController extends CommonController {

	private static Long LIST_FUNCTION_ACTION_ID = 5L;

	@Autowired
	private BoxClassRepository boxClassRepository;

	@Autowired
	private BoxRepository boxRepository;

	// 查询列表
	@GetMapping({ "", "/" })
	public String index() {
		return "box/list";
	}

	@RequestMapping(value = "/class/list", produces = "application/json")
	public @ResponseBody List<BoxClass> list_class(@RequestParam Map<String, String> requestParams) {
		String search = requestParams.getOrDefault("search", "");
		return boxClassRepository.findBySearchTerm(search);
	}

	// 查询列表API
	@RequestMapping(value = "/list/{classId}", produces = "application/json")
	public @ResponseBody Page<BoxSearchResult> list_ajax(@PathVariable("classId") Long classId,
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

		String selectQuery = "select * ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "from box where box_class_id= " + classId + " ";

		Map<String, Object> params = new HashMap<>();

		if (!code.trim().isEmpty()) {
			bodyQuery += " and code like CONCAT('%',:code, '%') ";
			params.put("code", code.trim());
		}

		int temp = Integer.parseInt(state);
		if (temp > -1) {
			bodyQuery += " and state=:state";
			params.put("state", temp);
		}

		temp = Integer.parseInt(used);
		if (temp > -1) {
			bodyQuery += " and used=:used";
			params.put("used", temp);
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "BoxSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();
		return new PageImpl<BoxSearchResult>(list, request, totalCount.longValue());
	}

	@Transactional
	@PostMapping("/update")
	public @ResponseBody GenericJsonResponse<Box> update_ajax(@RequestParam Map<String, String> requestParams) {

		Box box = new Box();
		String id = requestParams.get("id");
		String spec = requestParams.get("spec");
		String memo = requestParams.get("memo");
		String classId = requestParams.get("class_id");

		if (id != null && !id.isEmpty()) {
			box = boxRepository.findOneById(Long.valueOf(id));
		} else {
			box.setCode(Utils.generateId());
			box.setState(1);
			box.setUsed(0);
			box.setBoxClass(boxClassRepository.findOneById(Long.valueOf(classId)));
		}

		box.setSpec(spec);
		box.setMemo(memo);

		box = boxRepository.save(box);

		GenericJsonResponse<Box> jsonResponse;

		jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, box);

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
			}
			
			box = boxRepository.save(box);
		}

		GenericJsonResponse<Box> jsonResponse;

		jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, null);

		return jsonResponse;
	}

	private void checkPermission(String vendorCode, Long functionActionId) {
		AccountPermission accountPermission = this.getPermissionScopeOfFunction(functionActionId);
		boolean result = accountPermission.checkVendorPermission(vendorCode);
		if (!result) {
			// TODO:
			// show403();
		}
	}

}
