package com.srm.platform.vendor.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import com.srm.platform.vendor.model.DeliveryDetail;
import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.model.Notice;
import com.srm.platform.vendor.model.NoticeRead;
import com.srm.platform.vendor.model.PurchaseInDetail;
import com.srm.platform.vendor.model.StatementDetail;
import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.BoxRepository;
import com.srm.platform.vendor.repository.DeliveryDetailRepository;
import com.srm.platform.vendor.repository.DeliveryMainRepository;
import com.srm.platform.vendor.repository.NoticeReadRepository;
import com.srm.platform.vendor.repository.NoticeRepository;
import com.srm.platform.vendor.repository.PurchaseInDetailRepository;
import com.srm.platform.vendor.repository.StatementDetailRepository;
import com.srm.platform.vendor.repository.StatementMainRepository;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.Utils;

@RestController
@RequestMapping(path = "/api")
public class ApiController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String RESPONSE_SUCCESS = "1";
	private static final String RESPONSE_FAIL = "2";
	
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
	
	@Autowired
	private DeliveryMainRepository deliveryMainRepository;
	
	@Autowired
	private DeliveryDetailRepository deliveryDetailRepository;
	
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

	private String getStringValue(Map<String, Object> object, String key) {
		String temp = String.valueOf(object.get(key));
		if (!Utils.isEmpty(temp)) {
			return String.valueOf(object.get(key));
		}
		return null;
	}
	
	private List<LinkedHashMap<String, Object>> getDetailMap(Map<String, Object> object, String key) {
		return (List<LinkedHashMap<String, Object>>) object.get(key);
	}
	
	@ResponseBody
	@RequestMapping({ "/pda" })
	public Map<String, Object> pda(@RequestParam Map<String, Object> requestParams) {
		String method = getStringValue(requestParams, "method");
		Object content = requestParams.get("content");
		
		Map<String, Object> response = null;
		
		if (method == null || content == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "参数不正确");			
		} else if (method.equals("deleteBoxMsg")) {
			response = this.deleteBoxMsg(requestParams);
		} else if (method.equals("getBoxState")) {
			response = this.getBoxState(requestParams);
		} else if (method.equals("replceBox")) {
			response = this.replaceBox(requestParams);
		} else if (method.equals("createBoxMsg")) {
			response = this.createBoxMsg(requestParams);
		} else if (method.equals("getBoxMsg")) {
			response = this.getBoxMsg(requestParams);
		}
		
		return response;
	}
	
	
	
	private Map<String, Object> deleteBoxMsg(Map<String, Object> requestParams) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		Map<String, String> content = (Map<String, String>)requestParams.get("content");
		String boxCode = content.get("BoxCode");
		
		if (boxCode == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "参数不正确");	
			return response;
		}
		
		Box box = boxRepository.findOneByCode(boxCode);
		if (box == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "找不到箱码");	
			return response;
		}
		
		box.setBindDate(null);
		box.setBindProperty(null);
		box.setDelivery(null);
		box.setQuantity(0D);
		box.setUsed(Box.BOX_IS_EMPTY);
		boxRepository.save(box);
		
		response.put("error_code", RESPONSE_SUCCESS);
		response.put("msg", "已成功解绑编号为DCDS-55666的箱码信息");
		
		return response;
	}
	
	private Map<String, Object> getBoxState(Map<String, Object> requestParams) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		Map<String, String> content = (Map<String, String>)requestParams.get("content");
		String boxCode = content.get("BoxCode");
		
		if (boxCode == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "参数不正确");	
			return response;
		}
		
		Box box = boxRepository.findOneByCode(boxCode);
		if (box == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "找不到箱码");	
			return response;
		}
		
		
		response.put("error_code", RESPONSE_SUCCESS);
		response.put("msg", box.getUsed());
		
		return response;
	}
	
	private Map<String, Object> replaceBox(Map<String, Object> requestParams) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		Map<String, String> content = (Map<String, String>)requestParams.get("content");
		String boxCode1 = content.get("BoxCode1");
		String boxCode2 = content.get("BoxCode2");
		
		if (boxCode1 == null || boxCode2 == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "参数不正确");	
			return response;
		}
		
		Box box1 = boxRepository.findOneByCode(boxCode1);
		Box box2 = boxRepository.findOneByCode(boxCode2);
		if (box1 == null || box2 == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "找不到箱码");	
			return response;
		}
		
		box1.setCode(boxCode2);
		box2.setCode(boxCode1);
		
		response.put("error_code", RESPONSE_SUCCESS);
		response.put("msg", "更换成功");
		
		return response;
	}
	
	private Map<String, Object> createBoxMsg(Map<String, Object> requestParams) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		String deliveryCode = String.valueOf(requestParams.get("code"));

		List<Map<String, String>> content = (List<Map<String, String>>)requestParams.get("content");
		
		if (deliveryCode == null || content == null || content.size() == 0) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "参数不正确");	
			return response;
		}
		

		Map<String, String> data = content.get(0);
		String quantityStr = data.get("quantity");
		String deliveryRowNoStr = data.get("line_code");
		String boxCode = data.get("BoxCode");
		
		if (quantityStr == null || deliveryRowNoStr == null || boxCode == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "参数不正确");	
			return response;
		}
		
		Box box = boxRepository.findOneByCode(boxCode);
		if (box == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "找不到箱码");	
			return response;
		}
		
		Integer deliveryRowNo = Integer.parseInt(deliveryRowNoStr);
		Double quantity = Double.parseDouble(quantityStr);
		
		DeliveryDetail deliveryDetail = deliveryDetailRepository.findOneByCodeAndRowNo(deliveryCode, deliveryRowNo);
		if (deliveryDetail == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "找不到发货单");	
			return response;
		}
		
		box.setDelivery(deliveryDetail);
		box.setQuantity(quantity);
		box.setBindDate(new Date());
		box.setUsed(Box.BOX_IS_USING);
		
		response.put("error_code", RESPONSE_SUCCESS);
		response.put("msg", "提交成功");
		
		return response;
	}
	
	
	private Map<String, Object> getBoxMsg(Map<String, Object> requestParams) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		Map<String, String> content = (Map<String, String>)requestParams.get("content");
		String boxCode = content.get("BoxCode");
		
		if (boxCode == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "参数不正确");	
			return response;
		}
		
		Box box = boxRepository.findOneByCode(boxCode);
		
		if (box == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "找不到箱码");	
			return response;
		}
		
		DeliveryDetail deliveryDetail = box.getDelivery();		
		if (deliveryDetail == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "没有绑定");	
			return response;
		}
		
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		Map<String, String> temp = new HashMap<String, String>();
		Inventory inventory = deliveryDetail.getPurchaseOrderDetail().getInventory();
		temp.put("material_code", inventory.getCode());
		temp.put("name", inventory.getName());
		temp.put("specs", inventory.getSpecs());
		temp.put("quantity", String.valueOf(box.getQuantity()));
		temp.put("serial", deliveryDetail.getDeliverNumber());
		temp.put("order_code", deliveryDetail.getPurchaseOrderDetail().getMain().getCode());
		data.add(temp);
		
		
		response.put("error_code", RESPONSE_SUCCESS);
		response.put("msg", "成功获取装箱清单");

		response.put("supplier_code", deliveryDetail.getMain().getVendor().getCode());
		response.put("code", deliveryDetail.getMain().getCode());
		response.put("invoice_code", deliveryDetail.getMain().getCode());
		response.put("data", data);
		
		
		return response;
	}
}
