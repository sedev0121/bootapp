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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Box;
import com.srm.platform.vendor.model.DeliveryDetail;
import com.srm.platform.vendor.model.DeliveryMain;
import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.model.Notice;
import com.srm.platform.vendor.model.NoticeRead;
import com.srm.platform.vendor.model.PurchaseInDetail;
import com.srm.platform.vendor.model.PurchaseOrderDetail;
import com.srm.platform.vendor.model.PurchaseOrderMain;
import com.srm.platform.vendor.model.StatementDetail;
import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.BoxRepository;
import com.srm.platform.vendor.repository.DeliveryDetailRepository;
import com.srm.platform.vendor.repository.DeliveryMainRepository;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.NoticeReadRepository;
import com.srm.platform.vendor.repository.NoticeRepository;
import com.srm.platform.vendor.repository.PurchaseInDetailRepository;
import com.srm.platform.vendor.repository.StatementDetailRepository;
import com.srm.platform.vendor.repository.StatementMainRepository;
import com.srm.platform.vendor.u8api.RestApiClient;
import com.srm.platform.vendor.u8api.RestApiResponse;
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
	private RestApiClient apiClient;
	
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
	
	@Autowired
	private InventoryRepository inventoryRepository;
	
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
				box.setDeliveryCode(null);
				box.setBindDate(null);
				box.setBindProperty(null);
				box.setQuantity(null);
				box.setInventoryCode(null);
				box = boxRepository.save(box);	
			}			
		}
		
		return jsonResponse;

	}

	@ResponseBody
	@RequestMapping({ "/test" })
	public GenericJsonResponse<Box> test() {
		GenericJsonResponse<Box> jsonResponse;
		jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, null);
		
		DeliveryMain deliveryMain = deliveryMainRepository.findOneByCode("20190528182947786989");
		if (deliveryMain == null) {
			jsonResponse.setErrmsg("找不到发货单");
			jsonResponse.setSuccess(GenericJsonResponse.FAILED);
			return jsonResponse;
		}
		
		RestApiResponse u8Response = postForArrivalVouch(deliveryMain);
		
		if (!u8Response.isSuccess()) {
			jsonResponse.setErrmsg("Fail");
			jsonResponse.setSuccess(GenericJsonResponse.FAILED);
		}
		
		return jsonResponse;
	}


	
	@ResponseBody
	@RequestMapping({ "/pda" })
	public Map<String, Object> pda(@RequestBody Map<String, Object> requestParams) {
		String method = String.valueOf(requestParams.get("method"));
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
		} else if (method.equals("getFHD")) {
			response = this.getFHD(requestParams);
		} else if (method.equals("createDHD")) {
			response = this.createDHD(requestParams);
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
		box.setDeliveryCode(null);
		box.setInventoryCode(null);
		box.setQuantity(null);
		box.setUsed(Box.BOX_IS_EMPTY);
		boxRepository.save(box);
		
		response.put("error_code", RESPONSE_SUCCESS);
		response.put("msg", "已成功解绑编号为" + boxCode + "的箱码信息");
		
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
		
		box1 = boxRepository.save(box1);
		box2 = boxRepository.save(box2);
		
		response.put("error_code", RESPONSE_SUCCESS);
		response.put("msg", "更换成功");
		
		return response;
	}
	
	private Map<String, Object> createBoxMsg(Map<String, Object> requestParams) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		String deliveryCode = String.valueOf(requestParams.get("code"));

		List<Map<String, String>> content = (List<Map<String, String>>)requestParams.get("content");
		
		if (deliveryCode == null || content == null || content.size() == 0) {
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "参数不正确");	
			return response;
		}
		
		DeliveryMain deliveryMain = deliveryMainRepository.findOneByCode(deliveryCode);
		if (deliveryMain == null) {
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "找不到发货单");	
			return response;
		}
		
		
		Map<String, Double> boxSummary = new HashMap<String, Double>();
		Map<String, Double> deliverySummary = new HashMap<String, Double>();

		for(Map<String, String> data : content) {
			String quantityStr = data.get("quantity");
			String boxCode = data.get("BoxCode");
			String inventoryCode = data.get("material_code");
			
			if (quantityStr == null || inventoryCode == null || boxCode == null) {
				response.put("error_code", RESPONSE_FAIL);
				response.put("msg", "参数不正确");	
				return response;
			}
			
			Box box = boxRepository.findOneByCode(boxCode);
			if (box == null) {
				response.put("error_code", RESPONSE_FAIL);
				response.put("msg", "找不到箱码");	
				return response;
			}			
			
			Double quantity = Double.parseDouble(quantityStr);
			Double inventoryQuantity = boxSummary.get(inventoryCode);
			if (inventoryQuantity == null) {
				inventoryQuantity = 0D;
			}
			inventoryQuantity += quantity;
			boxSummary.put(inventoryCode, inventoryQuantity);
		}
		
		List<DeliveryDetail> detailList = deliveryDetailRepository.findDetailsByCode(deliveryCode);
		for(DeliveryDetail detail : detailList) {
			Inventory inventory = detail.getPurchaseOrderDetail().getInventory();
			if (inventory.getBoxClass() == null) {
				continue;
			}
			String inventoryCode = inventory.getCode();
			Double quantity = detail.getDeliveredQuantity();
			
			Double inventoryQuantity = deliverySummary.get(inventoryCode);
			if (inventoryQuantity == null) {
				inventoryQuantity = 0D;
			}
			inventoryQuantity += quantity;
			deliverySummary.put(inventoryCode, inventoryQuantity);
		}
		
		for(Map.Entry<String, Double> entry: deliverySummary.entrySet()) {
			String key = entry.getKey();
			Double value = entry.getValue();
			
			if (!boxSummary.containsKey(key) || value.compareTo(boxSummary.get(key)) != 0) {
				response.put("error_code", RESPONSE_FAIL);
				response.put("msg", "装箱数据与发货单不符");	
				return response;
			}
		}
		
		if (deliveryMain.getState() == Constants.DELIVERY_STATE_OK) {
			deliveryMain.setState(Constants.DELIVERY_STATE_DELIVERED);
			deliveryMainRepository.save(deliveryMain);
			
			List<Box> oldBoxList = boxRepository.findAllByDeliveryCode(deliveryCode);
			
			List<Box> emptyList = new ArrayList<Box>();
			for(Box box : oldBoxList) {
				if (box.getBoxClass() == null) {
					continue;
				}
				
				box.setDeliveryCode(null);
				box.setInventoryCode(null);
				box.setQuantity(null);
				box.setBindDate(null);
				box.setBindProperty(null);
				box.setUsed(Box.BOX_IS_EMPTY);
				
				emptyList.add(box);
			}
			
			boxRepository.saveAll(emptyList);
			
			
			List<Box> bindingBoxList = new ArrayList<Box>();
			for(Map<String, String> data : content) {
				String quantityStr = data.get("quantity");
				String boxCode = data.get("BoxCode");
				String inventoryCode = data.get("material_code");
				Double quantity = Double.parseDouble(quantityStr);
				
				Box box = boxRepository.findOneByCode(boxCode);
								
				box.setDeliveryCode(deliveryCode);
				box.setInventoryCode(inventoryCode);
				box.setQuantity(quantity);
				box.setBindDate(new Date());
				box.setUsed(Box.BOX_IS_USING);
				
				bindingBoxList.add(box);			
			}
			
			boxRepository.saveAll(bindingBoxList);
			
			response.put("error_code", RESPONSE_SUCCESS);
			response.put("msg", "提交成功");	
		} else {
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "该发货单不能提交");	
		}
		
		return response;
	}
	
	private Map<String, Object> createDHD(Map<String, Object> requestParams) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		String deliveryCode = String.valueOf(requestParams.get("code"));

		List<Map<String, String>> content = (List<Map<String, String>>)requestParams.get("content");
		
		if (deliveryCode == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "参数不正确");	
			return response;
		}
		
		DeliveryMain deliveryMain = deliveryMainRepository.findOneByCode(deliveryCode);
		if (deliveryMain == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "找不到发货单");	
			return response;
		}
		
		if (deliveryMain.getState() == Constants.DELIVERY_STATE_DELIVERED) {
			RestApiResponse u8Response = postForArrivalVouch(deliveryMain);
			
			if (u8Response.isSuccess()) {
				deliveryMain.setState(Constants.DELIVERY_STATE_ARRIVED);
				deliveryMainRepository.save(deliveryMain);
				
				response.put("error_code", RESPONSE_SUCCESS);
				response.put("msg", "提交成功");	
			} else {
				response.put("error_code", RESPONSE_FAIL);
				response.put("msg", u8Response.getErrmsg());
			}
		} else {
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "该发货单不能生成到货单");			
		}
		
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
		
		Inventory inventory = inventoryRepository.findOneByCode(box.getInventoryCode());	
		DeliveryMain deliveryMain = deliveryMainRepository.findOneByCode(box.getDeliveryCode());
		
		if (inventory == null || deliveryMain == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "没有绑定");	
			return response;
		}
		
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		Map<String, String> temp = new HashMap<String, String>();
		
		temp.put("material_code", inventory.getCode());
		temp.put("name", inventory.getName());
		temp.put("specs", inventory.getSpecs());
		temp.put("quantity", String.valueOf(box.getQuantity()));
		temp.put("serial", deliveryMain.getDeliverNumber());
		data.add(temp);
		
		
		response.put("error_code", RESPONSE_SUCCESS);
		response.put("msg", "成功获取装箱清单");

		response.put("supplier_code", deliveryMain.getVendor().getCode());
		response.put("code", deliveryMain.getCode());
		response.put("invoice_code", deliveryMain.getCode());
		response.put("data", data);
		
		
		return response;
	}
	
	private Map<String, Object> getFHD(Map<String, Object> requestParams) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		Map<String, String> content = (Map<String, String>)requestParams.get("content");
		String deliveryCode = content.get("code");
		
		if (deliveryCode == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "参数不正确");	
			return response;
		}
		
		DeliveryMain deliveryMain = deliveryMainRepository.findOneByCode(deliveryCode);
		List<DeliveryDetail> deliveryDetailList = deliveryDetailRepository.findDetailsByCode(deliveryCode);
		
		if (deliveryMain == null || deliveryDetailList == null) {
			response = new HashMap<String, Object>();
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "找不到发货单");	
			return response;
		}
		
		Map<String, Double> deliverySummary = new HashMap<String, Double>();
		Map<String, Double> orderSummary = new HashMap<String, Double>();

		for(DeliveryDetail detail : deliveryDetailList) {
			Inventory inventory = detail.getPurchaseOrderDetail().getInventory();
			String inventoryCode = inventory.getCode();
			Double quantity = detail.getDeliveredQuantity();
			
			Double inventoryQuantity = deliverySummary.get(inventoryCode);
			if (inventoryQuantity == null) {
				inventoryQuantity = 0D;
			}
			inventoryQuantity += quantity;
			deliverySummary.put(inventoryCode, inventoryQuantity);
			
			Double orderQuantity = detail.getPurchaseOrderDetail().getQuantity();
			
			Double orderSummaryQuantity = orderSummary.get(inventoryCode);
			if (orderSummaryQuantity == null) {
				orderSummaryQuantity = 0D;
			}
			orderSummaryQuantity += orderQuantity;
			orderSummary.put(inventoryCode, orderSummaryQuantity);
		}
		
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();		
		
		for(DeliveryDetail detail : deliveryDetailList) {
			
			Map<String, String> temp = new HashMap<String, String>();
			PurchaseOrderDetail orderDetail = detail.getPurchaseOrderDetail();
			Inventory inventory = detail.getPurchaseOrderDetail().getInventory();
			
			String inventoryCode = inventory.getCode();
			if (deliverySummary.containsKey(inventoryCode)) {
				temp.put("material_code", inventory.getCode());
				temp.put("name", inventory.getName());
				temp.put("specs", inventory.getSpecs());
				temp.put("packing_quantity", String.valueOf(orderDetail.getCountPerBox()));
				temp.put("quantity", String.valueOf(orderSummary.get(inventoryCode)));
				temp.put("Shipped", String.valueOf(deliverySummary.get(inventoryCode)));
				temp.put("serial", deliveryMain.getDeliverNumber());
				
				//box == 1 固定   box ==2 浮动
				if (inventory.getBoxClass() == null) {
					temp.put("box", "2"); 
				} else {
					temp.put("box", "1");
				}
				
				data.add(temp);
				deliverySummary.remove(inventoryCode);
			}
			
		}
		
		response.put("error_code", RESPONSE_SUCCESS);
		response.put("msg", "成功获取预发货单");

		response.put("supplier_code", deliveryMain.getVendor().getCode());
		response.put("code", deliveryMain.getCode());
		if (deliveryMain.getState() == Constants.DELIVERY_STATE_OK) {
			response.put("state", "1");
		} else if (deliveryMain.getState() == Constants.DELIVERY_STATE_DELIVERED) {
			response.put("state", "2");
		} else if (deliveryMain.getState() == Constants.DELIVERY_STATE_ARRIVED) {
			response.put("state", "3");
		} else {
			response.put("state", "0");
		}
		response.put("data", data);
		
		
		return response;
	}
	
	private RestApiResponse postForArrivalVouch(DeliveryMain deliveryMain) {
		
		if (deliveryMain == null) {
			return null;
		} else {
			
			List<DeliveryDetail> details = deliveryDetailRepository.findDetailsByCode(deliveryMain.getCode());
			
			Map<String, Object> postData = new HashMap<>();
			postData.put("ccode", deliveryMain.getCode());
			postData.put("ddate", Utils.formatDateTime(new Date()));
			postData.put("cvencode", deliveryMain.getVendor().getCode());
			postData.put("itaxrate", "0.0");
			postData.put("cmemo", "");
			postData.put("cpocode", "");
			postData.put("cbustype", "00");
			
			
			List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
			
			for(DeliveryDetail detail : details) {
				Map<String, Object> detailData = new HashMap<>();
				PurchaseOrderDetail orderDetail = detail.getPurchaseOrderDetail();
				detailData.put("cinvcode", orderDetail.getInventory().getCode());
				detailData.put("qty", detail.getDeliveredQuantity());
				detailData.put("inum", 1);
				detailData.put("itaxrate", orderDetail.getTaxRate());
				detailData.put("iposid", orderDetail.getOriginalId());
				detailData.put("cpocode", orderDetail.getMain().getCode());
				detailData.put("ivouchrowno", detail.getRowNo());
				detailData.put("fprice", orderDetail.getPrice());
				detailData.put("famount", orderDetail.getMoney());
				detailData.put("ftaxprice", orderDetail.getTaxPrice());
				detailData.put("ftaxamount", orderDetail.getSum());
				
				detailList.add(detailData);
			}	
			
			
			postData.put("detail", detailList);
			
			RestApiResponse response = apiClient.postForArrivalVouch(postData);
			
			return response;
		}
		
	}
}
