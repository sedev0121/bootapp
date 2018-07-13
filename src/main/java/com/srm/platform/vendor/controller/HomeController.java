package com.srm.platform.vendor.controller;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.repository.AccountRepository;

@Controller
@RequestMapping(path = "/")

public class HomeController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private HttpSession httpSession;

	@GetMapping({ "/", "" })
	@PreAuthorize("hasRole('ROLE_BUYER') OR hasRole('ROLE_VENDOR') OR hasRole('ROLE_ADMIN')")
	public String index(Authentication authentication) {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		String redirectUrl = "/buyer";

		for (GrantedAuthority a : authorities) {
			if (a.getAuthority().equals("ROLE_VENDOR")) {
				redirectUrl = "/vendor";
				break;
			}
			if (a.getAuthority().equals("ROLE_ADMIN")) {
				redirectUrl = "/admin";
				break;
			}

		}

		return "index";
	}

	@GetMapping("/forbidden")
	public String forbidden() {
		return "denied";
	}

	@RequestMapping(value = "login")
	public String login() {
		return "login";
	}

	@GetMapping(value = "/logout")
	public String logout() {
		httpSession.invalidate();
		return "redirect:/";
	}

	@PreAuthorize("hasRole('ROLE_BUYER') OR hasRole('ROLE_VENDOR') OR hasRole('ROLE_ADMIN')")
	@GetMapping("/profile")
	public String profile(Model model, Principal principal) {
		Account account = accountRepository.findOneByUsername(principal.getName());
		model.addAttribute("account", account);
		return "home/profile";
	}

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

	@RequestMapping(value = "test")
	public String test() {
		return "test";
	}

}
