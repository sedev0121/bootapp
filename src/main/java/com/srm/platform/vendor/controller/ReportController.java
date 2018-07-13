package com.srm.platform.vendor.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/report")
@PreAuthorize("hasRole('ROLE_BUYER')")
public class ReportController {
	// 查询列表
	@GetMapping({ "", "/" })
	public String index() {
		return "report/index";
	}
}
