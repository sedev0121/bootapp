package com.srm.platform.vendor.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.model.InventoryClass;
import com.srm.platform.vendor.model.Master;
import com.srm.platform.vendor.model.MeasurementUnit;
import com.srm.platform.vendor.model.PurchaseInDetail;
import com.srm.platform.vendor.model.PurchaseInMain;
import com.srm.platform.vendor.model.PurchaseOrderDetail;
import com.srm.platform.vendor.model.PurchaseOrderMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.InventoryClassRepository;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.MasterRepository;
import com.srm.platform.vendor.repository.MeasurementUnitRepository;
import com.srm.platform.vendor.repository.PurchaseInDetailRepository;
import com.srm.platform.vendor.repository.PurchaseInMainRepository;
import com.srm.platform.vendor.repository.PurchaseOrderDetailRepository;
import com.srm.platform.vendor.repository.PurchaseOrderMainRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.service.EmailService;
import com.srm.platform.vendor.u8api.ApiClient;
import com.srm.platform.vendor.u8api.AppProperties;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.Utils;

@RestController
@RequestMapping(path = "/sync_old")
public class SyncControllerOld {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ApiClient apiClient;

	@Autowired
	private HttpSession httpSession;

	@Autowired
	private AppProperties appProperties;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private PurchaseOrderMainRepository purchaseOrderMainRepository;

	@Autowired
	private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

	@Autowired
	private PurchaseInMainRepository purchaseInMainRepository;

	@Autowired
	private PurchaseInDetailRepository purchaseInDetailRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private MeasurementUnitRepository measurementUnitRepository;

	@Autowired
	private InventoryClassRepository inventoryClassRepository;

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private MasterRepository masterRepository;
	
	@ResponseBody
	@GetMapping({ "", "/", "/daily" })
	public boolean index() {
		this.measurementunit();
		this.inventoryDaily();
		this.vendorDaily();
		this.purchaseInDaily();
		this.purchaseorderDaily();	
		

		return true;
	}

	@ResponseBody
	@GetMapping({ "/init", "/init/" })
	public boolean initAll() {
		this.measurementunit();
		
		boolean result = false;
		result = this.inventoryInit();
		if (result) {
			this.saveLastSyncDate(Constants.KEY_SYNC_INVENTORY);
		}
		
		this.vendorInit();
		
		result = this.purchaseInInit();
		if (result) {
			this.saveLastSyncDate(Constants.KEY_SYNC_PURCHASE_IN);
		}
		
		result = this.purchaseorderInit();
		if (result) {
			this.saveLastSyncDate(Constants.KEY_SYNC_PURCHASE_ORDER);
		}

		return true;
	}

	@ResponseBody
	@RequestMapping({ "/vendor/init", "/vendor", "/vendor/" })
	public boolean vendorInit() {
		vendorRepository.deleteAll();

		return vendor(null);
	}

	@ResponseBody
	@RequestMapping(value = "/vendor/daily")
	public boolean vendorDaily() {
		String maxTimestamp = vendorRepository.findMaxTimestamp();

		return vendor(maxTimestamp);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	private boolean vendor(String beginTimeStamp) {

		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, String> requestParams;
		int i = 0, total_page = 1;
		List<LinkedHashMap<String, String>> vendorList;

		Map<String, Object> map = new HashMap<>();

		try {
			do {

				map = new HashMap<>();

				requestParams = new HashMap<>();

				requestParams.put("rows_per_page", "10");
				requestParams.put("page_index", Integer.toString(++i));

				if (beginTimeStamp != null) {
					requestParams.put("timestamp_begin", beginTimeStamp);
				}

				String response = apiClient.getBatchVendor(requestParams);
				map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
				});

				int errorCode = Integer.parseInt((String) map.get("errcode"));
				String errMsg = (String) map.get("errmsg");
				
				if (errorCode == appProperties.getError_code_success()) {
					total_page = Integer.parseInt((String) map.get("page_count"));
					vendorList = (List<LinkedHashMap<String, String>>) map.get("vendor");
					for (LinkedHashMap<String, String> temp : vendorList) {

						Vendor vendor = vendorRepository.findOneByCode(temp.get("code"));
						if (vendor == null) {
							vendor = new Vendor();
							vendor.setCode(temp.get("code"));
						}

						vendor.setAbbrname(temp.get("abbrname"));
						vendor.setAddress(temp.get("address"));
						vendor.setBankAccNumber(temp.get("bank_acc_number"));
						vendor.setBankOpen(temp.get("bank_open"));
						vendor.setCode(temp.get("code"));
						vendor.setContact(temp.get("contact"));
						vendor.setEmail(temp.get("email"));
						vendor.setEndDate(Utils.parseDateTime(temp.get("end_date")));
						vendor.setFax(temp.get("fax"));
						vendor.setIndustry(temp.get("industry"));
						vendor.setMemo(temp.get("memo"));
						vendor.setMobile(temp.get("mobile"));
						vendor.setName(temp.get("name"));
						vendor.setReceiveSite(temp.get("receive_site"));
						vendor.setSortCode(temp.get("sort_code"));
						

						vendorRepository.save(vendor);
					}
				} else if (errorCode == 20002) {
					return true;
				} else {
					sendSyncErrorEmail("供应商", errMsg);
					return false;
				}

			} while (i < total_page);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
			sendSyncErrorEmail("供应商", e.getMessage());
			return false;
		}

		return true;
	}

	@ResponseBody
	@RequestMapping(value = { "/inventory/init", "/inventory", "/inventory/" })
	public boolean inventoryInit() {
		inventoryRepository.deleteAllInBatch();
		boolean result = inventory(null);
		if (result) {
			saveLastSyncDate(Constants.KEY_SYNC_INVENTORY);
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/inventory/daily")
	public boolean inventoryDaily() {
		Date lastSyncDate = getLastSyncDate(Constants.KEY_SYNC_INVENTORY);
		boolean result = inventory(lastSyncDate);
		if (result) {
			saveLastSyncDate(Constants.KEY_SYNC_INVENTORY);
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	private boolean inventory(Date beginDate) {

		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, String> requestParams;
		int i = 0, total_page = 1;
		List<LinkedHashMap<String, String>> tempList;

		Map<String, Object> map = new HashMap<>();

		try {
			do {

				map = new HashMap<>();

				requestParams = new HashMap<>();

				requestParams.put("rows_per_page", "10");
				requestParams.put("page_index", Integer.toString(++i));

				if (beginDate != null)
					requestParams.put("modifydate_begin", Utils.formatDate(beginDate));

				String response = apiClient.getBatchInventory(requestParams);
				map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
				});

				int errorCode = Integer.parseInt((String) map.get("errcode"));
				String errMsg = (String) map.get("errmsg");
				
				if (errorCode == appProperties.getError_code_success()) {
					total_page = Integer.parseInt((String) map.get("page_count"));
					tempList = (List<LinkedHashMap<String, String>>) map.get("inventory");
					for (LinkedHashMap<String, String> temp : tempList) {

						Inventory inventory = new Inventory();
//						String tempValue = temp.get("bottom_sale_price");
//						if (tempValue != null)
//							inventory.setBottomSalePrice(Float.parseFloat(tempValue));
//
//						inventory.setCode(temp.get("code"));
//						inventory.setDefwarehouse(temp.get("defwarehouse"));
//						inventory.setDefwarehousename(temp.get("defwarehousename"));
//						inventory.setDrawtype(temp.get("drawtype"));
//
//						inventory.setEndDate(Utils.parseDateTime(temp.get("end_date")));
//
//						tempValue = temp.get("iimptaxrate");
//						if (tempValue != null)
//							inventory.setIimptaxrate(Float.parseFloat(temp.get("iimptaxrate")));
//						inventory.setInvaddcode(temp.get("invaddcode"));
//						inventory.setiSupplyType(temp.get("iSupplyType"));
//						inventory.setMainMeasure(measurementUnitRepository.findOneByCode(temp.get("main_measure")));
//
//						inventory.setModifyDate(Utils.parseDateTime(temp.get("ModifyDate")));
//
//						inventory.setName(temp.get("name"));
//						inventory.setPuunitCode(temp.get("puunit_code"));
//
//						tempValue = temp.get("puunit_ichangrate");
//						if (tempValue != null)
//							inventory.setPuunit_ichangrate(Float.parseFloat(tempValue));
//						inventory.setPuunitName(temp.get("puunit_name"));
//
//						tempValue = temp.get("ref_sale_price");
//						if (tempValue != null)
//							inventory.setRefSalePrice(Float.parseFloat(tempValue));
//						inventory.setSaunitCode(temp.get("saunit_code"));
//						inventory.setSaunitName(temp.get("saunit_name"));
//						tempValue = temp.get("saunit_ichangrate");
//						if (tempValue != null)
//							inventory.setSaunitIchangrate(Float.parseFloat(tempValue));
//
//						inventory.setStartDate(Utils.parseDateTime(temp.get("start_date")));
//						inventory.setStunitCode(temp.get("stunit_code"));
//
//						tempValue = temp.get("stunit_ichangrate");
//						if (tempValue != null)
//							inventory.setStunitIchangrate(Float.parseFloat(tempValue));
//						inventory.setStunitName(temp.get("stunit_name"));
//
//						tempValue = temp.get("tax_rate");
//						if (tempValue != null)
//							inventory.setTaxRate(Float.parseFloat(tempValue));
//						inventory.setUnitgroupCode(temp.get("unitgroup_code"));
//
//						tempValue = temp.get("unit_group_type");
//						if (tempValue != null)
//							inventory.setUnitgroupType(Integer.parseInt(tempValue));
//						inventory.setSelfDefine1(temp.get("self_define1"));
//						inventory.setSelfDefine2(temp.get("self_define2"));
//						inventory.setSelfDefine3(temp.get("self_define3"));
//						inventory.setSelfDefine4(temp.get("self_define4"));
//						inventory.setSelfDefine5(temp.get("self_define5"));
//						inventory.setSelfDefine6(temp.get("self_define6"));
//						inventory.setFree1(temp.get("free1"));
//						inventory.setFree2(temp.get("free2"));
//						inventory.setFree3(temp.get("free3"));
//						inventory.setFree4(temp.get("free4"));
//						inventory.setFree5(temp.get("free5"));
//						inventory.setFree6(temp.get("free6"));
//						inventory.setSpecs(temp.get("specs"));

						inventoryRepository.save(inventory);
					}
				} else {
					sendSyncErrorEmail("物料", errMsg);
					return false;
				}

			} while (i < total_page);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			sendSyncErrorEmail("物料", e.getMessage());
			logger.info(e.getMessage());
			return false;
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	@ResponseBody
	@RequestMapping(value = { "/measurementunit/init", "/measurementunit", "/measurementunit/" })
	public boolean measurementunit() {

		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, String> requestParams;
		int i = 0, total_page = 1;
		List<LinkedHashMap<String, String>> tempList;

		Map<String, Object> map = new HashMap<>();

		this.measurementUnitRepository.deleteAll();

		try {
			do {

				map = new HashMap<>();

				requestParams = new HashMap<>();

				requestParams.put("rows_per_page", "10");
				requestParams.put("page_index", Integer.toString(++i));

				String response = apiClient.getBatchMeasurementUnit(requestParams);
				map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
				});

				int errorCode = Integer.parseInt((String) map.get("errcode"));
				String errMsg = (String) map.get("errmsg");
				
				if (errorCode == appProperties.getError_code_success()) {
					total_page = Integer.parseInt((String) map.get("page_count"));
					tempList = (List<LinkedHashMap<String, String>>) map.get("unit");
					for (LinkedHashMap<String, String> temp : tempList) {

						MeasurementUnit unit = new MeasurementUnit();
						unit.setCode(temp.get("code"));
						unit.setGroupCode(temp.get("group_code"));
						unit.setName(temp.get("name"));
						unit.setMainFlag(Boolean.parseBoolean(temp.get("main_flag")));
						measurementUnitRepository.save(unit);
					}
				} else {
					sendSyncErrorEmail("单位", errMsg);
					return false;
				}

			} while (i < total_page);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			sendSyncErrorEmail("单位", e.getMessage());
			logger.info(e.getMessage());
			return false;
		}

		return true;
	}

	

	@ResponseBody
	@RequestMapping(value = { "/purchaseorder/init", "/purchaseorder", "/purchaseorder/" })
	public boolean purchaseorderInit() {
		purchaseOrder(null, null);
		return true;
	}

	@ResponseBody
	@RequestMapping(value = "/purchaseorder/daily")
	public boolean purchaseorderDaily() {
		Date lastSyncDate = getLastSyncDate(Constants.KEY_SYNC_PURCHASE_ORDER);
		boolean result = purchaseOrder(lastSyncDate, null);
		if (result) {
			saveLastSyncDate(Constants.KEY_SYNC_PURCHASE_ORDER);
		}
		
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/purchaseorder/vendor")
	public boolean purchaseorderForVendor() {
		if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
			return false;

		String units = (String) httpSession.getAttribute(Constants.KEY_DEFAULT_UNIT_LIST);
		List<Vendor> vendorList = vendorRepository
				.findVendorsByUnitIdList(Arrays.asList(StringUtils.split(units, ",")));
		for (Vendor vendor : vendorList) {
			purchaseOrder(new Date(), vendor.getCode());
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	private boolean purchaseOrder(Date startDate, String vendorCode) {

		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, String> requestParams;
		int i = 0, total_page = 1;
		List<LinkedHashMap<String, String>> list;

		Map<String, Object> map = new HashMap<>();

		boolean initTable = false;
		if (startDate == null) {
			initTable = true;
		}

		if (initTable) {
			this.purchaseOrderDetailRepository.deleteAllInBatch();
			this.purchaseOrderMainRepository.deleteAllInBatch();
		}

		List<LinkedHashMap<String, String>> tempList;
		
		try {
			do {

				map = new HashMap<>();

				requestParams = new HashMap<>();

				requestParams.put("sys_Order", "");
				requestParams.put("sys_PageIndex", Integer.toString(++i));
				requestParams.put("sys_PageSize", "10");

				requestParams.put("cpoid", "");
				requestParams.put("cbustype", "");
				requestParams.put("date_end", "");
				requestParams.put("verifier_begin", "");
				requestParams.put("verifier_end", "");
				requestParams.put("invcode_begin", "");
				requestParams.put("invcode_end", "");
				
				
				if (startDate != null) {
					requestParams.put("date_begin", Utils.formatDateZeroTime(startDate));
				} else {
					requestParams.put("date_begin","");
				}

				if (vendorCode != null) {
					requestParams.put("vendorcode", vendorCode);
				}else {
					requestParams.put("vendorcode", "");
				}

				String response = apiClient.getLinkU8PurchaseOrder(requestParams);
				response = response.replaceAll("\\\\", "");

				map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
				});

				total_page = Integer.parseInt((String) map.get("page_count"));
				tempList = (List<LinkedHashMap<String, String>>) map.get("list");
				for (LinkedHashMap<String, String> temp : tempList) {
					Date makeDate = Utils.parseDateTime2(temp.get("cmaketime"));
					Vendor vendor = vendorRepository.findOneByCode(temp.get("cVenCode"));

					//TODO:0=新建 1=审核 2=关闭
					int cState = Integer.parseInt(temp.get("cState"));
					if (cState == 0 || vendor == null) {
						continue;
					}

					PurchaseOrderMain main = purchaseOrderMainRepository.findOneByCode(temp.get("cPoid"));
					if (main == null) {
						main = new PurchaseOrderMain();
						main.setCode(temp.get("cPoid"));
					}
					
					String state = "";
					switch(cState) {
					case 1:
						state = "审核";
						break;
					case 2:
						state = "关闭";
						break;
					};
					main.setState(state);

					main.setSrmstate(Constants.PURCHASE_ORDER_STATE_START);
					main.setVendor(vendor);
					
					//TODO: 未税金额
//					if (temp.get("money") != null && !temp.get("money").isEmpty())
//						main.setMoney(Float.parseFloat(temp.get("money")));
					//TODO: 含税金额
//					if (temp.get("sum") != null && !temp.get("sum").isEmpty())
//						main.setSum(Float.parseFloat(temp.get("sum")));
					
					
					main.setPurchaseTypeName(temp.get("cBusType"));
					main.setMaker(temp.get("cMaker"));
					main.setMakedate(makeDate);
					main.setVerifier(temp.get("cVerifier"));
					main.setCloser(temp.get("cCloser"));
					main.setRemark(temp.get("cMemo"));

					purchaseOrderMainRepository.save(main);

					PurchaseOrderDetail detail = purchaseOrderDetailRepository.findOneByCodeAndRowno(main.getCode(),
							temp.get("ivouchrowno"));

					if (detail == null) {
						detail = new PurchaseOrderDetail();
						detail.setMain(main);
					}

					detail.setInventory(inventoryRepository.findOneByCode(temp.get("cInvCode")));
					if (temp.get("iQuantity") != null && !temp.get("iQuantity").isEmpty())
						detail.setQuantity(Float.parseFloat(temp.get("iQuantity")));
					if (temp.get("iNatUnitPrice") != null && !temp.get("iNatUnitPrice").isEmpty())
						detail.setPrice(Float.parseFloat(temp.get("iNatUnitPrice")));
					if (temp.get("iTaxNatPrice") != null && !temp.get("iTaxNatPrice").isEmpty())
						detail.setTaxprice(Float.parseFloat(temp.get("iTaxNatPrice")));
					if (temp.get("iNatSum") != null && !temp.get("iNatSum").isEmpty())
						detail.setSum(Float.parseFloat(temp.get("iNatSum")));
					if (temp.get("iNatMoney") != null && !temp.get("iNatMoney").isEmpty())
						detail.setMoney(Float.parseFloat(temp.get("iNatMoney")));
					if (temp.get("ivouchrowno") != null && !temp.get("ivouchrowno").isEmpty())
						detail.setRowno(Integer.parseInt(temp.get("ivouchrowno")));

					
					String arriveDateStr = temp.get("dArriveDate");
					detail.setArrivedate(Utils.parseDateTime2(arriveDateStr));
					
					detail.setConfirmdate(detail.getArrivedate());
					detail.setConfirmquantity(detail.getQuantity());
					
					purchaseOrderDetailRepository.save(detail);
				}
				
				

			} while (i < total_page);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			sendSyncErrorEmail("订单", e.getMessage());
			logger.info(e.getMessage());
			return false;
		}

		return true;
	}

	
	@ResponseBody
	@RequestMapping(value = { "/purchasein/init", "/purchasein", "/purchasein/" })
	public boolean purchaseInInit() {

		this.purchaseInDetailRepository.deleteAllInBatch();
		this.purchaseInMainRepository.deleteAllInBatch();

		purchaseIn(null, null, false);
		purchaseIn(null, null, true);
		return true;
	}

	@ResponseBody
	@RequestMapping(value = "/purchasein/daily")
	public boolean purchaseInDaily() {
		Date lastSyncDate = getLastSyncDate(Constants.KEY_SYNC_PURCHASE_IN);
		boolean result = purchaseIn(lastSyncDate, null, false);
		
		if (result) {
			result = purchaseIn(lastSyncDate, null, true);	
		}
		
		if (result) {
			saveLastSyncDate(Constants.KEY_SYNC_PURCHASE_IN);
		}
		
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/purchasein/vendor/{vendorCode}")
	public boolean purchaseInForVendor(@PathVariable("vendorCode") String vendorCode) {
		purchaseIn(new Date(), vendorCode, false);
		purchaseIn(new Date(), vendorCode, true);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public boolean purchaseIn(Date startDate, String vendorCode, boolean isWeiwai) {

		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, String> requestParams;
		int i = 0, total_page = 1;
		List<LinkedHashMap<String, String>> tempList;

		Map<String, Object> map = new HashMap<>();

		try {
			do {

				map = new HashMap<>();

				requestParams = new HashMap<>();

				requestParams.put("sys_Order", "");
				requestParams.put("sys_PageIndex", Integer.toString(++i));
				requestParams.put("sys_PageSize", "10");
				requestParams.put("code_begin", "");
				requestParams.put("code_end", "");
				requestParams.put("auditdate_begin", "");
				requestParams.put("auditdate_end", "");
				requestParams.put("warehousecode", "");
				requestParams.put("cPOcode", "");
				requestParams.put("cChangAuditTime_end", "");
				requestParams.put("bredvouch", "");

				if (vendorCode != null) {
					requestParams.put("vendorcode", vendorCode);
				} else {
					requestParams.put("vendorcode", "");
				}

				if (startDate != null) {
					requestParams.put("cChangAuditTime_begin", Utils.formatDateZeroTime(startDate));
				} else {
					requestParams.put("cChangAuditTime_begin", "");
				}

				String response;
				if (isWeiwai) {
					response = apiClient.getLinkU8BatchWeiwai(requestParams);
				} else {
					response = apiClient.getLinkU8BatchBasic(requestParams);
				}
				
				response = response.replaceAll("\\\\", "");

				map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
				});

				total_page = Integer.parseInt(String.valueOf(map.get("page_count")));
				tempList = (List<LinkedHashMap<String, String>>) map.get("list");
				for (LinkedHashMap<String, String> temp : tempList) {
					String code = temp.get("cCode");
					String type = temp.get("cBusType");
					String vendor_code = temp.get("cVenCode");
					String bredvouch = temp.get("bredvouch");
					String warehouse_code = temp.get("cWhCode");
					String warehouse_name = temp.get("cWhName");
					String memo = temp.get("dMemo");
					String date = temp.get("dDate");
					String verifyDate = temp.get("dVeriDate");
					String rowno = String.valueOf(temp.get("irowno"));
					String poCode = String.valueOf(temp.get("cPOID"));

					String inventory_code = temp.get("cInvCode");

					Double quantity = 0D;
					if (temp.get("iQuantity") != null && temp.get("iQuantity").length() > 0)
						quantity = Double.valueOf(String.valueOf(temp.get("iQuantity")));

					Double price = 0d;
					if (temp.get("iUnitCost") != null && temp.get("iUnitCost").length() > 0)
						price = Double.valueOf(String.valueOf(temp.get("iUnitCost")));

					Double cost = 0D;
					if (temp.get("iPrice") != null && temp.get("iPrice").length() > 0)
						cost = Double.valueOf(String.valueOf(temp.get("iPrice")));

					Double tax = 0D;
					if (temp.get("iTaxPrice") != null && temp.get("iTaxPrice").length() > 0)
						tax = Double.valueOf(String.valueOf(temp.get("iTaxPrice")));

					Double tax_price = 0d;
					if (temp.get("iTaxUnitCost") != null && temp.get("iTaxUnitCost").length() > 0)
						tax_price = Double.valueOf(String.valueOf(temp.get("iTaxUnitCost")));

					Double tax_rate = 0D;
					if (temp.get("iTaxRate") != null && temp.get("iTaxRate").length() > 0)
						tax_rate = Double.valueOf(String.valueOf(temp.get("iTaxRate")));

					Double tax_cost = 0d;
					if (temp.get("iSum") != null && temp.get("iSum").length() > 0)
						tax_cost = Double.valueOf(String.valueOf(temp.get("iSum")));

					String detailMemo = temp.get("cbMemo");

					Double nat_price = 0d;
					if (temp.get("iNatUnitPrice") != null && temp.get("iNatUnitPrice").length() > 0)
						nat_price = Double.valueOf(String.valueOf(temp.get("iNatUnitPrice")));

					Double nat_cost = 0d;
					if (temp.get("iNatMoney") != null && temp.get("iNatMoney").length() > 0)
						nat_cost = Double.valueOf(String.valueOf(temp.get("iNatMoney")));

					Double nat_tax_rate = 0D;
					if (temp.get("iPerTaxRate") != null && temp.get("iPerTaxRate").length() > 0)
						nat_tax_rate = Double.valueOf(String.valueOf(temp.get("iPerTaxRate")));

					Double nat_tax = 0d;
					if (temp.get("iNatTax") != null && temp.get("iNatTax").length() > 0)
						nat_tax = Double.valueOf(String.valueOf(temp.get("iNatTax")));

					Double nat_tax_price = 0d;
					if (temp.get("iNatTaxUnitCost") != null && temp.get("iNatTaxUnitCost").length() > 0)
						nat_tax_price = Double.valueOf(String.valueOf(temp.get("iNatTaxUnitCost")));

					Double nat_tax_cost = 0d;
					if (temp.get("iNatSum") != null && temp.get("iNatSum").length() > 0)
						nat_tax_cost = Double.valueOf(String.valueOf(temp.get("iNatSum")));

					String material_code = temp.get("cInvCodeMat");
					String material_name = temp.get("cInvNameMat");
					String material_unitname = temp.get("ccomunitnameMat");

					Long purchaseInDetailId = 0L;

					if (temp.get("AutoID") != null && temp.get("AutoID").length() > 0)
						purchaseInDetailId = Long.valueOf(String.valueOf(temp.get("AutoID")));

					Long purchaseOrderDetailId = 0L;

					if (temp.get("pOAutoID") != null && temp.get("pOAutoID").length() > 0)
						purchaseOrderDetailId = Long.valueOf(String.valueOf(temp.get("pOAutoID")));

					Double material_quantity = null;
					if (temp.get("iQuantityMat") != null && temp.get("iQuantityMat").length() > 0)
						material_quantity = Double.valueOf(String.valueOf(temp.get("iQuantityMat")));

					Double material_price = null;
					if (temp.get("iNatUnitPriceMat") != null && temp.get("iNatUnitPriceMat").length() > 0)
						material_price = Double.valueOf(String.valueOf(temp.get("iNatUnitPriceMat")));

					Double material_tax_price = null;
					if (temp.get("iNatTaxUnitPriceMat") != null && temp.get("iNatTaxUnitPriceMat").length() > 0)
						material_tax_price = Double.valueOf(String.valueOf(temp.get("iNatTaxUnitPriceMat")));
					
					PurchaseInMain main = purchaseInMainRepository.findOneByCode(code);
					if (main == null) {
						main = new PurchaseInMain();
						main.setCode(code);
						if (bredvouch != null && !bredvouch.isEmpty())
							main.setBredvouch(Integer.parseInt(bredvouch));
						main.setWarehouse_code(warehouse_code);
						main.setWarehouse_name(warehouse_name);
						main.setMemo(memo);
						main.setDate(Utils.parseDateTime2(date));
						main.setVerifyDate(Utils.parseDateTime2(verifyDate));

						main.setVendor(vendorRepository.findOneByCode(vendor_code));
						main.setType(type);

						main = purchaseInMainRepository.save(main);
					}

					PurchaseInDetail detail = purchaseInDetailRepository.findOneByCodeAndRowno(code,
							Integer.parseInt(rowno));
					if (detail != null) {
						logger.info("code=" + code + " rowno=" + rowno);
					} else {
						detail = new PurchaseInDetail();
					}

					detail.setMain(main);
					detail.setInventory(inventoryRepository.findOneByCode(inventory_code));

					detail.setQuantity(quantity);

					detail.setPrice(price);
					detail.setPoCode(poCode);

					detail.setPiDetailId(purchaseInDetailId);
					detail.setPoDetailId(purchaseOrderDetailId);

					detail.setRowno(Integer.parseInt(rowno));

					detail.setCost(cost);

					detail.setTax(tax);

					detail.setTaxPrice(tax_price);

					detail.setTaxRate(tax_rate);

					detail.setTaxCost(tax_cost);

					detail.setMemo(detailMemo);

					detail.setNatPrice(nat_price);

					detail.setNatCost(nat_cost);

					detail.setNatTaxRate(nat_tax_rate);

					detail.setNatTax(nat_tax);

					detail.setNatTaxPrice(nat_tax_price);

					detail.setNatTaxCost(nat_tax_cost);

					detail.setMaterialCode(material_code);
					detail.setMaterialName(material_name);
					detail.setMaterialUnitname(material_unitname);

					detail.setMaterialQuantity(material_quantity);

					detail.setMaterialPrice(material_price);

					detail.setMaterialTaxPrice(material_tax_price);

					purchaseInDetailRepository.save(detail);
				}

			} while (i < total_page);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			sendSyncErrorEmail("入库单", e.getMessage());
			logger.info(e.getMessage());
			return false;
		}

		return true;
	}
	
	private void sendSyncErrorEmail(String syncName, String errorMsg) {

		Map<String, Object> model = new HashMap<>();
		model.put("error", errorMsg);
		
//		emailService.sendSyncErrorEmail(syncName, model);
	}

	private Date getLastSyncDate(String key) {
		Date lastSyncDate = new Date();
		Master master = masterRepository.findOneByItemKey(key);
		
		if (master != null) {
			String lastSyncDateStr = master.getItemValue();
			lastSyncDate = Utils.parseDate(lastSyncDateStr);
		}
		return Utils.getStartSyncDate(lastSyncDate);
	}
	
	private void saveLastSyncDate(String key) {
		Date today = new Date();
		String value = Utils.formatDate(today);
		
		Master master = masterRepository.findOneByItemKey(key);
		if (master == null) {
			master = new Master();
			master.setItemKey(key);
		}
		master.setItemValue(value);
		
		masterRepository.save(master);
		
	}
}
