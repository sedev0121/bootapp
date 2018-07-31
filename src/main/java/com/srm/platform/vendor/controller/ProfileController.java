package com.srm.platform.vendor.controller;

import java.security.Principal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.repository.AccountRepository;

@Controller
@RequestMapping(path = "/profile")
public class ProfileController extends CommonController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${system.password.minlength}")
	private int PASSWORD_MIN_LENGTH;

	@Value("${system.password.no_special_char}")
	private boolean PASSWORD_NO_SPEICAL_CHAR;

	@Value("${system.password.must_include_char}")
	private boolean PASSWORD_MUST_INCLUDE_CHAR;

	@PreAuthorize("hasRole('ROLE_BUYER') OR hasRole('ROLE_VENDOR') OR hasRole('ROLE_ADMIN')")
	@GetMapping({ "/", "" })
	public String profile(Model model, Principal principal) {
		Account account = accountRepository.findOneByUsername(principal.getName());

		if (account == null)
			show404();

		model.addAttribute("password_min_length", PASSWORD_MIN_LENGTH);
		model.addAttribute("password_no_special_char", PASSWORD_NO_SPEICAL_CHAR);
		model.addAttribute("password_must_include_char", PASSWORD_MUST_INCLUDE_CHAR);
		model.addAttribute("account", account);
		return "home/profile";
	}

	@Transactional
	@PostMapping("/update")
	public @ResponseBody Account profile_update_ajax(@RequestParam Map<String, String> requestParams,
			Principal principal) {
		String weixin = requestParams.get("weixin");
		String qq = requestParams.get("qq");
		String yahoo = requestParams.get("yahoo");
		String wangwang = requestParams.get("wangwang");
		String mobile = requestParams.get("mobile");
		String tel = requestParams.get("tel");
		String address = requestParams.get("address");
		String gtalk = requestParams.get("gtalk");
		String email = requestParams.get("email");

		Account account = accountRepository.findOneByUsername(principal.getName());

		account.setWeixin(weixin);
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
	@PostMapping("/changepwd")
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

	@GetMapping("/checkemail")
	public @ResponseBody Boolean checkEmail_ajax(@RequestParam("id") Long id, @RequestParam("email") String email) {
		Account account = accountRepository.findOneByEmail(email);
		logger.info("email=" + email + " account=" + account);
		if (account != null && account.getId() != id) {
			return false;
		} else {
			return true;
		}
	}

	@GetMapping("/checkmobile")
	public @ResponseBody Boolean checkMobile_ajax(@RequestParam("id") Long id, @RequestParam("mobile") String mobile) {
		Account account = accountRepository.findOneByMobile(mobile);

		logger.info("mobile=" + mobile + " account=" + account);
		if (account != null && account.getId() != id) {
			return false;
		} else {
			return true;
		}
	}

	@GetMapping("/checkweixin")
	public @ResponseBody Boolean checkWeixin_ajax(@RequestParam("id") Long id, @RequestParam("weixin") String weixin) {
		Account account = accountRepository.findOneByWeixin(weixin);
		logger.info("weixin=" + weixin + " account=" + account);
		if (account != null && account.getId() != id) {
			return false;
		} else {
			return true;
		}
	}

}
