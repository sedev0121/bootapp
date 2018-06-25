package com.srm.platform.vendor.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.model.InventoryClass;
import com.srm.platform.vendor.model.MeasurementUnit;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.InventoryClassRepository;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.MeasurementUnitRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.u8api.ApiClient;
import com.srm.platform.vendor.u8api.AppProperties;

@RestController
@RequestMapping(path = "/sync")
public class SyncController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	@Autowired
	private ApiClient apiClient;

	@Autowired
	private AppProperties appProperties;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private MeasurementUnitRepository measurementUnitRepository;

	@Autowired
	private InventoryClassRepository inventoryClassRepository;

	@GetMapping({ "", "/", "/all" })
	public boolean index() {
		this.vendorUpdateAll();
		this.inventory();
		this.measurementunit();
		this.inventoryClass();

		return true;
	}

	@RequestMapping(value = "/vendor/part")
	public boolean vendorUpatePart() {
		return vendor(false);
	}

	@RequestMapping(value = "/vendor/all")
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
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:");
		try {
			result = dateFormat.parse(time).getTime();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return result;
	}

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
						String endDate = temp.get("end_date");
						if (endDate != null)
							vendor.setEndDate(Instant.ofEpochMilli(Long.parseLong(temp.get("end_date"))));

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

	@RequestMapping(value = "/inventory")
	public boolean inventory() {

		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, String> requestParams;
		int i = 0, total_page = 1;
		List<LinkedHashMap<String, String>> tempList;

		Map<String, Object> map = new HashMap<>();

		inventoryRepository.deleteAll();

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
						tempValue = temp.get("end_date");
						if (tempValue != null)
							try {
								inventory.setEndDate(formatter.parse(tempValue));
							} catch (ParseException e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							}
						tempValue = temp.get("iimptaxrate");
						if (tempValue != null)
							inventory.setIimptaxrate(Float.parseFloat(temp.get("iimptaxrate")));
						inventory.setInvaddcode(temp.get("invaddcode"));
						inventory.setiSupplyType(temp.get("iSupplyType"));
						inventory.setMainMeasure(temp.get("main_measure"));

						tempValue = temp.get("ModifyDate");
						if (tempValue != null)
							try {
								inventory.setModifyDate(formatter.parse(tempValue));
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

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

						tempValue = temp.get("start_date");
						if (tempValue != null && !tempValue.isEmpty())
							try {
								inventory.setStartDate(formatter.parse(tempValue));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
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
					tempList = (List<LinkedHashMap<String, String>>) map.get("inventory");
					for (LinkedHashMap<String, String> temp : tempList) {
						logger.info(temp.get("code") + " " + temp.get("name"));
						MeasurementUnit unit = new MeasurementUnit();
						unit.setCode(temp.get("code"));
						unit.setGroupCode(temp.get("group_code"));
						unit.setName(temp.get("name"));
						unit.setChangerate(Integer.parseInt(temp.get("changerate")));
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
}
