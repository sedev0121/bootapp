package com.srm.platform.vendor.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.PermissionGroup;
import com.srm.platform.vendor.model.PermissionGroupUser;
import com.srm.platform.vendor.model.PermissionUserScope;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.CompanyRepository;
import com.srm.platform.vendor.repository.PermissionGroupRepository;
import com.srm.platform.vendor.repository.PermissionGroupUserRepository;
import com.srm.platform.vendor.repository.PermissionUserScopeRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.saveform.AccountSaveForm;
import com.srm.platform.vendor.searchitem.AccountSearchItem;
import com.srm.platform.vendor.searchitem.AccountSearchResult;
import com.srm.platform.vendor.searchitem.PermissionScopeOfAccount;
import com.srm.platform.vendor.searchitem.SearchItem;
import com.srm.platform.vendor.searchitem.SellerSearchResult;
import com.srm.platform.vendor.utility.GenericJsonResponse;

@Controller
@RequestMapping(path = "/seller")
public class SellerController extends AccountController {
	

	// 用户管理->列表
	@GetMapping({ "/", "" })
	public String buyer(Model model) {
		return "admin/seller/list";
	}
	
	// 用户管理->列表
	@GetMapping("/list")
	public @ResponseBody Page<SellerSearchResult> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");
		String stateStr = requestParams.getOrDefault("state", "");

		Integer state = Integer.parseInt(stateStr);


		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		String selectQuery = "SELECT t.id, t.username, t.state, v.*, v.name vendorname ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "FROM account t left join vendor v on t.vendor_code=v.code where t.role='ROLE_VENDOR' ";

		Map<String, Object> params = new HashMap<>();

		if (!search.trim().isEmpty()) {
			bodyQuery += " and (v.name LIKE CONCAT('%',:search, '%') or v.phone LIKE CONCAT('%',:search, '%') or v.mobile LIKE CONCAT('%',:search, '%') or v.abbrname LIKE CONCAT('%',:search, '%') or t.username LIKE CONCAT('%',:search, '%') or v.email LIKE CONCAT('%',:search, '%')) ";
			params.put("search", search.trim());
		}

		if (state >= 0) {
			bodyQuery += " and state=:state";
			params.put("state", state);
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "SellerSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<SellerSearchResult>(list, request, totalCount.longValue());

	}

	// 用户管理->修改
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable("id") Long id, Model model) {
		Account account = accountRepository.findOneById(id);
		if (account == null)
			show404();

		PermissionGroupUser temp = new PermissionGroupUser();
		temp.setAccountId(account.getId());

		Example<PermissionGroupUser> example = Example.of(temp);
		List<PermissionGroupUser> resultList = permissionGroupUserRepository.findAll(example);

		List<PermissionGroup> groupList = new ArrayList<>();
		for (PermissionGroupUser group : resultList) {
			PermissionGroup item = permissionGroupRepository.findOneById(group.getGroupId());
			if (item != null)
				groupList.add(item);
		}

		List<PermissionScopeOfAccount> scopeList = this.permissionGroupRepository.findScopeListOfAccount(id);
		ObjectMapper mapper = new ObjectMapper();
		String jsonScopeListString = "";
		try {
			jsonScopeListString = mapper.writeValueAsString(scopeList);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		model.addAttribute("account", account);
		model.addAttribute("permission_scope", jsonScopeListString);
		return "admin/seller/edit";
	}

	// 用户管理->新建
	@GetMapping("/add")
	public String add(Model model) {
		model.addAttribute("account", new Account());
		model.addAttribute("groupList", "[]");
		return "admin/seller/edit";
	}

	

	// 用户修改
	@Transactional
	@PostMapping("/update")
	public @ResponseBody GenericJsonResponse<Account> update_ajax(AccountSaveForm accountSaveForm) {

		Account account = new Account();
		if (accountSaveForm.getId() != null) {
			account = accountRepository.findOneById(accountSaveForm.getId());
		}

		account.setUsername(accountSaveForm.getUsername());
		account.setRole("ROLE_VENDOR");
		boolean isDuplicatedVendor = false;
		List<Account> accountsHavingVendorCode = accountRepository.findAccountsByVendor(accountSaveForm.getVendor());
		for (Account vendorAccount : accountsHavingVendorCode) {
			if (vendorAccount.getId() != accountSaveForm.getId()) {
				logger.info(String.format("%d = %d", vendorAccount.getId(), accountSaveForm.getId()));
				isDuplicatedVendor = true;
				break;
			}
		}

		GenericJsonResponse<Account> jsonResponse;

		if (isDuplicatedVendor) {
			jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, "供应商重复", null);
			return jsonResponse;
		}

		if (accountSaveForm.getState() != null) {
			account.setState(1);
			account.setStartDate(new Date());
			account.setStopDate(null);
		} else {
			account.setState(0);
			account.setStopDate(new Date());
		}

		Vendor newVendor = vendorRepository.findOneByCode(accountSaveForm.getVendor());
		account.setVendor(newVendor);
		account = accountRepository.save(account);

		jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, account);

		return jsonResponse;
	}	
}
