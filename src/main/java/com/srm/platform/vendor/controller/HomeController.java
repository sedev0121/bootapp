package com.srm.platform.vendor.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Controller
@RequestMapping(path = "/")

public class HomeController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Autowired
	private HttpSession httpSession;

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

	@RequestMapping(value = "/test")
	public String test(Model model) {
		model.addAttribute("url", "/changepassword?id=1&token=e4706254-415a-4dac-84bf-7a621f2d405d");

		Map<String, Object> test = new HashMap<>();
		test.put("url", "test");

		Context context = new Context();
		// context.setVariable("url", "/test");
		context.setVariables(test);
		String html = templateEngine.process("email/resetpassword", context);
		logger.info(html);

		return "email/resetpassword";
	}

}
