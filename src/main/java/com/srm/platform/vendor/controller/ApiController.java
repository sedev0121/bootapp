package com.srm.platform.vendor.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Box;
import com.srm.platform.vendor.model.Company;
import com.srm.platform.vendor.model.DeliveryDetail;
import com.srm.platform.vendor.model.DeliveryMain;
import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.model.Master;
import com.srm.platform.vendor.model.Notice;
import com.srm.platform.vendor.model.NoticeRead;
import com.srm.platform.vendor.model.OperationHistory;
import com.srm.platform.vendor.model.PurchaseInDetail;
import com.srm.platform.vendor.model.PurchaseOrderDetail;
import com.srm.platform.vendor.model.StatementCompany;
import com.srm.platform.vendor.model.StatementDetail;
import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.model.Task;
import com.srm.platform.vendor.model.TaskLog;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.BoxRepository;
import com.srm.platform.vendor.repository.CompanyRepository;
import com.srm.platform.vendor.repository.DeliveryDetailRepository;
import com.srm.platform.vendor.repository.DeliveryMainRepository;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.MasterRepository;
import com.srm.platform.vendor.repository.NoticeReadRepository;
import com.srm.platform.vendor.repository.NoticeRepository;
import com.srm.platform.vendor.repository.OperationHistoryRepository;
import com.srm.platform.vendor.repository.PermissionGroupRepository;
import com.srm.platform.vendor.repository.PurchaseInDetailRepository;
import com.srm.platform.vendor.repository.StatementCompanyRepository;
import com.srm.platform.vendor.repository.StatementDetailRepository;
import com.srm.platform.vendor.repository.StatementMainRepository;
import com.srm.platform.vendor.repository.TaskLogRepository;
import com.srm.platform.vendor.repository.TaskRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.searchitem.DimensionTargetItem;
import com.srm.platform.vendor.searchitem.StatementPendingDetail;
import com.srm.platform.vendor.searchitem.StatementPendingItem;
import com.srm.platform.vendor.u8api.RestApiClient;
import com.srm.platform.vendor.u8api.RestApiResponse;
import com.srm.platform.vendor.utility.AccountPermission;
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
	public HttpSession httpSession;
	
	@Autowired
	private RestApiClient apiClient;
	
	@Autowired
	public OperationHistoryRepository operationHistoryRepository;
	
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
	
	@Autowired
	public MasterRepository masterRepository;
	
	@Autowired
	public TaskRepository taskRepository;
	
	@Autowired
	public TaskLogRepository taskLogRepository;
	
	@Autowired
	public VendorRepository vendorRepository;
	
	@Autowired
	public CompanyRepository companyRepository;
	
	@Autowired
	public StatementCompanyRepository statementCompanyRepository;
	
	@Autowired
	public PermissionGroupRepository permissionGroupRepository;
	
	private void addOpertionHistory(String targetId, String action, String content, String type, Account account) {
		OperationHistory operationHistory = new OperationHistory();
		operationHistory.setTargetId(targetId);
		operationHistory.setTargetType(type);
		operationHistory.setAction(action);
		operationHistory.setContent(content);
		operationHistory.setAccount(account);

		operationHistory = operationHistoryRepository.save(operationHistory);
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
	@RequestMapping({ "/delete_pi_rows" })
	public Map<String, Object> deletePIRows(@RequestBody Map<String, Object> requestParams) {
		
		List<String> autoIdList = (List<String>)requestParams.get("auto_ids");
		
		logger.info("=========/api/delete_pi_rows============");
		logger.info("<<< " + Utils.convertMapToJson(requestParams));
		
		List<PurchaseInDetail> list = new ArrayList<PurchaseInDetail>();
		
		for(String autoId : autoIdList) {
			if (Utils.isEmpty(autoId)) {				
				continue;
			}
			
			PurchaseInDetail temp = purchaseInDetailRepository.findOneByAutoId(Long.parseLong(autoId));
			if (temp != null) {
				logger.info("成功删除入库单行[" + autoId + "]");
				temp.setState(Constants.PURCHASE_IN_STATE_DELETED);
				list.add(temp);	
			} else {
				logger.error("找不到入库单行[" + autoId + "]");
			}
		}
		
		purchaseInDetailRepository.saveAll(list);
		
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("error_code", RESPONSE_SUCCESS);
		response.put("msg", "成功");
		
		logger.info(">>> " + Utils.convertMapToJson(response));
		return response;
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
				box.setEmpty();
				box = boxRepository.save(box);	
			}			
		}
		
		return jsonResponse;

	}

	@ResponseBody
	@RequestMapping({ "/pda" })
	public Map<String, Object> pda(@RequestBody Map<String, Object> requestParams) {
		String method = String.valueOf(requestParams.get("method"));
		Object content = requestParams.get("content");
		
		logger.info("=========/api/pda============");
		logger.info("<<< " + Utils.convertMapToJson(requestParams));
		
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
		} else if (method.equals("createTransferBoxMsg")) {
			response = this.createTransferBoxMsg(requestParams);
		} else if (method.equals("cancelDHD")) {
			response = this.cancelDHD(requestParams);
		}
		
		logger.info(">>> " + Utils.convertMapToJson(response));
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
		
		box.setEmpty();
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
			
			List<Box> oldBoxList = boxRepository.findAllByDeliveryCodeAndType(deliveryCode, Constants.BOX_TYPE_DELIVERY);
			
			List<Box> emptyList = new ArrayList<Box>();
			for(Box box : oldBoxList) {
				if (box.getBoxClass() == null) {
					continue;
				}
				
				box.setEmpty();
				
				emptyList.add(box);
			}
			
			boxRepository.saveAll(emptyList);
			
			
			List<Box> bindingBoxList = new ArrayList<Box>();
			for(Map<String, String> data : content) {
				String quantityStr = data.get("quantity");
				String boxCode = data.get("BoxCode");
				String inventoryCode = data.get("material_code");
				Inventory inventory = inventoryRepository.findOneByCode(inventoryCode);
				Double quantity = Double.parseDouble(quantityStr);
				
				Box box = boxRepository.findOneByCode(boxCode);
								
				box.setDeliveryCode(deliveryCode);
				box.setDeliveryNumber(deliveryMain.getDeliverNumber());
				box.setVendorCode(deliveryMain.getVendor().getCode());
				box.setVendorName(deliveryMain.getVendor().getName());
				box.setInventoryCode(inventory.getCode());
				box.setInventoryName(inventory.getName());
				box.setInventorySpecs(inventory.getSpecs());
				
				box.setQuantity(quantity);
				box.setBindDate(new Date());
				box.setType(Constants.BOX_TYPE_DELIVERY);
				
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
	
	private Map<String, Object> createTransferBoxMsg(Map<String, Object> requestParams) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		String transferCode = String.valueOf(requestParams.get("code"));

		List<Map<String, String>> content = (List<Map<String, String>>)requestParams.get("content");
		
		if (transferCode == null || content == null || content.size() == 0) {
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "参数不正确");	
			return response;
		}
		
		List<Box> oldBoxList = boxRepository.findAllByDeliveryCodeAndType(transferCode, Constants.BOX_TYPE_DIAOBO);
		
		if (oldBoxList.size() > 0) {
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "调拨单已绑定");
			return response;
		}
		
		List<Box> bindingBoxList = new ArrayList<Box>();
		
		for(Map<String, String> data : content) {
			String quantityStr = data.get("quantity");
			Double quantity = Double.parseDouble(quantityStr);
			
			String boxCode = data.get("BoxCode");
			String inventoryCode = data.get("material_code");
			String inventoryName = data.get("name");
			String inventorySpecs = data.get("specs");
			String storeName = data.get("warehouse_name");
			String storeCode = data.get("warehouse_id");
			
			Box box = boxRepository.findOneByCode(boxCode);
							
			if (box == null) {
				response.put("error_code", RESPONSE_FAIL);
				response.put("msg", "找不到箱码" + boxCode);
				return response;
			}
			
			box.setDeliveryCode(transferCode);
			box.setDeliveryNumber(null);
			box.setVendorCode(storeCode);
			box.setVendorName(storeName);
			box.setInventoryCode(inventoryCode);
			box.setInventoryName(inventoryName);
			box.setInventorySpecs(inventorySpecs);
			
			box.setQuantity(quantity);
			box.setBindDate(new Date());
			box.setType(Constants.BOX_TYPE_DIAOBO);
			
			box.setUsed(Box.BOX_IS_USING);
			
			bindingBoxList.add(box);			
		}
		
		boxRepository.saveAll(bindingBoxList);		
		
		response.put("error_code", RESPONSE_SUCCESS);
		response.put("msg", "提交成功");	
		
		return response;
	}
	
	private Map<String, Object> cancelDHD(Map<String, Object> requestParams) {
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
		} else if (deliveryMain.getState() != Constants.DELIVERY_STATE_ARRIVED) {
			response.put("error_code", RESPONSE_FAIL);
			response.put("msg", "该发货单还未收货，不能拒收");			
		} else {
			List<DeliveryDetail> canceledDeliveryDetailList = new ArrayList<DeliveryDetail>();
			
			for(Map<String, String> data : content) {
				String rowNoStr = data.get("rowNo");
				String inventoryCode = data.get("material_code");
				String quantityStr = data.get("quantity");
				String reason = data.get("reason");
				
				
				if (rowNoStr == null || inventoryCode == null || quantityStr == null || inventoryCode == null || reason == null) {
					response.put("error_code", RESPONSE_FAIL);
					response.put("msg", "参数不正确");	
					return response;
				}
				
				Integer rowNo = Integer.valueOf(rowNoStr);
				double quantity = Double.parseDouble(quantityStr);
				
				DeliveryDetail detail = deliveryDetailRepository.findOneByCodeAndRowNo(deliveryCode, rowNo);
				if (detail == null) {
					response.put("error_code", RESPONSE_FAIL);
					response.put("msg", "找不到行号为" + rowNo + "的货品");	
					return response;
				} else {
					String detailInventoryCode = detail.getPurchaseOrderDetail().getInventory().getCode();

					if (!detailInventoryCode.equals(inventoryCode)) {
						response.put("error_code", RESPONSE_FAIL);
						response.put("msg", "行号为" + rowNo + "的货品编码（"+ detailInventoryCode + "）不一致");	
						return response;
					} else {
						detail.setCancelQuantity(quantity);
						detail.setCancelReason(reason);
						detail.setCancelDate(new Date());
						
						canceledDeliveryDetailList.add(detail);
						
					}
				}
			}
			
			deliveryDetailRepository.saveAll(canceledDeliveryDetailList);
			
			addOpertionHistory(deliveryMain.getCode(), "拒收", "API", "delivery", deliveryMain.getCreater());
			
			response.put("error_code", RESPONSE_SUCCESS);
			response.put("msg", "");	
		}
		
		return response;
	}
	
	private Map<String, Object> createDHD(Map<String, Object> requestParams) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		String deliveryCode = String.valueOf(requestParams.get("code"));

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
				addOpertionHistory(deliveryMain.getCode(), "已收货", "API", "delivery", deliveryMain.getCreater());
				
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
		String isMes = String.valueOf(requestParams.get("isMes"));
		
		if (boxCode == null || isMes == null) {
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
		} else {
			Inventory inventory = inventoryRepository.findOneByCode(box.getInventoryCode());	
			DeliveryMain deliveryMain = deliveryMainRepository.findOneByCode(box.getDeliveryCode());
			
			if (inventory == null || deliveryMain == null) {
				response = new HashMap<String, Object>();
				response.put("error_code", RESPONSE_FAIL);
				response.put("msg", "没有绑定");
			} else {
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
			}
		}
		
//		if (IS_MES.equals(isMes) && response.get("error_code") == RESPONSE_FAIL ) {
//			RestApiResponse u8Response = apiClient.getBoxMsg(content);
//			Map<String, Object> responseMapData = u8Response.getOriginalMap();
//			String error_code = String.valueOf(responseMapData.get("error_code"));
//			if (RESPONSE_SUCCESS.equals(error_code)) {
//				response.put("error_code", RESPONSE_SUCCESS);
//				response.put("msg", "成功获取装箱清单");
//				
//				response.put("supplier_code", responseMapData.get("supplier_code"));
//				response.put("code", responseMapData.get("code"));
//				response.put("invoice_code", responseMapData.get("invoice_code"));
//				response.put("data", responseMapData.get("data"));				
//			} else {
//				String msg = String.valueOf(responseMapData.get("msg"));
//				response.put("msg", msg);
//			}
//		}
		
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
			
			RestApiResponse response = Utils.postForArrivalVouch(deliveryMain, details, apiClient);
			
			return response;
		}
		
	}
	
	@ResponseBody
	@RequestMapping({ "/statement/all" })
	public GenericJsonResponse<String> statement() {
		if (!isAutoTaskStartTime()) {
			return new GenericJsonResponse<>(GenericJsonResponse.FAILED, "没有到时间", null);
		} else {
			logger.info("========/statement/all ==========");
			logger.info("自动运行对账单生成");
			return statement(null, null);	
		}
		
	}
	
	@ResponseBody
	@RequestMapping({ "/statement/{date}" })
	public GenericJsonResponse<String> statementForAccount(@PathVariable("date") String date) {
		Account loginAccount = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			loginAccount = accountRepository.findOneByUsername(authentication.getName());	
		}	
		
		GenericJsonResponse<String> response = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, null);
		if (loginAccount == null) {
			response = new GenericJsonResponse<>(GenericJsonResponse.FAILED, "没有权限", null);
		} else {
			response = statement(date, loginAccount);
		}
		return response;		
	}
	
	private GenericJsonResponse<String> statement(String dateStr, Account loginAccount) {			
		
		String actionName = dateStr==null?"自动批量生成":"手动批量生成";
		Date statementDate;
		if (dateStr == null) {
			Master master = masterRepository.findOneByItemKey(Constants.KEY_AUTO_TASK_STATEMENT_DATE);
			if (master == null) {
				master = new Master();	
				master.setItemKey(Constants.KEY_AUTO_TASK_STATEMENT_DATE);
				master.setItemValue(Constants.DEFAULT_STATEMENT_DATE);
				masterRepository.save(master);
			}
			
			statementDate = Utils.getStatementDate(master.getItemValue());	
		} else {
			statementDate = Utils.parseDate(dateStr);
		}
		
		Date filterDate = Utils.getNextDate(statementDate);		
		
		List<StatementPendingItem> pendingDataList = this.statementDetailRepository.findAllPendingData(filterDate);
		List<StatementPendingItem> filteredList = new ArrayList<StatementPendingItem>();
		if (loginAccount != null) {
			AccountPermission accountPermission = getPermissionScopeOfStatement(loginAccount.getId());	
			List<Long> allowedCompanyIdList = accountPermission.getCompanyList();
			if (!(allowedCompanyIdList == null || allowedCompanyIdList.size() == 0)) {
				
				List<StatementCompany> statementCompanyList = statementCompanyRepository.findStatementCompanys(allowedCompanyIdList);	
				List<Long> statementCompanyIdList = new ArrayList<Long>();
				for(StatementCompany temp : statementCompanyList) {
					statementCompanyIdList.add(temp.getId());
				}
				
				for (StatementPendingItem item : pendingDataList) {
					if (statementCompanyIdList.contains(item.getStatement_company_id())) {
						filteredList.add(item);
					}
				}
				pendingDataList = filteredList;
			}

			List<String> allowedVendorCodeList = accountPermission.getVendorList();
			if (!(allowedVendorCodeList == null || allowedVendorCodeList.size() == 0)) {
				filteredList = new ArrayList<StatementPendingItem>();
				for (StatementPendingItem item : pendingDataList) {
					if (allowedVendorCodeList.contains(item.getVendor_code())) {
						filteredList.add(item);
					}
				}
				pendingDataList = filteredList;
			}
		} else {
			loginAccount = accountRepository.findOneByRole("ROLE_ADMIN");
		}
		
		Task task = new Task();
		task.setStatementDate(statementDate);
		task.setMakeDate(new Date());
		task.setMaker(loginAccount);
		task = taskRepository.save(task);
		
		StatementMain main;
		
		for (StatementPendingItem item : pendingDataList) {
			String vendorCode = item.getVendor_code();
			Long statementCompanyId = item.getStatement_company_id();
			String type = item.getType();
			
			main = generateStatementMain(statementDate, vendorCode, statementCompanyId, type, task);
			generateStatementDetails(main);
			if (statementDetailRepository.findByCode(main.getCode()).size() == 0) {
				statementMainRepository.delete(main);
			} else {
				saveTaskLog(main, task);
				saveOperationHistory(main, actionName);	
			}					
		}
		
		GenericJsonResponse<String> response = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, null);	
		
		
		return response;
	}
	
	private StatementMain generateStatementMain(Date statementDate, String vendorCode, Long statementCompanyId, String type, Task task) {
		logger.info(String.format("生成对账单 供应商=%s, 结算实体=%d, 类型=%s, 任务编号=%s", vendorCode, statementCompanyId, type, task.getCode()));
		StatementMain main = new StatementMain();		
		
		Integer statementType = "普通采购".equalsIgnoreCase(type)? Constants.STATEMENT_TYPE_BASIC : Constants.STATEMENT_TYPE_WEIWAI;
		Vendor vendor = vendorRepository.findOneByCode(vendorCode);
		StatementCompany statementCompany = statementCompanyRepository.findOneById(statementCompanyId);
		
		main.setDate(statementDate);
		main.setVendor(vendor);
		main.setStatementCompany(statementCompany);
		main.setType(statementType);
		main.setTaskCode(task.getCode());
		main.setMakeDate(new Date());
		main.setState(Constants.STATEMENT_STATE_NEW);
		main.setMaker(task.getMaker());
		main = statementMainRepository.save(main);
		return main;
	}
	
	private void generateStatementDetails(StatementMain main) {
		logger.info("生成对账单明细 ");
		String vendorCode = main.getVendor().getCode();
		Long statementCompanyId = main.getStatementCompany().getId();
		String type = main.getType() == 1 ? "普通采购":"委外加工";
		Date filterDate = Utils.getNextDate(main.getDate());
		
		List<StatementPendingDetail> pendingDetailList = this.statementDetailRepository.findAllPendingDetail(vendorCode, statementCompanyId, type, filterDate);
		int index = 1;
		double costSum = 0, taxCostSum = 0, taxSum = 0;
		List<PurchaseInDetail> purchaseInDetailList = new ArrayList<PurchaseInDetail>();
		List<StatementDetail> statementDetailList = new ArrayList<StatementDetail>();
		
		for (StatementPendingDetail detail : pendingDetailList) {
			PurchaseInDetail purchaseInDetail = purchaseInDetailRepository.findOneById(detail.getId());
			if (purchaseInDetail.getState() != Constants.PURCHASE_IN_STATE_WAIT) {
				this.logger.info(String.format("Details continue=> %s %s", detail.getId(), detail.getCode()));
				continue;
			}			
			
			Double cost = purchaseInDetail.getCost();
			Double taxCost = purchaseInDetail.getTaxCost();
			
			costSum += (cost==null)?0:cost;
			taxCostSum += (taxCost==null)?0:taxCost;
			
			purchaseInDetail.setState(Constants.PURCHASE_IN_STATE_START);				
			purchaseInDetailList.add(purchaseInDetail);
			
			StatementDetail statementDetail = new StatementDetail();
			statementDetail.setCode(main.getCode());
			statementDetail.setPiDetailId(detail.getId());
			statementDetail.setRowNo(index++);
			statementDetail.setAdjustTaxCost(0D);
			
			statementDetailList.add(statementDetail);
			
			logger.info(String.format("生成对账单明细  入库单AutoID=%d", purchaseInDetail.getAutoId()));
		}
		
		purchaseInDetailRepository.saveAll(purchaseInDetailList);
		statementDetailRepository.saveAll(statementDetailList);
		
		taxSum = taxCostSum - costSum;
		
		main.setCostSum(costSum);
		main.setTaxCostSum(taxCostSum);
		main.setTaxSum(taxSum);
		main.setAdjustCostSum(0D);
		main = statementMainRepository.save(main);
	}
	
	private TaskLog saveTaskLog(StatementMain main, Task task) {
		TaskLog taskLog = new TaskLog();
		taskLog.setTask(task);
		taskLog.setVendor(main.getVendor());
		taskLog.setCreateDate(main.getMakeDate());
		taskLog.setStatement(main);
		taskLog.setState(1);
		
		taskLog = taskLogRepository.save(taskLog);
		return taskLog;
	}
	
	private void saveOperationHistory(StatementMain main, String actionName) {
		OperationHistory operationHistory = new OperationHistory();
		operationHistory.setTargetId(main.getCode());
		operationHistory.setTargetType("statement");
		operationHistory.setAction(actionName);
		operationHistory.setAccount(main.getMaker());

		operationHistory = operationHistoryRepository.save(operationHistory);
	}
	
	private AccountPermission getPermissionScopeOfStatement(Long accountId) {
		Long STATEMENT_LIST_FUNCTION_ACTION_ID = 21L;
		
		List<DimensionTargetItem> scopeList = permissionGroupRepository.findPermissionScopeOf(accountId, STATEMENT_LIST_FUNCTION_ACTION_ID);
		AccountPermission accountPermission = new AccountPermission(accountId, STATEMENT_LIST_FUNCTION_ACTION_ID, scopeList);
		return accountPermission;
	}
	

	private boolean isAutoTaskStartTime() {
		String dateStr, timeStr;
		Master master = masterRepository.findOneByItemKey(Constants.KEY_AUTO_TASK_START_DATE);
		if (master == null) {
			master = new Master();	
			master.setItemKey(Constants.KEY_AUTO_TASK_STATEMENT_DATE);
			master.setItemValue(Constants.DEFAULT_STATEMENT_DATE);
			masterRepository.save(master);
		}
		dateStr = master.getItemValue();
		
		master = masterRepository.findOneByItemKey(Constants.KEY_AUTO_TASK_START_TIME);
		if (master == null) {
			master = new Master();	
			master.setItemKey(Constants.KEY_AUTO_TASK_START_TIME);
			master.setItemValue(Constants.DEFAULT_STATEMENT_TIME);
			masterRepository.save(master);
		}

		timeStr = master.getItemValue();
		
		Date today = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		String yearMonthStr = dateFormat.format(today);
		String startDateStr = yearMonthStr + "-" + dateStr + " " + timeStr + ":00";
		Date startDate = Utils.parseDateTime(startDateStr);
		
		String startTime = Utils.formatStatementDateTime(startDate);
		String todayTime = Utils.formatStatementDateTime(today);
		
		if (startTime.equalsIgnoreCase(todayTime)) {
			return true;
		} else {
			return false;
		}		
	}
	
	@ResponseBody
	@RequestMapping({ "/test" })
	public boolean test() {
		List<Long> array = new ArrayList<Long>();
		array.add(1L);
		array.add(2L);
		array.add(3L);
		
		boolean result = array.contains(1L);
		
		return result;
		
	}
}
