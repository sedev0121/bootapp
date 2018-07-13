package com.srm.platform.vendor.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.PasswordResetToken;
import com.srm.platform.vendor.repository.PasswordResetTokenRepository;
import com.srm.platform.vendor.service.AccountService;
import com.srm.platform.vendor.service.EmailService;

@Controller

public class ResetPasswordController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private EmailService emailService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private PasswordResetTokenRepository passwordTokenRepository;

	@RequestMapping(value = "/resetpassword")
	public @ResponseBody String resetPassword(HttpServletRequest request, @RequestParam("email") String email) {
		Account account = accountService.loadUserByEmail(email);
		if (account == null) {
			return "0";
		}
		String token = UUID.randomUUID().toString();
		accountService.createPasswordResetTokenForUser(account, token);
		SimpleMailMessage message = constructResetTokenEmail(request.getContextPath(), token, account);
		// mailSender.send(message);

		Map<String, Object> model = new HashMap<>();
		model.put("token", token);
		model.put("account", account);
		model.put("signature", "https://memorynotfound.com");
		String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		model.put("resetUrl", url + "/reset-password?token=" + token);

		// emailService.sendEmail(message, model);

		return "1";
	}

	private SimpleMailMessage constructResetTokenEmail(String contextPath, String token, Account account) {
		String url = contextPath + "/changepassword?id=" + account.getId() + "&token=" + token;
		String message = "";

		logger.info(url);

		SimpleMailMessage email = new SimpleMailMessage();
		email.setSubject("重置密码");
		email.setText(message);
		email.setTo(account.getEmail());
		email.setFrom("no-reply@memorynotfound.com");
		return email;
	}

	@GetMapping(value = "/changepassword")
	public String showChangePasswordPage(Model model, @RequestParam("id") long id,
			@RequestParam("token") String token) {
		String result = accountService.validatePasswordResetToken(id, token);
		model.addAttribute("token", token);
		model.addAttribute("error", result);
		return "change_password";
	}

	@RequestMapping(value = "/savepassword")
	@Transactional
	public String savePassword(@RequestParam Map<String, String> requestParams) {
		String tokenStr = requestParams.get("token");
		String password = requestParams.get("password");

		PasswordResetToken token = passwordTokenRepository.findByToken(tokenStr);
		Account account = token.getAccount();
		account.setPassword(password);
		accountService.save(account);
		passwordTokenRepository.delete(token);

		return "redirect:/login?resetSuccess";
	}

}
