package com.srm.platform.vendor.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.model.InventoryClass;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.model.VendorClass;
import com.srm.platform.vendor.repository.InventoryClassRepository;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.VendorClassRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.u8api.RestApiClient;
import com.srm.platform.vendor.u8api.RestApiResponse;
import com.srm.platform.vendor.utility.Utils;


@RestController
@RequestMapping(path = "/sync")
public class SyncController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RestApiClient apiClient;
	
	@Autowired
	private InventoryClassRepository inventoryClassRepository;
	
	@Autowired
	private InventoryRepository inventoryRepository;
	
	@Autowired
	private VendorClassRepository vendorClassRepository;
	
	@Autowired
	private VendorRepository vendorRepository;
	
	@ResponseBody
	@GetMapping({ "", "/"})
	public boolean index() {
		this.inventoryClass();
		return true;
	}


	@Transactional
	@ResponseBody
	@RequestMapping(value = { "/inventory_class", "/inventory_class/" })
	public boolean inventoryClass() {

		List<String> codes = new ArrayList<String>();

		RestApiResponse response = apiClient.postForInventoryClass();
		
		if (response.isSuccess()) {
			for (LinkedHashMap<String, String> temp : response.getData()) {
				InventoryClass inventoryClass = inventoryClassRepository.findOneByCode(temp.get("code"));
				if (inventoryClass == null) {
					inventoryClass = new InventoryClass();
				}

				inventoryClass.setCode(temp.get("code"));
				inventoryClass.setName(temp.get("name"));
				String value = temp.get("end_rank_flag");
				if (value != null) {
					inventoryClass.setEndRankFlag(Integer.valueOf(value));
				}
				
				value = temp.get("rank");
				if (value != null) {
					inventoryClass.setRank(Integer.parseInt(value));
				}
					
				inventoryClassRepository.save(inventoryClass);
				
				codes.add(temp.get("code"));
			}
		}
		
		if (codes.size() > 0) {
			response = apiClient.postConfirmForInventoryClass(codes);	
		}		
		
		return true;
	}
	
	@Transactional
	@ResponseBody
	@RequestMapping(value = { "/inventory", "/inventory/" })
	public boolean inventory() {

		List<String> codes = new ArrayList<String>();

		RestApiResponse response = apiClient.postForInventory();
		
		if (response.isSuccess()) {
			for (LinkedHashMap<String, String> temp : response.getData()) {
				Inventory inventory = inventoryRepository.findOneByCode(temp.get("code"));
				if (inventory == null) {
					inventory = new Inventory();
				}

				String tempValue;
				inventory.setCode(temp.get("code"));
				inventory.setName(temp.get("name"));
				inventory.setInventoryClass(inventoryClassRepository.findOneByCode(temp.get("sort_code")));
				inventory.setSpecs(temp.get("specs"));
				inventory.setMainMeasure(temp.get("main_measure"));
				inventory.setDefwarehouse(temp.get("defwarehouse"));
				inventory.setDefwarehousename(temp.get("defwarehousename"));
				tempValue = temp.get("iimptaxrate");
				if (tempValue != null)
					inventory.setIimptaxrate(Float.parseFloat(temp.get("iimptaxrate")));
				
				inventory.setStartDate(Utils.parseDateTime(temp.get("start_date")));
				inventory.setEndDate(Utils.parseDateTime(temp.get("end_date")));
				inventory.setModifyDate(Utils.parseDateTime(temp.get("ModifyDate")));


				inventoryRepository.save(inventory);
				
				codes.add(temp.get("code"));
			}
		}
		
		if (codes.size() > 0) {
			response = apiClient.postConfirmForInventory(codes);	
		}		
		
		return true;
	}
	
	@Transactional
	@ResponseBody
	@RequestMapping(value = { "/vendor_class", "/vendor_class/" })
	public boolean vendorClass() {

		List<String> codes = new ArrayList<String>();

		RestApiResponse response = apiClient.postForVendorClass();
		
		if (response.isSuccess()) {
			for (LinkedHashMap<String, String> temp : response.getData()) {
				VendorClass vendorClass = vendorClassRepository.findOneByCode(temp.get("code"));
				if (vendorClass == null) {
					vendorClass = new VendorClass();
				}

				vendorClass.setCode(temp.get("code"));
				vendorClass.setName(temp.get("name"));
				String value = temp.get("end_rank_flag");
				if (value != null) {
					vendorClass.setEndRankFlag(Integer.valueOf(value));
				}
				
				value = temp.get("rank");
				if (value != null) {
					vendorClass.setRank(Integer.parseInt(value));
				}


				vendorClassRepository.save(vendorClass);
				
				codes.add(temp.get("code"));
			}
		}
		
		if (codes.size() > 0) {
			response = apiClient.postConfirmForVendorClass(codes);	
		}		
		
		return true;
	}
	
	
	@Transactional
	@ResponseBody
	@RequestMapping(value = { "/vendor", "/vendor/" })
	public boolean vendor() {

		List<String> codes = new ArrayList<String>();

		RestApiResponse response = apiClient.postForVendor();
		
		if (response.isSuccess()) {
			for (LinkedHashMap<String, String> temp : response.getData()) {
				Vendor vendor = vendorRepository.findOneByCode(temp.get("code"));
				if (vendor == null) {
					vendor = new Vendor();
					vendor.setCode(temp.get("code"));
				}

				vendor.setName(temp.get("name"));
				vendor.setAbbrname(temp.get("abbrname"));
				vendor.setSortCode(temp.get("sort_code"));
				vendor.setIndustry(temp.get("industry"));
				vendor.setAddress(temp.get("address"));
				vendor.setBankOpen(temp.get("bank_open"));
				vendor.setBankAccNumber(temp.get("bank_acc_number"));
				vendor.setPhone(temp.get("phone"));
				vendor.setFax(temp.get("fax"));
				vendor.setEmail(temp.get("email"));
				vendor.setContact(temp.get("contact"));
				vendor.setMobile(temp.get("mobile"));
				vendor.setReceiveSite(temp.get("receive_site"));
				vendor.setEndDate(Utils.parseDateTime(temp.get("end_date")));
				vendor.setMemo(temp.get("memo"));
				
				vendorRepository.save(vendor);
				
				codes.add(temp.get("code"));
			}
		}
		
		if (codes.size() > 0) {
			response = apiClient.postConfirmForVendor(codes);	
		}		
		
		return true;
	}

}
