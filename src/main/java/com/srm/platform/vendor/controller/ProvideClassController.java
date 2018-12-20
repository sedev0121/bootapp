package com.srm.platform.vendor.controller;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Notice;
import com.srm.platform.vendor.model.NoticeRead;
import com.srm.platform.vendor.model.ProvideClass;
import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.NoticeSearchResult;
import com.srm.platform.vendor.utility.ProvideClassSearchResult;
import com.srm.platform.vendor.utility.SearchItem;
import com.srm.platform.vendor.utility.UploadFileHelper;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/provideclass")

public class ProvideClassController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// 列表
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping({ "/", "" })
	public String index(Model model) {
		return "admin/provide_class/list";
	}

	@GetMapping("/add")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String add(Model model) {
		ProvideClass item = new ProvideClass();
		model.addAttribute("item", item);
		return "admin/provide_class/edit";
	}

	// 修改
	@GetMapping("/{id}/edit")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String edit(@PathVariable("id") Long id, Model model) {
		ProvideClass item = provideClassRepository.findOneById(id);

		if (item == null) {
			this.show404();
		}
		model.addAttribute("item", item);
		return "admin/provide_class/edit";
	}

	// 用户管理->列表
	@GetMapping("/list")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public @ResponseBody Page<ProvideClassSearchResult> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "code");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		String selectQuery = "SELECT * ";
		String countQuery = "select count(id) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "FROM provide_class";

		Map<String, Object> params = new HashMap<>();

		if (!search.trim().isEmpty()) {
			bodyQuery += " where name like CONCAT('%',:search, '%') or code like CONCAT('%',:search, '%') ";
			params.put("search", search.trim());
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "ProvideClassSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<ProvideClassSearchResult>(list, request, totalCount.longValue());
	}

	// 修改
	@Transactional
	@PostMapping("/update")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public @ResponseBody GenericJsonResponse<ProvideClass> update_ajax(
			@RequestParam Map<String, String> requestParams) {

		GenericJsonResponse<ProvideClass> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null,
				null);

		String id = requestParams.get("id");
		String code = requestParams.get("code");
		String name = requestParams.get("name");

		Long idKey = (id != null && !id.isEmpty()) ? Long.valueOf(id) : 0L;

		List<ProvideClass> duplicatedItems = provideClassRepository.findDuplicatedCode(Long.valueOf(code), idKey);
		if (duplicatedItems.size() > 0) {
			jsonResponse.setSuccess(GenericJsonResponse.FAILED);
			jsonResponse.setErrmsg("供货类别编码重复");
		} else {
			ProvideClass item = new ProvideClass();

			if (id != null && !id.isEmpty()) {
				item = provideClassRepository.findOneById(Long.parseLong(id));
			}

			item.setCode(Long.parseLong(code));
			item.setName(name);
			item = provideClassRepository.save(item);

			jsonResponse.setData(item);
		}

		return jsonResponse;

	}

	// 删除
	@GetMapping("/{id}/delete")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public @ResponseBody GenericJsonResponse<ProvideClass> delete(@PathVariable("id") Long id, Model model) {
		
		GenericJsonResponse<ProvideClass> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null,
				null);
		
		List<ProvideClass> usingList = provideClassRepository.findListUsingId(id);
		if (usingList.size() > 0) {
			jsonResponse.setSuccess(GenericJsonResponse.FAILED);
			jsonResponse.setErrmsg("供货类别正在使用当中");
		}else {
			ProvideClass item = provideClassRepository.findOneById(id);
			provideClassRepository.delete(item);

			jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, item);
		}
		

		return jsonResponse;
	}
	
	@ResponseBody
	@PreAuthorize("hasRole('ROLE_BUYER') or hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/search", produces = "application/json")
	public Page<SearchItem> search_ajax(@RequestParam(value = "q") String search) {
		PageRequest request = PageRequest.of(0, 15, Direction.ASC, "name");

		return provideClassRepository.findForSelect(search, request);

	}
}
