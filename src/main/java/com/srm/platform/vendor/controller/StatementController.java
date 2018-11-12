package com.srm.platform.vendor.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.PurchaseInDetail;
import com.srm.platform.vendor.model.StatementDetail;
import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.model.VenPriceAdjustMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.PurchaseInDetailRepository;
import com.srm.platform.vendor.repository.StatementDetailRepository;
import com.srm.platform.vendor.repository.StatementMainRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.StatementDetailItem;
import com.srm.platform.vendor.utility.StatementSaveForm;
import com.srm.platform.vendor.utility.StatementSearchResult;
import com.srm.platform.vendor.utility.U8InvoicePostData;
import com.srm.platform.vendor.utility.U8InvoicePostEntry;
import com.srm.platform.vendor.utility.UploadFileHelper;
import com.srm.platform.vendor.utility.Utils;
import com.srm.platform.vendor.utility.VenPriceDetailItem;

@Controller
@RequestMapping(path = "/statement")
@PreAuthorize("hasRole('ROLE_VENDOR') or hasAuthority('对账单管理-查看列表')")
public class StatementController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private StatementMainRepository statementMainRepository;

	@Autowired
	private StatementDetailRepository statementDetailRepository;

	@Autowired
	private PurchaseInDetailRepository purchaseInDetailRepository;

	// 查询列表
	@GetMapping({ "", "/" })
	public String index() {
		return "statement/index";
	}

	@GetMapping({ "/add" })
	@PreAuthorize("hasAuthority('对账单管理-新建/发布')")
	public String add(Model model) {
		StatementMain main = new StatementMain(accountRepository);
		model.addAttribute("main", main);
		return "statement/edit";
	}

	@GetMapping({ "/{code}/edit" })
	public String edit(@PathVariable("code") String code, Model model) {
		StatementMain main = this.statementMainRepository.findOneByCode(code);
		if (main == null)
			show404();

		model.addAttribute("main", main);
		return "statement/edit";
	}

	@GetMapping({ "/{code}/read/{msgid}" })
	public String read(@PathVariable("code") String code, @PathVariable("msgid") Long msgid, Model model) {
		setReadDate(msgid);
		return "redirect:/statement/" + code + "/edit";
	}

	@GetMapping("/{code}/delete")
	@PreAuthorize("hasAuthority('对账单管理-删除')")
	public @ResponseBody Boolean delete(@PathVariable("code") String code) {
		StatementMain main = statementMainRepository.findOneByCode(code);
		if (main != null) {
			List<StatementDetail> detailList = statementDetailRepository.findByCode(main.getCode());
			if (detailList != null) {
				for (StatementDetail detail : detailList) {
					PurchaseInDetail purchaseInDetail = purchaseInDetailRepository
							.findOneById(detail.getPurchaseInDetailId());

					if (purchaseInDetail == null)
						continue;

					purchaseInDetail.setState(Constants.PURCHASE_IN_STATE_WAIT);
					purchaseInDetailRepository.save(purchaseInDetail);
				}
			}
			statementMainRepository.delete(main);
			postUnLock(main);
		}

		return true;
	}

	@GetMapping("/{code}/deleteattach")
	@PreAuthorize("hasAuthority('对账单管理-新建/发布')")
	public @ResponseBody Boolean deleteAttach(@PathVariable("code") String code) {
		StatementMain main = statementMainRepository.findOneByCode(code);

		File attach = new File(UploadFileHelper.getUploadDir(Constants.PATH_UPLOADS_STATEMENT) + File.separator
				+ main.getAttachFileName());
		if (attach.exists())
			attach.delete();
		main.setAttachFileName(null);
		main.setAttachOriginalName(null);
		statementMainRepository.save(main);
		return true;
	}

	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<StatementSearchResult> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");
		String vendorStr = requestParams.getOrDefault("vendor", "");
		String stateStr = requestParams.getOrDefault("state", "0");
		String typeStr = requestParams.getOrDefault("type", "0");
		String code = requestParams.getOrDefault("code", "");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);

		Integer state = Integer.parseInt(stateStr);
		Integer type = Integer.parseInt(typeStr);
		Date startDate = Utils.parseDate(start_date);
		Date endDate = Utils.getNextDate(end_date);

		switch (order) {
		case "vendor_name":
			order = "b.name";
			break;
		case "vendor_code":
			order = "b.code";
			break;
		case "verifier":
			order = "d.realname";
			break;
		case "confirmer":
			order = "e.realname";
			break;
		case "invoicenummaker":
			order = "f.realname";
			break;
		case "u8invoicemaker":
			order = "g.realname";
			break;
		case "maker":
			order = "c.realname";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		String selectQuery = "select a.*, b.name vendor_name, c.realname maker, d.realname verifier, e.realname confirmer, f.realname invoicenummaker, g.realname u8invoicemaker ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "from statement_main a left join vendor b on a.vendor_code=b.code left join account c on a.maker_id=c.id "
				+ "left join account d on a.verifier_id=d.id left join account e on a.confirmer_id=e.id left join account f on a.invoicenummaker_id=f.id "
				+ "left join account g on a.u8invoicemaker_id=g.id where 1=1 ";

		List<String> unitList = this.getDefaultUnitList();
		Map<String, Object> params = new HashMap<>();

		if (isVendor()) {
			Vendor vendor = this.getLoginAccount().getVendor();
			vendorStr = vendor == null ? "0" : vendor.getCode();
			bodyQuery += " and b.code= :vendor";
			params.put("vendor", vendorStr);

			bodyQuery += " and a.state>=" + Constants.STATEMENT_STATE_REVIEW;

		} else {
			bodyQuery += " and b.unit_id in :unitList";
			params.put("unitList", unitList);
			if (!vendorStr.trim().isEmpty()) {
				bodyQuery += " and (b.name like CONCAT('%',:vendor, '%') or b.code like CONCAT('%',:vendor, '%')) ";
				params.put("vendor", vendorStr.trim());
			}
		}

		if (!code.trim().isEmpty()) {
			bodyQuery += " and a.code like CONCAT('%',:code, '%') ";
			params.put("code", code.trim());
		}

		if (state > 0) {
			bodyQuery += " and a.state=:state";
			params.put("state", state);
		}

		if (type > 0) {
			bodyQuery += " and a.type=:type";
			params.put("type", type);
		}

		if (startDate != null) {
			bodyQuery += " and a.makedate>=:startDate";
			params.put("startDate", startDate);
		}
		if (endDate != null) {
			bodyQuery += " and a.makedate<:endDate";
			params.put("endDate", endDate);
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "StatementSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<StatementSearchResult>(list, request, totalCount.longValue());

	}

	@RequestMapping(value = "/{code}/details", produces = "application/json")
	public @ResponseBody List<StatementDetailItem> details_ajax(@PathVariable("code") String code) {
		List<StatementDetailItem> list = statementDetailRepository.findDetailsByCode(code);

		return list;
	}

	@Transactional
	@PostMapping("/update")
	public @ResponseBody GenericJsonResponse<StatementMain> update_ajax(StatementSaveForm form,
			BindingResult bindingResult) {
		StatementMain main = statementMainRepository.findOneByCode(form.getCode());

		if (main == null) {
			main = new StatementMain();
			main.setCode(form.getCode());
		}

		if (form.getState() <= Constants.STATEMENT_STATE_SUBMIT) {

			main.setMakedate(new Date());
			main.setVendor(vendorRepository.findOneByCode(form.getVendor()));
			main.setMaker(accountRepository.findOneById(form.getMaker()));
			main.setRemark(form.getRemark());
			main.setType(form.getType());
			main.setTaxRate(form.getTax_rate());

			String origianlFileName = null;
			String savedFileName = null;
			MultipartFile attach = form.getAttach();
			if (attach != null) {
				origianlFileName = attach.getOriginalFilename();
				File file = UploadFileHelper.simpleUpload(attach, true, Constants.PATH_UPLOADS_STATEMENT);

				if (file != null)
					savedFileName = file.getName();
			}

			if (savedFileName != null) {
				main.setAttachFileName(savedFileName);
				main.setAttachOriginalName(origianlFileName);
			}
		} else if (form.getState() == Constants.STATEMENT_STATE_REVIEW
				|| (main.getState() == Constants.STATEMENT_STATE_SUBMIT
						&& form.getState() == Constants.STATEMENT_STATE_CANCEL)) {
			main.setVerifier(this.getLoginAccount());
			main.setVerifydate(new Date());

		} else if (form.getState() == Constants.STATEMENT_STATE_CONFIRM) {
			main.setInvoicenummaker(this.getLoginAccount());
			main.setInvoicenumdate(new Date());
			main.setConfirmer(this.getLoginAccount());
			main.setConfirmdate(new Date());
		} else if (main.getState() == Constants.STATEMENT_STATE_REVIEW
				&& form.getState() == Constants.STATEMENT_STATE_CANCEL) {
			main.setConfirmer(this.getLoginAccount());
			main.setConfirmdate(new Date());
		} else if (form.getState() == Constants.STATEMENT_STATE_INVOICE_PUBLISH) {
			main.setInvoiceType(form.getInvoice_type());
			GenericJsonResponse<StatementMain> u8Response = this.u8invoice(main);
			if (u8Response.getSuccess() == GenericJsonResponse.SUCCESS) {
				main.setU8invoicemaker(this.getLoginAccount());
				main.setU8invoicedate(new Date());
				main.setInvoiceCancelDate(null);
				main.setInvoiceCancelReason(null);

			} else {
				return u8Response;
			}
		}

		String action = null;
		List<Account> toList = new ArrayList<>();
		switch (form.getState()) {
		case Constants.STATEMENT_STATE_SUBMIT:
			List<String> idList = new ArrayList();
			idList.add(String.valueOf(main.getVendor().getUnit().getId()));
			toList.addAll(accountRepository.findAllBuyersByUnitIdList(idList));
			action = "提交";
			break;
		case Constants.STATEMENT_STATE_REVIEW:
			toList.add(main.getMaker());
			toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
			action = "审核发布";
			break;
		case Constants.STATEMENT_STATE_CANCEL:
			toList.add(main.getMaker());
			action = "退回";
			break;
		case Constants.STATEMENT_STATE_CONFIRM:
			toList.add(main.getMaker());
			action = "确认";
			break;
		case Constants.STATEMENT_STATE_INVOICE_PUBLISH:
			toList.add(main.getMaker());
			toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
			action = "生成U8发票";
			break;
		case Constants.STATEMENT_STATE_NEW:
			if (main.getState() == Constants.STATEMENT_STATE_INVOICE_CANCEL
					|| main.getState() == Constants.STATEMENT_STATE_CONFIRM) {
				toList.add(main.getMaker());
				toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
				action = "撤销";
			}
		}

		if (action != null) {
			String title = String.format("对账单【%s】已由【%s】%s，请及时查阅和处理！", main.getCode(),
					this.getLoginAccount().getRealname(), action);

			this.sendmessage(title, toList, String.format("/statement/%s/read", main.getCode()));
		}

		if (this.isVendor() && form.getInvoice_code() != null && !form.getInvoice_code().isEmpty()) {
			main.setInvoiceCode(form.getInvoice_code());
		}

		main.setState(form.getState());
		main = statementMainRepository.save(main);

		GenericJsonResponse<StatementMain> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null,
				main);

		if (form.getState() <= Constants.STATEMENT_STATE_SUBMIT) {

			postUnLock(main);
			List<StatementDetail> detailList = statementDetailRepository.findByCode(main.getCode());
			for (StatementDetail detail : detailList) {
				PurchaseInDetail purchaseInDetail = purchaseInDetailRepository
						.findOneById(detail.getPurchaseInDetailId());

				if (purchaseInDetail != null) {
					purchaseInDetail.setState(Constants.PURCHASE_IN_STATE_WAIT);
					purchaseInDetailRepository.save(purchaseInDetail);
				}
			}

			statementDetailRepository.deleteInBatch(statementDetailRepository.findByCode(main.getCode()));
			if (form.getTable() != null) {
				int i = 1;
				for (Map<String, String> row : form.getTable()) {
					StatementDetail detail = new StatementDetail();
					detail.setCode(main.getCode());
					detail.setPurchaseinType(Constants.STATEMENT_DETAIL_TYPE_BASIC);
					detail.setPurchaseInDetailId(Long.parseLong(row.get("purchase_in_detail_id")));

					String closedQuantity = row.get("closed_quantity");
					String closedPrice = row.get("closed_price");
					String closedMoney = row.get("closed_money");
					String closedTaxPrice = row.get("closed_tax_price");
					String closedTaxMoney = row.get("closed_tax_money");
					String taxRate = row.get("nat_tax_rate");

					String real_quantity = row.get("real_quantity");
					String yuanci = row.get("yuanci");
					String yinci = row.get("yinci");
					String unit_weight = row.get("unit_weight");
					String memo = row.get("memo");

					if (closedQuantity != null && !closedQuantity.isEmpty())
						detail.setClosedQuantity(Float.parseFloat(closedQuantity));

					if (closedPrice != null && !closedPrice.isEmpty())
						detail.setClosedPrice(Double.parseDouble(closedPrice));

					if (closedMoney != null && !closedMoney.isEmpty())
						detail.setClosedMoney(Double.parseDouble(closedMoney));

					if (closedTaxPrice != null && !closedTaxPrice.isEmpty())
						detail.setClosedTaxPrice(Double.parseDouble(closedTaxPrice));

					if (closedTaxMoney != null && !closedTaxMoney.isEmpty())
						detail.setClosedTaxMoney(Double.parseDouble(closedTaxMoney));

					if (taxRate != null && !taxRate.isEmpty())
						detail.setTaxRate(Float.parseFloat(taxRate));

					if (real_quantity != null && !real_quantity.isEmpty())
						detail.setRealQuantity(Float.parseFloat(real_quantity));

					if (yuanci != null && !yuanci.isEmpty())
						detail.setYuanci(Float.parseFloat(yuanci));

					if (yinci != null && !yinci.isEmpty())
						detail.setYinci(Float.parseFloat(yinci));

					if (unit_weight != null && !unit_weight.isEmpty())
						detail.setUnitWeight(Float.parseFloat(unit_weight));

					detail.setMemo(memo);
					detail.setRowNo(i++);

					detail = statementDetailRepository.save(detail);
				}
			}
		}

		if (main.getState() <= Constants.STATEMENT_STATE_SUBMIT) {
			List<StatementDetail> detailList = statementDetailRepository.findByCode(main.getCode());
			for (StatementDetail detail : detailList) {
				PurchaseInDetail purchaseInDetail = purchaseInDetailRepository
						.findOneById(detail.getPurchaseInDetailId());

				if (purchaseInDetail != null) {
					purchaseInDetail.setState(Constants.PURCHASE_IN_STATE_START);
					purchaseInDetailRepository.save(purchaseInDetail);
				}
			}
		} else if (main.getState() == Constants.STATEMENT_STATE_INVOICE_PUBLISH) {
			List<StatementDetail> detailList = statementDetailRepository.findByCode(main.getCode());
			for (StatementDetail detail : detailList) {
				PurchaseInDetail purchaseInDetail = purchaseInDetailRepository
						.findOneById(detail.getPurchaseInDetailId());

				if (purchaseInDetail != null) {
					purchaseInDetail.setState(Constants.PURCHASE_IN_STATE_FINISH);
					purchaseInDetailRepository.save(purchaseInDetail);
				}
			}
		}

		if (form.getState() <= Constants.STATEMENT_STATE_SUBMIT) {
			postLock(main);
		}

		return jsonResponse;
	}

	private GenericJsonResponse<StatementMain> u8invoice(StatementMain main) {

		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, Object> map = new HashMap<>();

		GenericJsonResponse<StatementMain> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null,
				main);
		try {

			map = new HashMap<>();

			String postJson = createJsonString(main);
			Map<String, String> getParams = new HashMap<>();

			String response = apiClient.generateLinkU8PurchaseInvoice(getParams, postJson);

			map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
			});

			int errorCode = Integer.parseInt((String) map.get("errcode"));
			String errmsg = String.valueOf(map.get("errmsg"));

			if (errorCode == 0) {
				jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, errorCode + ":" + errmsg, main);
			}

		} catch (IOException e) {
			logger.info(e.getMessage());
			jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, "服务器错误！", main);
		}

		return jsonResponse;
	}

	private String createJsonString(StatementMain main) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";

		U8InvoicePostData post = new U8InvoicePostData();
		post.setCpbvcode(main.getInvoiceCode());
		post.setCunitcode(main.getVendor().getCode());
		post.setCvencode(main.getVendor().getCode());
		post.setCpbvbilltype(main.getInvoiceType() == 1 ? "01" : "02");
		post.setCbustype(main.getType() == 1 ? "普通采购" : "委外加工");
		post.setCpbvmemo(main.getRemark());
		post.setCptcode(main.getType() == 1 ? "01" : "05");
		post.setCpbvmaker(this.getLoginAccount().getRealname());
		post.setIpbvtaxrate(main.getTaxRate());
		post.setIdiscountaxtype(main.getInvoiceType() == 1 ? "0" : "1");

		List<U8InvoicePostEntry> entryList = new ArrayList<>();

		List<StatementDetail> detailList = statementDetailRepository.findByCode(main.getCode());

		double chargeBack = 0D, closedMoneySum = 0D, closedTaxMoneySum = 0D;

		for (StatementDetail detail : detailList) {
			PurchaseInDetail purchaseInDetail = purchaseInDetailRepository.findOneById(detail.getPurchaseInDetailId());

			if (purchaseInDetail == null) {
				chargeBack = -detail.getClosedTaxMoney();
			} else {
				closedMoneySum += detail.getClosedMoney();
				closedTaxMoneySum += detail.getClosedTaxMoney();
			}
		}

		int i = 1, index = 0;
		double invoiceMoneySum = 0D, invoiceTaxMoneySum = 0D;

		for (StatementDetail detail : detailList) {
			PurchaseInDetail purchaseInDetail = purchaseInDetailRepository.findOneById(detail.getPurchaseInDetailId());

			index++;
			if (purchaseInDetail == null)
				continue;

			U8InvoicePostEntry entry = new U8InvoicePostEntry();

			entry.setCinvcode(purchaseInDetail.getInventory().getCode());
			entry.setIpbvquantity(detail.getClosedQuantity());

			double invoicePrice, invoiceMoney, invoiceTaxPrice, invoiceTaxMoney, itemChargeBack, itemTaxChargeBack;
			if (chargeBack > 0) {
				if (index == detailList.size()) {
					invoiceMoney = detail.getClosedMoney() - (chargeBack - invoiceMoneySum);
					invoiceTaxMoney = detail.getClosedTaxMoney() - (chargeBack - invoiceTaxMoneySum);
				} else {
					itemChargeBack = Utils.costRound(chargeBack * detail.getClosedMoney() / closedMoneySum);
					invoiceMoney = detail.getClosedMoney() - itemChargeBack;
					
					itemTaxChargeBack = Utils.costRound(chargeBack * detail.getClosedTaxMoney() / closedTaxMoneySum);
					invoiceTaxMoney = detail.getClosedTaxMoney() - itemTaxChargeBack;
					
					invoiceMoneySum += itemChargeBack;
					invoiceTaxMoneySum += itemTaxChargeBack;
				}
				invoicePrice = invoiceMoney / detail.getClosedQuantity();
				invoiceTaxPrice = invoiceTaxMoney / detail.getClosedQuantity();
			} else {
				invoicePrice = detail.getClosedPrice();
				invoiceMoney = detail.getClosedMoney();
				invoiceTaxPrice = detail.getClosedTaxPrice();
				invoiceTaxMoney = detail.getClosedTaxMoney();
			}

			invoicePrice = Utils.priceRound(invoicePrice);
			invoiceMoney = Utils.costRound(invoiceMoney);
			invoiceTaxPrice = Utils.priceRound(invoiceTaxPrice);
			invoiceTaxMoney = Utils.costRound(invoiceTaxMoney);

			// 发票含税单价
			entry.setiOriTaxCost(invoiceTaxPrice);
			// 发票未税单价
			entry.setiOriCost(invoicePrice);
			// 发票未税金额
			entry.setiOriMoney(invoiceMoney);
			// 发票含税金额-发票未税金额
			entry.setiOriTaxPrice(invoiceTaxMoney - invoiceMoney);
			// 发票含税金额
			entry.setiOriSum(invoiceTaxMoney);

			// 税率（表体）
			entry.setiTaxRate(detail.getTaxRate());

			// 采购入库子表ID
			entry.setRdsid(purchaseInDetail.getPiDetailId());
			// 采购订单子表ID
			entry.setIposid(purchaseInDetail.getPoDetailId());
			// SRM里表体的行号
			entry.setIvouchrowno(i);

			// 入库日期
			entry.setDindate(purchaseInDetail.getMain().getDate());

			// 发票含税单价
			entry.setInattaxprice(invoiceTaxPrice);
			// 发票未税单价
			entry.setiCost(invoicePrice);
			// 发票未税金额
			entry.setiMoney(invoiceMoney);
			// 发票含税金额-发票未税金额
			entry.setiTaxPrice(invoiceTaxMoney - invoiceMoney);
			// 发票未税金额
			entry.setiSum(invoiceMoney);

			entryList.add(entry);
			i++;
		}

		post.setList(entryList);

		try {
			// Map<String, U8InvoicePostData> map = new HashMap<>();
			// map.put("StrJson", post);
			//
			jsonString = mapper.writeValueAsString(post);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonString;
	}

	@GetMapping("/{code}/download")
	public ResponseEntity<Resource> download(@PathVariable("code") String code) {
		StatementMain main = this.statementMainRepository.findOneByCode(code);
		if (main == null)
			show404();

		return download(Constants.PATH_UPLOADS_STATEMENT + File.separator + main.getAttachFileName(),
				main.getAttachOriginalName());
	}

	private String createLockJsonString(StatementMain main) {
		String jsonString = "";
		List<StatementDetail> list = statementDetailRepository.findByCode(main.getCode());

		List<Map<String, String>> postData = new ArrayList();

		for (StatementDetail detail : list) {
			Map<String, String> map = new HashMap<>();
			PurchaseInDetail purchaseInDetail = purchaseInDetailRepository.findOneById(detail.getPurchaseInDetailId());

			if (purchaseInDetail == null)
				continue;

			map.put("autoid", String.valueOf(purchaseInDetail.getPiDetailId()));
			map.put("iquantity", String.valueOf(purchaseInDetail.getQuantity()));
			postData.add(map);
		}

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			jsonString = objectMapper.writeValueAsString(postData);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return jsonString;
	}

	private void postLock(StatementMain main) {
		String postJson = createLockJsonString(main);
		String response = apiClient.postLock(String.format("{%s}", postJson));
	}

	private void postUnLock(StatementMain main) {
		String postJson = createLockJsonString(main);
		String response = apiClient.postUnLock(String.format("{%s}", postJson));
	}
}
