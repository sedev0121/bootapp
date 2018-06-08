package com.srm.platform.vendor.controller;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/") //
public class HomeController {

	@GetMapping("/")
	public String dashboard(Authentication authentication) {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		String redirectUrl = "buyer";

		for (GrantedAuthority a : authorities) {
			if (a.getAuthority().equals("ROLE_VENDOR")) {
				redirectUrl = "/vendor";
				break;
			}

		}

		return "redirect:" + redirectUrl;
	}

	@GetMapping("/forbidden")
	public String forbidden() {
		return "denied";
	}

}
