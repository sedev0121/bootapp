package com.srm.platform.vendor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.CompanyRepository;
import com.srm.platform.vendor.repository.PermissionGroupRepository;
import com.srm.platform.vendor.repository.PermissionGroupUserRepository;
import com.srm.platform.vendor.repository.PermissionUserScopeRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.searchitem.SearchItem;

public class AccountController extends CommonController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());


	@GetMapping("/{id}/delete")
	public @ResponseBody Boolean delete(@PathVariable("id") Long id, Model model) {
		Account account = accountRepository.findOneById(id);
		accountRepository.delete(account);
		return true;
	}

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
