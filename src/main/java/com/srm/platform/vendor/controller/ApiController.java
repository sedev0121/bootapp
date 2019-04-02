package com.srm.platform.vendor.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Box;
import com.srm.platform.vendor.model.Notice;
import com.srm.platform.vendor.model.NoticeRead;
import com.srm.platform.vendor.model.PurchaseInDetail;
import com.srm.platform.vendor.model.StatementDetail;
import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.BoxRepository;
import com.srm.platform.vendor.repository.NoticeReadRepository;
import com.srm.platform.vendor.repository.NoticeRepository;
import com.srm.platform.vendor.repository.PurchaseInDetailRepository;
import com.srm.platform.vendor.repository.StatementDetailRepository;
import com.srm.platform.vendor.repository.StatementMainRepository;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;

@RestController
@RequestMapping(path = "/api")
public class ApiController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private StatementMainRepository statementMainRepository;

	@Autowired
	public NoticeRepository noticeRepository;

	@Autowired
	public NoticeReadRepository noticeReadRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private PurchaseInDetailRepository purchaseInDetailRepository;
	
	@Autowired
	private StatementDetailRepository statementDetailRepository;
	
	@Autowired
	private BoxRepository boxRepository;
	
	@ResponseBody
	@RequestMapping({ "/invoice" })
	public Integer index(@RequestParam Map<String, String> requestParams) {
		String reason = requestParams.getOrDefault("reason", null);
		String invoice_num = requestParams.getOrDefault("invoice_num", null);

		if (invoice_num == null) {
			return 0;
		}else {
			StatementMain main = statementMainRepository.findOneByInvoiceCode(invoice_num);

			if (main != null) {
				main.setInvoiceCancelDate(new Date());
				main.setInvoiceCancelReason(reason);
				main.setState(Constants.STATEMENT_STATE_INVOICE_CANCEL);
				statementMainRepository.save(main);
				this.cancelPurchaseInState(main);
				sendmessage(main);
				return 1;
				
				
				
			} else {
				return 0;
			}
		}

	}
	
	private void cancelPurchaseInState(StatementMain main) {
		List<StatementDetail> detailList = statementDetailRepository.findByCode(main.getCode());
		for (StatementDetail detail : detailList) {
			PurchaseInDetail purchaseInDetail = purchaseInDetailRepository
					.findOneById(detail.getPurchaseInDetailId());

			if (purchaseInDetail != null) {
				purchaseInDetail.setState(Constants.PURCHASE_IN_STATE_START);
				purchaseInDetailRepository.save(purchaseInDetail);
			}
		}
	}
	private void sendmessage(StatementMain main) {
		List<Account> toList = new ArrayList<>();
		toList.add(main.getMaker());
		toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
		
		String title = String.format("对账单【%s】已由【U8系统】退回，请及时查阅和处理！", main.getCode());
		String url = String.format("/statement/%s/read", main.getCode());		
		
		Notice notice = new Notice();
		notice.setState(Constants.NOTICE_STATE_PUBLISH);
		notice.setType(Constants.NOTICE_TYPE_SYSTEM);
		notice.setTitle(title);
		notice.setContent(title);
		notice.setCreateDate(new Date());
		notice.setUrl(url);
		notice = noticeRepository.save(notice);

		for (Account account : toList) {
			NoticeRead noticeRead = new NoticeRead();
			noticeRead.setNotice(notice);
			noticeRead.setAccount(account);
			noticeReadRepository.save(noticeRead);
		}
	}
	
	@ResponseBody
	@RequestMapping({ "/box/empty" })
	public GenericJsonResponse<Box> boxEmpty(@RequestParam Map<String, String> requestParams) {
		GenericJsonResponse<Box> jsonResponse;
		jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, null);

		Box box = new Box();
		String code = requestParams.get("box_id");

		if (code == null || code.isEmpty()) {
			jsonResponse.setSuccess(GenericJsonResponse.FAILED);
			jsonResponse.setErrmsg("箱码(box_id)不能为空");
		} else {
			box = boxRepository.findOneByCode(code);
			if (box == null) {
				jsonResponse.setSuccess(GenericJsonResponse.FAILED);
				jsonResponse.setErrmsg("该箱码不存在");
			}else {
				box.setUsed(0);
				box = boxRepository.save(box);	
			}			
		}
		
		return jsonResponse;

	}

}
