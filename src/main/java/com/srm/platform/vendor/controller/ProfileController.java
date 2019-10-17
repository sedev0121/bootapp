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
import com.srm.platform.vendor.utility.Constants;

@Controller
@RequestMapping(path = "/profile")
public class ProfileController extends CommonController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${srm.password.minlength}")
	private int PASSWORD_MIN_LENGTH;

	@Value("${srm.password.no_special_char}")
	private boolean PASSWORD_NO_SPEICAL_CHAR;

	@Value("${srm.password.must_include_char}")
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
		String mobile = requestParams.get("mobile");
		String tel = requestParams.get("tel");
		String address = requestParams.get("address");
		String email = requestParams.get("email");

		Account account = accountRepository.findOneByUsername(principal.getName());

		account.setMobile(mobile);
		account.setTel(tel);
		account.setAddress(address);
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
		String type = requestParams.get("type");

		Account account = accountRepository.findOneByUsername(principal.getName());

		if (Integer.parseInt(type) == Constants.PASSWORD_TYPE_NORMAL) {
			if (passwordEncoder.matches(oldPassword, account.getPassword())) {
				account.setPassword(passwordEncoder.encode(newPassword));
				account = accountRepository.save(account);
				return "1";
			} else {
				return "旧密码有误！";
			}	
		} else {
			if (passwordEncoder.matches(oldPassword, account.getSecondPassword())) {
				account.setSecondPassword(passwordEncoder.encode(newPassword));
				account = accountRepository.save(account);
				return "1";
			} else {
				return "旧二级密码有误！";
			}
		}
		

	}
	
	@PostMapping("/check_second_password")
	public @ResponseBody String checkSecondPassword(@RequestParam Map<String, String> requestParams,
			Principal principal) {
		String secondPassword = requestParams.get("pwd");

		Account account = accountRepository.findOneByUsername(principal.getName());

		if (passwordEncoder.matches(secondPassword, account.getSecondPassword())) {	
			httpSession.setAttribute("second_password", 1);
			return "1";
		} else {
			return "0";
		}
	}

	@GetMapping("/checkemail")
	public @ResponseBody Boolean checkEmail_ajax(@RequestParam("id") long id, @RequestParam("email") String email) {
		Account account = accountRepository.findOneByEmail(email);
		if (account != null && account.getId().longValue() != id) {
			return false;
		} else {
			return true;
		}
	}

	@GetMapping("/checkmobile")
	public @ResponseBody Boolean checkMobile_ajax(@RequestParam("id") long id, @RequestParam("mobile") String mobile) {
		Account account = accountRepository.findOneByMobile(mobile);

		if (account != null && account.getId().longValue() != id) {
			return false;
		} else {
			return true;
		}
	}

}
