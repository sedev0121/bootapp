package com.srm.platform.vendor.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/delivery")
public class DeliveryControler extends CommonController {
	@PreAuthorize("hasRole('ROLE_BUYER')")
	@GetMapping({ "/", "" })
	public String index() {
		return "report/delivery";
	}
}
