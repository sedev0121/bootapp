package com.srm.platform.vendor.controller;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.repository.StatementMainRepository;
import com.srm.platform.vendor.utility.Constants;

@RestController
@RequestMapping(path = "/api")
public class ApiController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private StatementMainRepository statementMainRepository;

	@ResponseBody
	@RequestMapping({ "/invoice" })
	public Integer index(@RequestParam Map<String, String> requestParams) {
		String reason = requestParams.getOrDefault("reason", null);
		String invoice_num = requestParams.getOrDefault("invoice_num", null);

		StatementMain main = statementMainRepository.findOneByInvoiceCode(invoice_num);

		if (main != null) {
			main.setInvoiceCancelDate(new Date());
			main.setInvoiceCancelReason(reason);
			main.setState(Constants.STATEMENT_STATE_INVOICE_CANCEL);
			statementMainRepository.save(main);
			return 1;
		} else {
			return 0;
		}

	}

}
