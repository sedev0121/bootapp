package com.srm.platform.vendor.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.PasswordResetToken;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.PasswordResetTokenRepository;
import com.srm.platform.vendor.service.AccountService;
import com.srm.platform.vendor.service.EmailService;
import com.srm.platform.vendor.u8api.ApiClient;
import com.srm.platform.vendor.utility.Utils;

@Controller

public class ResetPasswordController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private ApiClient apiClient;

	@Autowired
	private EmailService emailService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private PasswordResetTokenRepository passwordTokenRepository;

	@Value("${system.password.minlength}")
	private int PASSWORD_MIN_LENGTH;

	@Value("${system.password.no_special_char}")
	private boolean PASSWORD_NO_SPEICAL_CHAR;

	@Value("${system.password.must_include_char}")
	private boolean PASSWORD_MUST_INCLUDE_CHAR;

	@Transactional
	@RequestMapping(value = "/resetpassword")
	public @ResponseBody String resetPassword(HttpServletRequest request, @RequestParam("email") String email) {
		Account account = accountService.loadUserByEmail(email);
		if (account == null) {
			return "0";
		}
		String token = UUID.randomUUID().toString();
		accountService.createPasswordResetTokenForUser(account, token);
		String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		String url = baseUrl + "/changepassword?id=" + account.getId() + "&token=" + token;

		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject("重置密码");
		message.setText("");
		message.setTo(account.getEmail());

		Map<String, Object> model = new HashMap<>();
		model.put("token", token);
		model.put("account", account);
		model.put("url", url);
		model.put("baseUrl", baseUrl);

		emailService.sendEmail(message, model);

		return "1";
	}

	@Transactional
	@RequestMapping(value = "/resetpassword/phone")
	public @ResponseBody String resetPasswordByPhone(HttpServletRequest request, @RequestParam("phone") String phone) {
		Account account = accountRepository.findOneByMobile(phone);
		if (account == null) {
			return "0";
		}

		String newPassword = Utils.generateResetPassword();
		account.setPassword(newPassword);
		accountService.save(account);

		String message = String.format("密码重置成功！请用新密码【%s】登陆。", newPassword);
		logger.info(message);
		// ObjectMapper objectMapper = new ObjectMapper();
		// Map<String, Object> map = new HashMap<>();
		// String response = apiClient.sendSMS(account.getMobile(), message);
		// try {
		// map = objectMapper.readValue(response, new TypeReference<Map<String,
		// Object>>() {
		// });
		// logger.info((String) map.get("errcode"));
		// } catch (Exception e) {
		// e.printStackTrace();
		// return "0";
		// }

		return "1";
	}

	@GetMapping(value = "/changepassword")
	public String showChangePasswordPage(Model model, @RequestParam("id") long id,
			@RequestParam("token") String token) {
		String result = accountService.validatePasswordResetToken(id, token);
		model.addAttribute("token", token);
		model.addAttribute("error", result);
		model.addAttribute("password_min_length", PASSWORD_MIN_LENGTH);
		model.addAttribute("password_no_special_char", PASSWORD_NO_SPEICAL_CHAR);
		model.addAttribute("password_must_include_char", PASSWORD_MUST_INCLUDE_CHAR);
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
