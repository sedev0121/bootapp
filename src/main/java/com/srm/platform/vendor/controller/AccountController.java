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
import com.srm.platform.vendor.utility.GenericJsonResponse;

public class AccountController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PermissionGroupRepository permissionGroupRepository;
	
	@Autowired
	private PermissionGroupUserRepository permissionGroupUserRepository;
	
	@Autowired
	private PermissionUserScopeRepository permissionUserScopeRepository;
	
	@Autowired
	private VendorRepository vendorRepository;
	
	@Autowired
	private CompanyRepository companyRepository;

	@ResponseBody
	@RequestMapping(value = "/company/list", produces = "application/json")
	public Page<SearchItem> company_list(@RequestParam(value = "q") String search) {
		PageRequest request = PageRequest.of(0, 15, Direction.ASC, "name");
		return companyRepository.findForSelect(request);

	}
	
	// 用户修改
	@Transactional
	@GetMapping("/checkuser")
	public @ResponseBody Boolean checkUser_ajax(@RequestParam("id") Long id,
			@RequestParam("username") String username) {
		Account account = accountRepository.findOneByUsername(username);

		if (account != null && account.getId() != id) {
			return false;
		} else {
			return true;
		}
	}
}
