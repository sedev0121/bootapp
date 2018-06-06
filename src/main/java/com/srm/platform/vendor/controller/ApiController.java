package com.srm.platform.vendor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.srm.platform.vendor.u8api.ApiClient;

@RestController
@RequestMapping(path = "/api")
public class ApiController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ApiClient apiClient;

	@RequestMapping(value = "/get_vendor", produces = "application/json")
	public String get_vendor(@RequestParam String id, @RequestParam int ds) {
		return apiClient.getVendor(id, ds);
	}
}
