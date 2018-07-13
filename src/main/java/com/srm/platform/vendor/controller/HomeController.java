package com.srm.platform.vendor.controller;

import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/")

public class HomeController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

	@RequestMapping(value = "test")
	public String test() {
		return "test";
	}

}
