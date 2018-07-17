package com.srm.platform.vendor.controller;

import java.security.Principal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.repository.AccountRepository;

@Controller

public class ProfileController extends CommonController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PreAuthorize("hasRole('ROLE_BUYER') OR hasRole('ROLE_VENDOR') OR hasRole('ROLE_ADMIN')")
	@GetMapping("/profile")
	public String profile(Model model, Principal principal) {
		Account account = accountRepository.findOneByUsername(principal.getName());
		model.addAttribute("account", account);
		return "home/profile";
	}

	@Transactional
	@PostMapping("/profile/update")
	public @ResponseBody Account profile_update_ajax(@RequestParam Map<String, String> requestParams,
			Principal principal) {
		String skype = requestParams.get("skype");
		String qq = requestParams.get("qq");
		String yahoo = requestParams.get("yahoo");
		String wangwang = requestParams.get("wangwang");
		String mobile = requestParams.get("mobile");
		String tel = requestParams.get("tel");
		String address = requestParams.get("address");
		String gtalk = requestParams.get("gtalk");
		String email = requestParams.get("email");

		Account account = accountRepository.findOneByUsername(principal.getName());

		account.setSkype(skype);
		account.setQq(qq);
		account.setYahoo(yahoo);
		account.setWangwang(wangwang);
		account.setMobile(mobile);
		account.setTel(tel);
		account.setAddress(address);
		account.setGtalk(gtalk);
		account.setEmail(email);

		account = accountRepository.save(account);
		return account;
	}

	@Transactional
	@PostMapping("/profile/changepwd")
	public @ResponseBody String change_password_ajax(@RequestParam Map<String, String> requestParams,
			Principal principal) {
		String oldPassword = requestParams.get("old_pwd");
		String newPassword = requestParams.get("new_pwd");

		Account account = accountRepository.findOneByUsername(principal.getName());

		if (passwordEncoder.matches(oldPassword, account.getPassword())) {
			account.setPassword(passwordEncoder.encode(newPassword));
			account = accountRepository.save(account);
			return "1";
		} else {
			return "旧密码有误！";
		}

	}

}
