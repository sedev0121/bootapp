package com.srm.platform.vendor.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

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
import com.srm.platform.vendor.saveform.AccountSaveForm;
import com.srm.platform.vendor.searchitem.BuyerSearchResult;
import com.srm.platform.vendor.searchitem.PermissionScopeOfAccount;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;

@Controller
@RequestMapping(path = "/buyer")
@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('采购员用户管理-查看列表')")
public class BuyerController extends AccountController {	

	// 用户管理->列表
	@GetMapping({ "/", "" })
	public String buyer(Model model) {
		return "admin/buyer/list";
	}

	// 用户管理->修改
	@GetMapping("/{id}/edit")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('采购员用户管理-新建/修改')")
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
		return "admin/buyer/edit";
	}

	// 用户管理->新建
	@GetMapping("/add")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('采购员用户管理-新建/修改')")
	public String add(Model model) {
		model.addAttribute("account", new Account());
		model.addAttribute("pwd", Constants.DEFAULT_PASSWORD);
		model.addAttribute("groupList", "[]");
		return "admin/buyer/edit";
	}

	// 用户管理->列表
	@GetMapping("/list")
	public @ResponseBody Page<BuyerSearchResult> list_ajax(@RequestParam Map<String, String> requestParams) {
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

		String selectQuery = "SELECT t.*, c.name companyname ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "FROM account t left join company c on t.company_id=c.id "
				+ "where t.role='ROLE_BUYER' ";

		Map<String, Object> params = new HashMap<>();

		if (!search.trim().isEmpty()) {
			bodyQuery += " and (c.name LIKE CONCAT('%',:search, '%') or t.username LIKE CONCAT('%',:search, '%') or t.realname LIKE CONCAT('%',:search, '%') or t.duty LIKE CONCAT('%',:search, '%') or t.email LIKE CONCAT('%',:search, '%')) ";
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
		q = em.createNativeQuery(selectQuery, "BuyerSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<BuyerSearchResult>(list, request, totalCount.longValue());

	}

	// 用户修改
	@Transactional
	@PostMapping("/update")
	public @ResponseBody GenericJsonResponse<Account> update_ajax(AccountSaveForm accountSaveForm) {

		Account account = new Account();
		if (accountSaveForm.getId() != null) {
			account = accountRepository.findOneById(accountSaveForm.getId());
		} else {
			account.setPassword(passwordEncoder.encode(accountSaveForm.getPassword()));
		}

		account.setUsername(accountSaveForm.getUsername());
		account.setRealname(accountSaveForm.getRealname());
		account.setUnitname(accountSaveForm.getUnitname());
		account.setEmployeeNo(accountSaveForm.getEmployee_no());
		account.setMobile(accountSaveForm.getMobile());
		account.setTel(accountSaveForm.getTel());
		account.setEmail(accountSaveForm.getEmail());
		account.setRole(accountSaveForm.getRole());
		account.setDuty(accountSaveForm.getDuty());
		account.setCompany(companyRepository.findOneById(accountSaveForm.getCompany()));

		boolean isDuplicatedVendor = false;
		List<Account> accountsHavingVendorCode = accountRepository.findAccountsByVendor(accountSaveForm.getVendor());
		for (Account vendorAccount : accountsHavingVendorCode) {
			if (vendorAccount.getId() != accountSaveForm.getId()) {
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

		account = accountRepository.save(account);

		jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, account);

		permissionGroupUserRepository.deleteByAccountId(account.getId());
		permissionUserScopeRepository.deleteByAccountId(account.getId());

		List<Map<String, Long>> permissionGroupIdList = accountSaveForm.getPermission_group_ids();
		if (permissionGroupIdList != null) {
			for (Map<String, Long> group : permissionGroupIdList) {
				PermissionGroupUser temp = new PermissionGroupUser();
				temp.setAccountId(account.getId());
				temp.setGroupId(group.get("group_id"));
				permissionGroupUserRepository.save(temp);
			}
		}

		List<Map<String, String>> permissionScopeList = accountSaveForm.getPermission_scope_list();
		if (permissionScopeList != null) {
			for (Map<String, String> scope : permissionScopeList) {
				PermissionUserScope temp = new PermissionUserScope();
				temp.setAccountId(account.getId());
				temp.setGroupId(Long.valueOf(scope.get("group_id")));
				temp.setDimensionId(Long.valueOf(scope.get("dimension_id")));
				temp.setTargetId(scope.get("target_id"));
				permissionUserScopeRepository.save(temp);
			}
		}
		
		if (account.getState() == Constants.ACCOUNT_STATE_DISABLE) {
			permissionUserScopeRepository.deleteByAccountDimension(account.getId());
		}

		return jsonResponse;
	}
}
