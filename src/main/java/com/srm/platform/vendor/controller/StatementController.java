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

					purchaseInDetail.setState(Constants.PURCHASE_IN_STATE_WAIT);
					purchaseInDetailRepository.save(purchaseInDetail);
				}
			}
			statementMainRepository.delete(main);
		}

		return true;
	}

	@GetMapping("/{code}/deleteattach")
	@PreAuthorize("hasAuthority('对账单管理-新建/发布')")
	public @ResponseBody Boolean deleteAttach(@PathVariable("code") String code, HttpServletRequest request) {
		StatementMain main = statementMainRepository.findOneByCode(code);
		String applicationPath = request.getServletContext().getRealPath("");
		File attach = new File(applicationPath + File.separator + main.getAttachFileName());
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

			bodyQuery += " and a.state>1";

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
			BindingResult bindingResult, HttpServletRequest request) {
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
				File file = UploadFileHelper.simpleUpload(attach, request, true, Constants.PATH_UPLOADS_STATEMENT);
				logger.info(attach.getOriginalFilename());
				if (file != null)
					savedFileName = file.getName();
			}

			if (savedFileName != null) {
				main.setAttachFileName(savedFileName);
				main.setAttachOriginalName(origianlFileName);
			}

		} else if (form.getState() == Constants.STATEMENT_STATE_CONFIRM
				|| (main.getState() == Constants.STATEMENT_STATE_SUBMIT
						&& form.getState() == Constants.STATEMENT_STATE_CANCEL)) {
			main.setConfirmer(this.getLoginAccount());
			main.setConfirmdate(new Date());
		} else if (form.getState() == Constants.STATEMENT_STATE_VERIFY
				|| (main.getState() == Constants.STATEMENT_STATE_CONFIRM
						&& form.getState() == Constants.STATEMENT_STATE_CANCEL)) {
			main.setVerifier(this.getLoginAccount());
			main.setVerifydate(new Date());
		} else if (form.getState() == Constants.STATEMENT_STATE_INVOICE_NUM) {
			main.setInvoicenummaker(this.getLoginAccount());
			main.setInvoicenumdate(new Date());
		} else if (form.getState() == Constants.STATEMENT_STATE_INVOICE_PUBLISH) {
			main.setInvoiceType(form.getInvoice_type());
			GenericJsonResponse<StatementMain> u8Response = this.u8invoice(main);
			if (u8Response.getSuccess() == GenericJsonResponse.SUCCESS) {
				main.setU8invoicemaker(this.getLoginAccount());
				main.setU8invoicedate(new Date());

			} else {
				return u8Response;
			}
		}

		String action = null;
		List<Account> toList = new ArrayList<>();
		switch (form.getState()) {
		case Constants.STATEMENT_STATE_SUBMIT:
			toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
			action = "发布";
			break;
		case Constants.STATEMENT_STATE_CONFIRM:
			toList.add(main.getMaker());
			action = "确认";
			break;
		case Constants.STATEMENT_STATE_VERIFY:
			toList.add(main.getMaker());
			toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
			action = "审核";
			break;
		case Constants.STATEMENT_STATE_CANCEL:
			toList.add(main.getMaker());
			toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
			action = "退回";
		case Constants.STATEMENT_STATE_INVOICE_NUM:
			toList.add(main.getMaker());
			action = "填发票号";
			break;
		case Constants.STATEMENT_STATE_INVOICE_PUBLISH:
			toList.add(main.getMaker());
			toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
			action = "生成U8发票";
		}
		String title = String.format("对账单【%s】已由【%s】%s，请及时查阅和处理！", main.getCode(), this.getLoginAccount().getRealname(),
				action);

		this.sendmessage(title, toList);

		if (this.isVendor() && form.getInvoice_code() != null && !form.getInvoice_code().isEmpty()) {
			main.setInvoiceCode(form.getInvoice_code());
		}

		main.setState(form.getState());
		main = statementMainRepository.save(main);

		GenericJsonResponse<StatementMain> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null,
				main);

		if (form.getState() <= Constants.STATEMENT_STATE_CONFIRM && form.getTable() != null) {
			statementDetailRepository.deleteInBatch(statementDetailRepository.findByCode(main.getCode()));

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
				String taxRate = row.get("tax_rate");

				String real_quantity = row.get("real_quantity");
				String yuanci = row.get("yuanci");
				String yinci = row.get("yinci");
				String unit_weight = row.get("unit_weight");
				String memo = row.get("memo");

				if (closedQuantity != null && !closedQuantity.isEmpty())
					detail.setClosedQuantity(Float.parseFloat(closedQuantity));

				if (closedPrice != null && !closedPrice.isEmpty())
					detail.setClosedPrice(Float.parseFloat(closedPrice));

				if (closedMoney != null && !closedMoney.isEmpty())
					detail.setClosedMoney(Float.parseFloat(closedMoney));

				if (closedTaxPrice != null && !closedTaxPrice.isEmpty())
					detail.setClosedTaxPrice(Float.parseFloat(closedTaxPrice));

				if (closedTaxMoney != null && !closedTaxMoney.isEmpty())
					detail.setClosedTaxMoney(Float.parseFloat(closedTaxMoney));

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

				detail = statementDetailRepository.save(detail);
			}

		}

		if (main.getState() <= Constants.STATEMENT_STATE_SUBMIT) {
			List<StatementDetail> detailList = statementDetailRepository.findByCode(main.getCode());
			for (StatementDetail detail : detailList) {
				PurchaseInDetail purchaseInDetail = purchaseInDetailRepository
						.findOneById(detail.getPurchaseInDetailId());

				purchaseInDetail.setState(Constants.PURCHASE_IN_STATE_START);
				purchaseInDetailRepository.save(purchaseInDetail);
			}
		}

		if (main.getState() == Constants.STATEMENT_STATE_VERIFY) {
			List<StatementDetail> detailList = statementDetailRepository.findByCode(main.getCode());
			for (StatementDetail detail : detailList) {
				PurchaseInDetail purchaseInDetail = purchaseInDetailRepository
						.findOneById(detail.getPurchaseInDetailId());

				purchaseInDetail.setState(Constants.PURCHASE_IN_STATE_FINISH);
				purchaseInDetailRepository.save(purchaseInDetail);
			}
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
			String response = apiClient.generatePurchaseInvoice(postJson, main.getCode());

			map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
			});

			int errorCode = Integer.parseInt((String) map.get("errcode"));
			String errmsg = String.valueOf(map.get("errmsg"));

			if (errorCode == appProperties.getError_code_success()) {
				String id = String.valueOf(map.get("id"));
				main.setU8invoiceid(id);
				statementMainRepository.save(main);
			} else {
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
		post.setInvoicecode(main.getInvoiceCode());
		post.setDelegatecode(main.getVendor().getCode());
		post.setVendorcode(main.getVendor().getCode());
		post.setDate(Utils.formatDate(main.getMakedate()));
		post.setInvoicetype(main.getInvoiceType() == 1 ? "01" : "02");
		post.setPurchasecode(main.getType() == 1 ? "01" : "05");
		post.setMaker(this.getLoginAccount().getRealname());

		List<U8InvoicePostEntry> entryList = new ArrayList<>();

		List<StatementDetail> detailList = statementDetailRepository.findByCode(main.getCode());
		for (StatementDetail detail : detailList) {
			PurchaseInDetail purchaseInDetail = purchaseInDetailRepository.findOneById(detail.getPurchaseInDetailId());

			if (purchaseInDetail == null)
				continue;
			U8InvoicePostEntry entry = new U8InvoicePostEntry();
			entry.setQuantity(detail.getClosedQuantity());
			entry.setTaxrate(detail.getTaxRate());
			entry.setOritaxcost(detail.getClosedTaxPrice());
			entry.setInventorycode(purchaseInDetail.getInventory().getCode());
			entryList.add(entry);
		}

		post.setEntry(entryList);

		try {
			Map<String, U8InvoicePostData> map = new HashMap<>();
			map.put("purchaseinvoice", post);
			jsonString = mapper.writeValueAsString(map);
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
}
