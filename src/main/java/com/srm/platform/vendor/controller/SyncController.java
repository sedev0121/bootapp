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
import com.srm.platform.vendor.model.PurchaseOrderDetail;
import com.srm.platform.vendor.model.PurchaseOrderMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.model.VendorClass;
import com.srm.platform.vendor.repository.BoxClassRepository;
import com.srm.platform.vendor.repository.CompanyRepository;
import com.srm.platform.vendor.repository.InventoryClassRepository;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.PurchaseOrderDetailRepository;
import com.srm.platform.vendor.repository.PurchaseOrderMainRepository;
import com.srm.platform.vendor.repository.VendorClassRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.u8api.RestApiClient;
import com.srm.platform.vendor.u8api.RestApiResponse;
import com.srm.platform.vendor.utility.Constants;
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

	@Autowired
	private BoxClassRepository boxClassRepository;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private PurchaseOrderMainRepository purchaseOrderMainRepository;

	@Autowired
	private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

	@ResponseBody
	@GetMapping({ "", "/" })
	public boolean index() {
		logger.info("=============== Sync Start ==============");
		this.inventoryClass();
		this.vendorClass();
		this.inventory();
		this.vendor();
		this.order();
		logger.info("=============== Sync End ==============");
		return true;
	}

	private String getStringValue(LinkedHashMap<String, Object> object, String key) {
		String temp = String.valueOf(object.get(key));
		if (!Utils.isEmpty(temp)) {
			return String.valueOf(object.get(key));
		}
		return null;
	}

	private Integer getIntegerValue(LinkedHashMap<String, Object> object, String key) {
		String temp = String.valueOf(object.get(key));
		if (!Utils.isEmpty(temp)) {
			return Integer.valueOf(temp);
		}
		return null;
	}

	private Double getDoubleValue(LinkedHashMap<String, Object> object, String key) {
		String temp = String.valueOf(object.get(key));
		if (!Utils.isEmpty(temp)) {
			return Double.valueOf(temp);
		}
		return null;
	}

	private List<LinkedHashMap<String, Object>> getDetailMap(LinkedHashMap<String, Object> object, String key) {
		return (List<LinkedHashMap<String, Object>>) object.get(key);
	}

	@Transactional
	@ResponseBody
	@RequestMapping(value = { "/inventory_class", "/inventory_class/" })
	public int inventoryClass() {
		int totalCount = 0;
		boolean hasMore = true;
		while (hasMore) {
			List<String> codes = new ArrayList<String>();

			RestApiResponse response = apiClient.postForInventoryClass();

			if (response.isSuccess()) {
				for (LinkedHashMap<String, Object> temp : response.getData()) {
					InventoryClass inventoryClass = inventoryClassRepository
							.findOneByCode(getStringValue(temp, "code"));
					if (inventoryClass == null) {
						inventoryClass = new InventoryClass();
						inventoryClass.setCode(getStringValue(temp, "code"));
					}

					inventoryClass.setName(getStringValue(temp, "name"));
					inventoryClass.setParentCode(getStringValue(temp, "upcode"));
					inventoryClass.setEndRankFlag(getIntegerValue(temp, "end_rank_flag"));
					inventoryClass.setRank(getIntegerValue(temp, "rank"));

					inventoryClassRepository.save(inventoryClass);

					codes.add(getStringValue(temp, "code"));
					totalCount++;

				}

				if (codes.size() > 0) {
					response = apiClient.postConfirmForInventoryClass(codes);
				} else {
					hasMore = false;
				}
			} else {
				hasMore = false;
			}
		}

		return totalCount;
	}

	@Transactional
	@ResponseBody
	@RequestMapping(value = { "/inventory", "/inventory/" })
	public int inventory() {
		int totalCount = 0;
		boolean hasMore = true;
		while (hasMore) {
			List<String> codes = new ArrayList<String>();

			RestApiResponse response = apiClient.postForInventory();

			if (response.isSuccess()) {
				for (LinkedHashMap<String, Object> temp : response.getData()) {
					Inventory inventory = inventoryRepository.findOneByCode(getStringValue(temp, "code"));
					if (inventory == null) {
						inventory = new Inventory();
						inventory.setBoxClass(boxClassRepository.findOneById(1L));
					}

					inventory.setCode(getStringValue(temp, "code"));
					inventory.setName(getStringValue(temp, "name"));
					inventory.setInventoryClass(
							inventoryClassRepository.findOneByCode(getStringValue(temp, "sort_code")));
					inventory.setSpecs(getStringValue(temp, "specs"));
					inventory.setMainMeasure(getStringValue(temp, "main_measure"));
					inventory.setDefwarehouse(getStringValue(temp, "defwarehouse"));
					inventory.setDefwarehousename(getStringValue(temp, "defwarehousename"));
					inventory.setIimptaxrate(getDoubleValue(temp, "iimptaxrate"));
					
					inventory.setIsAsset(getIntegerValue(temp, "bInvAsset"));
					inventory.setIsImport(getIntegerValue(temp, "bImport"));
					inventory.setIsPurchase(getIntegerValue(temp, "bPurchase"));
					inventory.setIsWeiwai(getIntegerValue(temp, "bProxyForeign"));
					

					inventory.setStartDate(Utils.parseDateTime(getStringValue(temp, "start_date")));
					inventory.setEndDate(Utils.parseDateTime(getStringValue(temp, "end_date")));
					inventory.setModifyDate(Utils.parseDateTime(getStringValue(temp, "modify_date")));

					inventoryRepository.save(inventory);

					codes.add(getStringValue(temp, "code"));
					totalCount++;
				}

				if (codes.size() > 0) {
					response = apiClient.postConfirmForInventory(codes);
				} else {
					hasMore = false;
				}
			} else {
				hasMore = false;
			}
		}

		return totalCount;
	}

	@Transactional
	@ResponseBody
	@RequestMapping(value = { "/vendor_class", "/vendor_class/" })
	public int vendorClass() {
		int totalCount = 0;
		boolean hasMore = true;
		while (hasMore) {
			List<String> codes = new ArrayList<String>();

			RestApiResponse response = apiClient.postForVendorClass();

			if (response.isSuccess()) {
				for (LinkedHashMap<String, Object> temp : response.getData()) {
					VendorClass vendorClass = vendorClassRepository.findOneByCode(getStringValue(temp, "code"));
					if (vendorClass == null) {
						vendorClass = new VendorClass();
						vendorClass.setCode(getStringValue(temp, "code"));
					}

					vendorClass.setName(getStringValue(temp, "name"));
					vendorClass.setEndRankFlag(getIntegerValue(temp, "end_rank_flag"));
					vendorClass.setRank(getIntegerValue(temp, "rank"));

					vendorClassRepository.save(vendorClass);

					codes.add(getStringValue(temp, "code"));
					totalCount++;
				}

				if (codes.size() > 0) {
					response = apiClient.postConfirmForVendorClass(codes);
				} else {
					hasMore = false;
				}
			} else {
				hasMore = false;
			}
		}

		return totalCount;
	}

	@Transactional
	@ResponseBody
	@RequestMapping(value = { "/vendor", "/vendor/" })
	public int vendor() {
		int totalCount = 0;
		boolean hasMore = true;
		while (hasMore) {
			List<String> codes = new ArrayList<String>();

			RestApiResponse response = apiClient.postForVendor();

			if (response.isSuccess()) {
				for (LinkedHashMap<String, Object> temp : response.getData()) {
					Vendor vendor = vendorRepository.findOneByCode(getStringValue(temp, "code"));
					if (vendor == null) {
						vendor = new Vendor();
						vendor.setCode(getStringValue(temp, "code"));
					}

					vendor.setName(getStringValue(temp, "name"));
					vendor.setAbbrname(getStringValue(temp, "abbrname"));
					
					VendorClass vendorClass = vendorClassRepository.findOneByCode(getStringValue(temp, "sort_code"));
					vendor.setVendorClass(vendorClass);
					vendor.setIndustry(getStringValue(temp, "industry"));
					vendor.setAddress(getStringValue(temp, "address"));
					vendor.setBankOpen(getStringValue(temp, "bank_open"));
					vendor.setBankAccNumber(getStringValue(temp, "bank_acc_number"));
					vendor.setPhone(getStringValue(temp, "phone"));
					vendor.setFax(getStringValue(temp, "fax"));
					vendor.setEmail(getStringValue(temp, "email"));
					vendor.setContact(getStringValue(temp, "contact"));
					vendor.setMobile(getStringValue(temp, "mobile"));
					vendor.setReceiveSite(getStringValue(temp, "receive_site"));
					vendor.setEndDate(Utils.parseDateTime(getStringValue(temp, "end_date")));
					vendor.setMemo(getStringValue(temp, "memo"));

					vendorRepository.save(vendor);

					codes.add(getStringValue(temp, "code"));

					totalCount++;
				}

				if (codes.size() > 0) {
					response = apiClient.postConfirmForVendor(codes);
				} else {
					hasMore = false;
				}
			} else {
				hasMore = false;
			}
		}

		return totalCount;
	}

	@Transactional
	@ResponseBody
	@RequestMapping(value = { "/order", "/order/" })
	public int order() {
		int totalCount = 0;
		boolean hasMore = true;
		while (hasMore) {
			
			List<String> pocodes = new ArrayList<String>();
			List<String> mocodes = new ArrayList<String>();

			RestApiResponse response = apiClient.postForOrder();

			if (response.isSuccess()) {
				if (response.getData() != null && response.getData().size() == 0) {
					hasMore = false;
					break;
				}
				for (LinkedHashMap<String, Object> temp : response.getData()) {

					PurchaseOrderMain main = purchaseOrderMainRepository.findOneByCode(getStringValue(temp, "cPOID"));
					if (main == null) {
						main = new PurchaseOrderMain();
						main.setCode(getStringValue(temp, "cPOID"));
					}
					main.setPurchaseTypeName(getStringValue(temp, "cBusType"));
					main.setPoid(getStringValue(temp, "POID"));
					Vendor vendor = vendorRepository.findOneByCode(getStringValue(temp, "cVenCode"));

					// TODO:0=新建 1=审核 2=关闭
					int cState = getIntegerValue(temp, "cState");
					if (cState == 0 || vendor == null) {
						if (main.getPurchaseTypeName().equals("普通采购")) {
							pocodes.add(main.getPoid());
						} else {
							mocodes.add(main.getPoid());
						}
						totalCount++;
						continue;
					}					

					main.setOrderdate(Utils.parseDateTime(getStringValue(temp, "dPODate")));
					main.setVendor(vendor);

					main.setVerifier(getStringValue(temp, "cVerifier"));
					main.setAudittime(Utils.parseDateTime(getStringValue(temp, "cAuditTime")));

					main.setChangeverifier(getStringValue(temp, "cChangVerifier"));
					main.setChangeaudittime(Utils.parseDateTime(getStringValue(temp, "cChangAuditTime")));

					main.setMaker(getStringValue(temp, "cMaker"));
					main.setMakedate(Utils.parseDateTime(getStringValue(temp, "cmaketime")));

					main.setCloser(getStringValue(temp, "cCloser"));
					main.setClosedate(Utils.parseDateTime(getStringValue(temp, "dCloseTime")));

					main.setRemark(getStringValue(temp, "cMemo"));
					
					main.setDepartment(getStringValue(temp, "cDepName"));
					main.setPerson(getStringValue(temp, "cPersonName"));
					main.setCurrency(getStringValue(temp, "cexch_name"));
					
					main.setExchangeRate(this.getDoubleValue(temp, "nflat"));
					main.setCurrency(getStringValue(temp, "cexch_name"));

					String state = "";
					switch (cState) {
					case 1:
						state = "审核";
						break;
					case 2:
						state = "关闭";
						break;
					}
					
					main.setState(state);
					main.setSrmstate(Constants.PURCHASE_ORDER_STATE_START);

					purchaseOrderMainRepository.save(main);

//					purchaseOrderDetailRepository.deleteAll(purchaseOrderDetailRepository.findDetailsByCode(main.getCode()));
					
					List<LinkedHashMap<String, Object>> details = getDetailMap(temp, "details");
					if (details == null || details.size() == 0) {
						continue;
					}
					
					boolean addDetailSuccess = true;					
					
					String companyCode = null;
					Double taxRate = null;
					
					for (LinkedHashMap<String, Object> detailTemp : details) {
						String originalID = getStringValue(detailTemp, "ID");
						PurchaseOrderDetail detail = purchaseOrderDetailRepository.findOneByOrigianId(main.getCode(), originalID);

						if (detail == null) {
							detail = new PurchaseOrderDetail();
							detail.setMain(main);
						} else {
							logger.info("exist=" + originalID);
						}

						detail.setRowNo(getIntegerValue(detailTemp, "ivouchrowno"));
						detail.setUnitName(getStringValue(detailTemp, "cComUnitName"));
						
						String tempCompanyCode = getStringValue(detailTemp, "cFactoryCode"); //业务实体编码 
						
						if (companyCode == null) {
							companyCode = tempCompanyCode;
						}
						
						String inventoryCode = getStringValue(detailTemp, "cInvCode");
						Inventory inventory = inventoryRepository.findOneByCode(inventoryCode);
						
						if (inventory == null) {
							logger.info("inventory null=" + inventoryCode);
							addDetailSuccess = false;
							continue;
						}
						detail.setInventory(inventory);
						detail.setInventoryName(getStringValue(detailTemp, "cInvName"));
						detail.setInventoryClassCode(getStringValue(detailTemp, "cInvCCode"));
						detail.setInventoryClassName(getStringValue(detailTemp, "cInvCName"));
						detail.setQuantity(getDoubleValue(detailTemp, "iQuantity")); //数量

						String arriveDateStr = getStringValue(detailTemp, "dArriveDate"); //计划到货日期
						detail.setArriveDate(Utils.parseDateTime(arriveDateStr));

						detail.setTaxPrice(getDoubleValue(detailTemp, "iTaxNatPrice")); //含税单价
						detail.setSum(getDoubleValue(detailTemp, "iNatSum")); //含税金额
						
						Double tempTaxRate = getDoubleValue(detailTemp, "iPerTaxRate");
						detail.setTaxRate(tempTaxRate); //税率 
						
						if (taxRate == null) {
							taxRate = tempTaxRate;
						}
						
						detail.setOriginalId(originalID); //表体唯一id
						detail.setPrepayMoney(getDoubleValue(detailTemp, "Deposit")); //定金 
						
						detail.setPrice(getDoubleValue(detailTemp, "iNatUnitPrice")); //去税单价
						detail.setMoney(getDoubleValue(detailTemp, "iNatMoney")); //去税金额

						detail.setBackedQuantity(getDoubleValue(detailTemp, "fPoRetQuantity")); //退货数量 
						detail.setArrivedQuantity(getDoubleValue(detailTemp, "iArrQTY")); //累计到货数量 
						detail.setInvoicedQuantity(getDoubleValue(detailTemp, "iInvQTY")); //累计开票数量 
						detail.setInvoicedMoney(getDoubleValue(detailTemp, "iNatInvMoney")); //累计开票金额 
						
						detail.setCloserName(getStringValue(detailTemp, "cbCloser")); //行关闭人 
						
						String closeDateStr = getStringValue(detailTemp, "cbCloseTime"); //行关闭时间  
						detail.setCloseDate(Utils.parseDateTime(closeDateStr));
						detail.setMemo(getStringValue(detailTemp, "cbMemo")); //表体备注 
						

						purchaseOrderDetailRepository.save(detail);
					}

					if (taxRate != null) {
						main.setTaxRate(taxRate);
					}
					
					if (companyCode != null) {
						main.setCompany(companyRepository.findOneByCode(companyCode));
					}
					
					purchaseOrderMainRepository.save(main);
					
					if (addDetailSuccess) {
						if (main.getPurchaseTypeName().equals("普通采购")) {
							pocodes.add(main.getPoid());
						} else {
							mocodes.add(main.getPoid());
						}
						totalCount++;
					}					
				}

				if (pocodes.size() > 0 || mocodes.size() > 0) {
					response = apiClient.postConfirmForOrder(pocodes, mocodes);
				} else {
					hasMore = false;
					break;
				}
			} else {
				hasMore = false;
				break;
			}
		}

		return totalCount;
	}

}
