package com.srm.platform.vendor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.searchitem.SearchItem;
import com.srm.platform.vendor.utility.Constants;

public class AccountController extends CommonController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected PasswordEncoder passwordEncoder;
	
	@GetMapping("/{id}/delete")
	public @ResponseBody Boolean delete(@PathVariable("id") Long id, Model model) {
		Account account = accountRepository.findOneById(id);
		accountRepository.delete(account);
		return true;
	}

	// 用户修改
	@Transactional
	@GetMapping("/checkuser")
	public @ResponseBody Boolean checkUser_ajax(@RequestParam("id") long id,
			@RequestParam("username") String username) {
		Account account = accountRepository.findOneByUsername(username);

		if (account != null && account.getId().longValue() != id) {
			return false;
		} else {
			return true;
		}
	}
	
	// 用户修改
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/reset_pwd/{id}")
	public @ResponseBody String resetPassword(@PathVariable("id") Long id) {
		Account account = accountRepository.findOneById(id);
		account.setPassword(passwordEncoder.encode(Constants.DEFAULT_PASSWORD));
		account = accountRepository.save(account);
		
		return "1";
	}
	
	// 用户修改
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/reset_second_pwd/{id}")
	public @ResponseBody String resetSecondPassword(@PathVariable("id") Long id) {
		Account account = accountRepository.findOneById(id);
		account.setSecondPassword(passwordEncoder.encode(Constants.DEFAULT_SECOND_PASSWORD));
		account = accountRepository.save(account);
		
		return "1";
	}
}
