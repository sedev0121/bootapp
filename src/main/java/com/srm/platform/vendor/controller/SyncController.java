package com.srm.platform.vendor.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.model.InventoryClass;
import com.srm.platform.vendor.model.MeasurementUnit;
import com.srm.platform.vendor.model.PurchaseInDetail;
import com.srm.platform.vendor.model.PurchaseInMain;
import com.srm.platform.vendor.model.PurchaseOrderDetail;
import com.srm.platform.vendor.model.PurchaseOrderMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.InventoryClassRepository;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.MeasurementUnitRepository;
import com.srm.platform.vendor.repository.PurchaseInDetailRepository;
import com.srm.platform.vendor.repository.PurchaseInMainRepository;
import com.srm.platform.vendor.repository.PurchaseOrderDetailRepository;
import com.srm.platform.vendor.repository.PurchaseOrderMainRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.u8api.ApiClient;
import com.srm.platform.vendor.u8api.AppProperties;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.Utils;

@RestController
@RequestMapping(path = "/sync")
public class SyncController {
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

	@GetMapping({ "", "/", "/all" })
	public boolean index() {
		this.measurementunit();
		this.inventoryClass();
		this.inventory();
		this.vendorUpdateAll();
		this.purchaseorderInit();

		return true;
	}

	@RequestMapping(value = "/vendor/part")
	public boolean vendorUpatePart() {
		return vendor(false);
	}

	@RequestMapping({ "/vendor/all", "/vendor" })
	public boolean vendorUpdateAll() {
		return vendor(true);
	}

	private Long getLastSyncTime() {

		Date today = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime().getTime();
	}

	private Long getTime(String time) {
		Long result = 0L;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			result = dateFormat.parse(time).getTime();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return result;
	}

	@Transactional
	private boolean vendor(boolean isAll) {

		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, String> requestParams;
		int i = 0, total_page = 1;
		List<LinkedHashMap<String, String>> vendorList;

		Map<String, Object> map = new HashMap<>();

		if (isAll)
			vendorRepository.deleteAll();

		try {
			do {

				map = new HashMap<>();

				requestParams = new HashMap<>();

				requestParams.put("rows_per_page", "10");
				requestParams.put("page_index", Integer.toString(++i));

				String response = apiClient.getBatchVendor(requestParams);
				map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
				});

				logger.info((String) map.get("errcode"));
				int errorCode = Integer.parseInt((String) map.get("errcode"));

				if (errorCode == appProperties.getError_code_success()) {
					total_page = Integer.parseInt((String) map.get("page_count"));
					vendorList = (List<LinkedHashMap<String, String>>) map.get("vendor");
					for (LinkedHashMap<String, String> temp : vendorList) {
						logger.info(temp.get("code") + " " + temp.get("name"));

						Vendor vendor = new Vendor();
						vendor.setCode(temp.get("code"));
						if (this.getTime(temp.get("timeStamp")) >= getLastSyncTime()) {
							Example<Vendor> example = Example.of(vendor);
							Optional<Vendor> result = vendorRepository.findOne(example);
							if (result.isPresent())
								vendor = result.get();
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
						String timeStamp = temp.get("timeStamp");
						if (timeStamp != null)
							vendor.setTimestamp(Instant.ofEpochMilli(Long.parseLong(temp.get("timestamp"))));

						vendorRepository.save(vendor);
					}
				} else {
					return false;
				}

			} while (i < total_page);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
			return false;
		}

		return true;
	}

	@Transactional
	@RequestMapping(value = "/inventory")
	public boolean inventory() {

		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, String> requestParams;
		int i = 0, total_page = 1;
		List<LinkedHashMap<String, String>> tempList;

		Map<String, Object> map = new HashMap<>();

		inventoryRepository.deleteAllInBatch();

		try {
			do {

				map = new HashMap<>();

				requestParams = new HashMap<>();

				requestParams.put("rows_per_page", "10");
				requestParams.put("page_index", Integer.toString(++i));

				String response = apiClient.getBatchInventory(requestParams);
				map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
				});

				int errorCode = Integer.parseInt((String) map.get("errcode"));

				if (errorCode == appProperties.getError_code_success()) {
					total_page = Integer.parseInt((String) map.get("page_count"));
					tempList = (List<LinkedHashMap<String, String>>) map.get("inventory");
					for (LinkedHashMap<String, String> temp : tempList) {
						logger.info(temp.get("code") + " " + temp.get("name"));
						Inventory inventory = new Inventory();
						String tempValue = temp.get("bottom_sale_price");
						if (tempValue != null)
							inventory.setBottomSalePrice(Float.parseFloat(tempValue));

						inventory.setCode(temp.get("code"));
						inventory.setDefwarehouse(temp.get("defwarehouse"));
						inventory.setDefwarehousename(temp.get("defwarehousename"));
						inventory.setDrawtype(temp.get("drawtype"));

						inventory.setEndDate(Utils.parseDateTime(temp.get("end_date")));

						tempValue = temp.get("iimptaxrate");
						if (tempValue != null)
							inventory.setIimptaxrate(Float.parseFloat(temp.get("iimptaxrate")));
						inventory.setInvaddcode(temp.get("invaddcode"));
						inventory.setiSupplyType(temp.get("iSupplyType"));
						inventory.setMainMeasure(measurementUnitRepository.findOneByCode(temp.get("main_measure")));

						inventory.setModifyDate(Utils.parseDateTime(temp.get("ModifyDate")));

						inventory.setName(temp.get("name"));
						inventory.setPuunitCode(temp.get("puunit_code"));

						tempValue = temp.get("puunit_ichangrate");
						if (tempValue != null)
							inventory.setPuunit_ichangrate(Float.parseFloat(tempValue));
						inventory.setPuunitName(temp.get("puunit_name"));

						tempValue = temp.get("ref_sale_price");
						if (tempValue != null)
							inventory.setRefSalePrice(Float.parseFloat(tempValue));
						inventory.setSaunitCode(temp.get("saunit_code"));
						inventory.setSaunitName(temp.get("saunit_name"));
						tempValue = temp.get("saunit_ichangrate");
						if (tempValue != null)
							inventory.setSaunitIchangrate(Float.parseFloat(tempValue));

						inventory.setStartDate(Utils.parseDateTime(temp.get("start_date")));
						inventory.setStunitCode(temp.get("stunit_code"));

						tempValue = temp.get("stunit_ichangrate");
						if (tempValue != null)
							inventory.setStunitIchangrate(Float.parseFloat(tempValue));
						inventory.setStunitName(temp.get("stunit_name"));

						tempValue = temp.get("tax_rate");
						if (tempValue != null)
							inventory.setTaxRate(Float.parseFloat(tempValue));
						inventory.setUnitgroupCode(temp.get("unitgroup_code"));

						tempValue = temp.get("unit_group_type");
						if (tempValue != null)
							inventory.setUnitgroupType(Integer.parseInt(tempValue));
						inventory.setSelfDefine1(temp.get("self_define1"));
						inventory.setSelfDefine2(temp.get("self_define2"));
						inventory.setSelfDefine3(temp.get("self_define3"));
						inventory.setSelfDefine4(temp.get("self_define4"));
						inventory.setSelfDefine5(temp.get("self_define5"));
						inventory.setSelfDefine6(temp.get("self_define6"));
						inventory.setFree1(temp.get("free1"));
						inventory.setFree2(temp.get("free2"));
						inventory.setFree3(temp.get("free3"));
						inventory.setFree4(temp.get("free4"));
						inventory.setFree5(temp.get("free5"));
						inventory.setFree6(temp.get("free6"));
						inventory.setSpecs(temp.get("specs"));

						inventoryRepository.save(inventory);
					}
				} else {
					return false;
				}

			} while (i < total_page);

		} catch (IOException e) {
			// TODO Auto-generated catch block

			logger.info(e.getMessage());
			return false;
		}

		return true;
	}

	@Transactional
	@RequestMapping(value = "/measurementunit")
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

				if (errorCode == appProperties.getError_code_success()) {
					total_page = Integer.parseInt((String) map.get("page_count"));
					tempList = (List<LinkedHashMap<String, String>>) map.get("unit");
					for (LinkedHashMap<String, String> temp : tempList) {
						logger.info(temp.get("code") + " " + temp.get("name"));
						MeasurementUnit unit = new MeasurementUnit();
						unit.setCode(temp.get("code"));
						unit.setGroupCode(temp.get("group_code"));
						unit.setName(temp.get("name"));
						// if (temp.get("changerate") != null && !temp.get("changerate").isEmpty())
						// unit.setChangerate(Integer.parseInt(temp.get("changerate")));
						unit.setMainFlag(Boolean.parseBoolean(temp.get("main_flag")));
						measurementUnitRepository.save(unit);
					}
				} else {
					return false;
				}

			} while (i < total_page);

		} catch (IOException e) {
			// TODO Auto-generated catch block

			logger.info(e.getMessage());
			return false;
		}

		return true;
	}

	@Transactional
	@RequestMapping(value = "/inventory_class")
	public boolean inventoryClass() {

		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, String> requestParams;
		int i = 0, total_page = 1;
		List<LinkedHashMap<String, String>> tempList;

		Map<String, Object> map = new HashMap<>();

		this.inventoryClassRepository.deleteAll();

		try {
			do {

				map = new HashMap<>();

				requestParams = new HashMap<>();

				requestParams.put("rows_per_page", "10");
				requestParams.put("page_index", Integer.toString(++i));

				String response = apiClient.getBatchInventoryClass(requestParams);
				map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
				});

				int errorCode = Integer.parseInt((String) map.get("errcode"));

				if (errorCode == appProperties.getError_code_success()) {
					total_page = Integer.parseInt((String) map.get("page_count"));
					tempList = (List<LinkedHashMap<String, String>>) map.get("inventory");
					for (LinkedHashMap<String, String> temp : tempList) {
						logger.info(temp.get("code") + " " + temp.get("name"));
						InventoryClass inventoryClass = new InventoryClass();
						inventoryClass.setCode(temp.get("code"));
						inventoryClass.setName(temp.get("name"));
						inventoryClass.setEndRankFlag(Boolean.parseBoolean(temp.get("end_rank_flag")));
						inventoryClass.setRank(Integer.parseInt(temp.get("rank")));
						inventoryClassRepository.save(inventoryClass);
					}
				} else {
					return false;
				}

			} while (i < total_page);

		} catch (IOException e) {
			// TODO Auto-generated catch block

			logger.info(e.getMessage());
			return false;
		}

		return true;
	}

	@RequestMapping(value = { "/purchaseorder/init", "/purchaseorder", "/purchaseorder/" })
	public boolean purchaseorderInit() {
		purchaseOrder(null, null);
		return true;
	}

	@RequestMapping(value = "/purchaseorder/daily")
	public boolean purchaseorderPart() {
		purchaseOrder(new Date(), null);
		return true;
	}

	@RequestMapping(value = "/purchaseorder/vendor")
	public boolean purchaseorderVendor() {
		if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
			return false;

		String units = (String) httpSession.getAttribute(Constants.KEY_DEFAULT_UNIT_LIST);
		purchaseOrder(new Date(), units);
		return true;
	}

	@Transactional
	private boolean purchaseOrder(Date startDate, String unitList) {

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

		List<Vendor> vendorCodeList = new ArrayList<>();

		if (unitList != null) {
			vendorCodeList
					.addAll(vendorRepository.findVendorsByUnitIdList(Arrays.asList(StringUtils.split(unitList, ","))));
		}

		try {
			do {

				map = new HashMap<>();

				requestParams = new HashMap<>();

				requestParams.put("rows_per_page", "10");
				requestParams.put("page_index", Integer.toString(++i));

				if (startDate != null) {
					requestParams.put("date_begin", Utils.formatDate(startDate));
				}

				String response = apiClient.getBatchPurchaseOrder(requestParams);
				logger.info(response);
				map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
				});

				logger.info((String) map.get("errcode"));
				int errorCode = Integer.parseInt((String) map.get("errcode"));

				if (errorCode == appProperties.getError_code_success()) {
					total_page = Integer.parseInt((String) map.get("page_count"));
					list = (List<LinkedHashMap<String, String>>) map.get("purchaseorderlist");
					for (LinkedHashMap<String, String> temp : list) {
						logger.info(temp.get("code") + " " + temp.get("name"));

						PurchaseOrderMain main = new PurchaseOrderMain();
						main.setCode(temp.get("code"));

						Date makeDate = Utils.parseDate(temp.get("date"));
						Vendor vendor = vendorRepository.findOneByCode(temp.get("vendorcode"));

						if (makeDate == null || temp.get("state") == "新建" || vendor == null) {
							continue;
						}

						Example<PurchaseOrderMain> example = Example.of(main);
						Optional<PurchaseOrderMain> result = purchaseOrderMainRepository.findOne(example);
						if (result.isPresent())
							main = result.get();
						main.setSrmstate(Constants.PURCHASE_ORDER_STATE_START);
						main.setVendor(vendorRepository.findOneByCode(temp.get("vendorcode")));
						main.setMakedate(makeDate);
						main.setState(temp.get("state"));
						if (temp.get("money") != null && !temp.get("money").isEmpty())
							main.setMoney(Float.parseFloat(temp.get("money")));
						if (temp.get("sum") != null && !temp.get("sum").isEmpty())
							main.setSum(Float.parseFloat(temp.get("sum")));
						main.setPurchaseTypeName(temp.get("purchase_type_name"));
						main.setRemark(temp.get("remark"));
						main.setMaker(temp.get("maker"));
						main.setVerifier(temp.get("verifier"));
						main.setCloser(temp.get("closer"));
						main.setDeptcode(temp.get("deptcode"));
						main.setDeptname(temp.get("deptname"));
						main.setPersoncode(temp.get("personcode"));
						main.setPersonname(temp.get("personname"));

						purchaseOrderMainRepository.save(main);

						purchaseOrderDetail(main);
					}
				} else {
					return false;
				}

			} while (i < total_page);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
			return false;
		}

		return true;
	}

	private boolean purchaseOrderDetail(PurchaseOrderMain main) {

		ObjectMapper objectMapper = new ObjectMapper();

		List<LinkedHashMap<String, String>> entryList;

		Map<String, Object> map = new HashMap<>();

		try {

			map = new HashMap<>();

			String response = apiClient.getPurchaseOrder(main.getCode());
			logger.info(response);
			map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
			});

			logger.info((String) map.get("errcode"));
			int errorCode = Integer.parseInt((String) map.get("errcode"));

			if (errorCode == appProperties.getError_code_success()) {
				LinkedHashMap<String, Object> order = (LinkedHashMap<String, Object>) map.get("purchaseorder");
				entryList = (List<LinkedHashMap<String, String>>) order.get("entry");

				for (LinkedHashMap<String, String> entryMap : entryList) {
					PurchaseOrderDetail detail = new PurchaseOrderDetail();
					detail.setMain(purchaseOrderMainRepository.findOneByCode(main.getCode()));
					detail.setInventory(inventoryRepository.findByCode(entryMap.get("inventorycode")));
					if (entryMap.get("quantity") != null && !entryMap.get("quantity").isEmpty())
						detail.setQuantity(Float.parseFloat(entryMap.get("quantity")));
					if (entryMap.get("price") != null && !entryMap.get("price").isEmpty())
						detail.setPrice(Float.parseFloat(entryMap.get("price")));
					if (entryMap.get("tax") != null && !entryMap.get("tax").isEmpty())
						detail.setTaxprice(Float.parseFloat(entryMap.get("tax")));
					if (entryMap.get("sum") != null && !entryMap.get("sum").isEmpty())
						detail.setSum(Float.parseFloat(entryMap.get("sum")));
					if (entryMap.get("money") != null && !entryMap.get("money").isEmpty())
						detail.setMoney(Float.parseFloat(entryMap.get("money")));
					if (entryMap.get("tax") != null && !entryMap.get("tax").isEmpty())
						detail.setTax(Float.parseFloat(entryMap.get("tax")));
					if (entryMap.get("rowno") != null && !entryMap.get("rowno").isEmpty())
						detail.setRowno(Integer.parseInt(entryMap.get("rowno")));

					String arriveDateStr = entryMap.get("arrivedate");
					detail.setArrivedate(Utils.parseDate(arriveDateStr));

					purchaseOrderDetailRepository.save(detail);
				}

			} else {
				return false;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
			return false;
		}

		return true;
	}

	@RequestMapping(value = { "/purchasein/init", "/purchasein", "/purchasein/" })
	public boolean purchaseInInit() {
		purchaseIn(null, null, false);
		purchaseIn(null, null, true);
		return true;
	}

	@RequestMapping(value = "/purchasein/daily")
	public boolean purchaseInPart() {
		purchaseIn(new Date(), null, false);
		purchaseIn(new Date(), null, true);
		return true;
	}

	@RequestMapping(value = "/purchasein/vendor/{vendorCode}")
	public boolean purchaseInVendor(@PathVariable("vendorCode") String vendorCode) {
		purchaseIn(new Date(), vendorCode, false);
		purchaseIn(new Date(), vendorCode, true);
		return true;
	}

	@Transactional
	public boolean purchaseIn(Date startDate, String vendorCode, boolean isWeiwai) {

		boolean initTable = false;
		if (startDate == null) {
			initTable = true;
		}

		if (initTable) {
			// this.purchaseInDetailRepository.deleteAllInBatch();
			// this.purchaseInMainRepository.deleteAllInBatch();
		}

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

				if (vendorCode != null) {
					requestParams.put("vendor_code", vendorCode);
				}

				if (startDate != null) {
					// requestParams.put("start_date", Utils.formatDateZeroTime(startDate));
				}

				String response;
				if (isWeiwai) {
					response = apiClient.getLinkU8BatchWeiwai(requestParams);
				} else {
					response = apiClient.getLinkU8BatchBasic(requestParams);
				}

				map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
				});

				int errorCode = Integer.parseInt((String) map.get("errcode"));

				if (errorCode == appProperties.getError_code_success()) {
					total_page = Integer.parseInt(String.valueOf(map.get("page_count")));
					tempList = (List<LinkedHashMap<String, String>>) map.get("list");
					for (LinkedHashMap<String, String> temp : tempList) {
						String code = temp.get("CCode");
						String type = temp.get("CBusType");
						String vendor_code = temp.get("CVenCode");
						String bredvouch = temp.get("bredvouch");
						String warehouse_code = temp.get("CWhCode");
						String warehouse_name = temp.get("CWhName");
						String memo = temp.get("CMemo");
						String date = temp.get("DVeriDate");
						String rowno = String.valueOf(temp.get("irowno"));

						logger.info(code + " " + rowno);
						String inventory_code = temp.get("CInvCode");
						Float quantity = Float.valueOf(String.valueOf(temp.get("IQuantity")));
						Float price = Float.valueOf(String.valueOf(temp.get("IUnitCost")));
						Float cost = Float.valueOf(String.valueOf(temp.get("IPrice")));
						Float tax = Float.valueOf(String.valueOf(temp.get("ITaxPrice")));
						Float tax_price = Float.valueOf(String.valueOf(temp.get("ITaxUnitCost")));
						Float tax_rate = Float.valueOf(String.valueOf(temp.get("ITaxRate")));
						Float tax_cost = Float.valueOf(String.valueOf(temp.get("ISum")));
						String detailMemo = temp.get("cbMemo");
						Float nat_price = Float.valueOf(String.valueOf(temp.get("INatUnitPrice")));
						Float nat_cost = Float.valueOf(String.valueOf(temp.get("INatMoney")));
						Float nat_tax_rate = Float.valueOf(String.valueOf(temp.get("IPerTaxRate")));
						Float nat_tax = Float.valueOf(String.valueOf(temp.get("INatTax")));
						Float nat_tax_price = Float.valueOf(String.valueOf(temp.get("INatTaxUnitCost")));

						Float nat_tax_cost = Float.valueOf(String.valueOf(temp.get("INatSum")));
						String material_code = temp.get("CInvCodeMat");
						String material_name = temp.get("CInvNameMat");
						String material_unitname = temp.get("ccomunitnameMat");

						Float material_quantity = null;
						if (temp.get("IQuantityMat") != null)
							material_quantity = Float.valueOf(String.valueOf(temp.get("IQuantityMat")));

						Float material_price = null;
						if (temp.get("INatUnitPriceMat") != null)
							material_price = Float.valueOf(String.valueOf(temp.get("INatUnitPriceMat")));

						Float material_tax_price = null;
						if (temp.get("INatTaxUnitPriceMat") != null)
							material_tax_price = Float.valueOf(String.valueOf(temp.get("INatTaxUnitPriceMat")));

						PurchaseInMain main = purchaseInMainRepository.findOneByCode(code);
						if (main == null) {
							main = new PurchaseInMain();
							main.setCode(code);
							if (bredvouch != null && !bredvouch.isEmpty())
								main.setBredvouch(Integer.parseInt(bredvouch));
							main.setWarehouse_code(warehouse_code);
							main.setWarehouse_name(warehouse_name);
							main.setMemo(memo);
							main.setDate(Utils.parseDateTime(date));

							main.setVendor(vendorRepository.findOneByCode(vendor_code));
							main.setType(type);

							main = purchaseInMainRepository.save(main);
						}

						PurchaseInDetail detail = purchaseInDetailRepository.findOneByCodeAndRowno(code,
								Integer.parseInt(rowno));
						if (detail != null) {
							continue;
						}
						detail = new PurchaseInDetail();
						detail.setMain(main);
						detail.setInventory(inventoryRepository.findByCode(inventory_code));

						detail.setQuantity(quantity);

						detail.setPrice(price);
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
				} else {
					return false;
				}

			} while (i < total_page);

		} catch (IOException e) {
			// TODO Auto-generated catch block

			logger.info(e.getMessage());
			return false;
		}

		return true;
	}

}
