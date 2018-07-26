package com.srm.platform.vendor.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//库存报表
@Controller
@RequestMapping(path = "/incoming")

public class IncomingController extends CommonController {
	@PreAuthorize("hasRole('ROLE_BUYER')")
	@GetMapping({ "/", "" })
	public String index() {
		return "report/incoming";
	}
}
