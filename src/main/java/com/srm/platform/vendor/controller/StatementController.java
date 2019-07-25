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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.srm.platform.vendor.model.Master;
import com.srm.platform.vendor.model.PurchaseInDetail;
import com.srm.platform.vendor.model.StatementDetail;
import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.saveform.StatementSaveForm;
import com.srm.platform.vendor.searchitem.StatementDetailItem;
import com.srm.platform.vendor.searchitem.StatementSearchResult;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.U8InvoicePostData;
import com.srm.platform.vendor.utility.U8InvoicePostEntry;
import com.srm.platform.vendor.utility.UploadFileHelper;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/statement")
public class StatementController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	@Override
	protected String getOperationHistoryType() {
		return "statement";
	};
	
	// 查询列表
	@GetMapping({ "", "/" })
	@PreAuthorize("hasRole('ROLE_BUYER') and hasAuthority('对账单管理-查看列表') or hasRole('ROLE_VENDOR')")
	public String index() {
		return "statement/index";
	}

	@GetMapping({ "/add" })
	@PreAuthorize("hasAuthority('对账单管理-新建/提交')")
	public String add(Model model) {
		StatementMain main = new StatementMain();
		main.setMaker(this.getLoginAccount());		
		
		Master master = masterRepository.findOneByItemKey(Constants.KEY_AUTO_TASK_STATEMENT_DATE);
		if (master == null) {
			master = new Master();	
			master.setItemKey(Constants.KEY_AUTO_TASK_STATEMENT_DATE);
			master.setItemValue(Constants.DEFAULT_STATEMENT_DATE);
			masterRepository.save(master);
		}
		
		Date statementDate = Utils.getStatementDate(master.getItemValue());
		main.setDate(statementDate);
		
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
//	@PreAuthorize("hasAuthority('对账单管理-删除')")
	public @ResponseBody Boolean delete(@PathVariable("code") String code) {
		StatementMain main = statementMainRepository.findOneByCode(code);
		if (main != null) {
			List<StatementDetail> detailList = statementDetailRepository.findByCode(main.getCode());
			if (detailList != null) {
				for (StatementDetail detail : detailList) {
					PurchaseInDetail purchaseInDetail = purchaseInDetailRepository
							.findOneById(detail.getPiDetailId());

					if (purchaseInDetail == null)
						continue;

					purchaseInDetail.setState(Constants.PURCHASE_IN_STATE_WAIT);
					purchaseInDetailRepository.save(purchaseInDetail);
				}
			}
			statementDetailRepository.deleteAll(detailList);
			statementMainRepository.delete(main);
//			postUnLock(main);
		}

		return true;
	}

	@GetMapping("/{code}/deleteattach")
//	@PreAuthorize("hasAuthority('对账单管理-新建/发布')")
	public @ResponseBody Boolean deleteAttach(@PathVariable("code") String code) {
		StatementMain main = statementMainRepository.findOneByCode(code);

//		File attach = new File(UploadFileHelper.getUploadDir(Constants.PATH_UPLOADS_STATEMENT) + File.separator
//				+ main.getAttachFileName());
//		if (attach.exists())
//			attach.delete();
//		main.setAttachFileName(null);
//		main.setAttachOriginalName(null);
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
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		String selectQuery = "select a.*, b.name vendor_name, b.address vendor_address, com.name company_name ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "from statement_main a left join vendor b on a.vendor_code=b.code  "
				+ "left join company com on a.company_id=com.id where 1=1 ";

		Map<String, Object> params = new HashMap<>();

		if (isVendor()) {
			Vendor vendor = this.getLoginAccount().getVendor();
			vendorStr = vendor == null ? "0" : vendor.getCode();
			bodyQuery += " and b.code= :vendor";
			params.put("vendor", vendorStr);

			bodyQuery += " and a.state=" + Constants.STATEMENT_STATE_DEPLOY + " or a.state >=" + Constants.STATEMENT_STATE_CONFIRM;

		} else {
//			List<String> vendorList = this.getVendorListOfUser();
//			
//			if (vendorList.size() == 0) {
//				return new PageImpl<StatementSearchResult>(new ArrayList(), request, 0);
//			}
//			
//			bodyQuery += " and b.code in :vendorList";
//			params.put("vendorList", vendorList);
//			if (!vendorStr.trim().isEmpty()) {
//				bodyQuery += " and (b.name like CONCAT('%',:vendor, '%') or b.code like CONCAT('%',:vendor, '%')) ";
//				params.put("vendor", vendorStr.trim());
//			}
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
			bodyQuery += " and a.make_date>=:startDate";
			params.put("startDate", startDate);
		}
		if (endDate != null) {
			bodyQuery += " and a.make_date<:endDate";
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

		String action = null;
		List<Account> toList = new ArrayList<>();
		
		if (!form.isInvoiceAction()) {
			main.setState(form.getState());
			
			if (form.getState() <= Constants.STATEMENT_STATE_SUBMIT) {

				main.setMakeDate(new Date());
				main.setVendor(vendorRepository.findOneByCode(form.getVendor()));
				main.setMaker(this.getLoginAccount());
				main.setType(form.getType());
				main.setTaxRate(form.getTax_rate());
				main.setCompany(companyRepository.findOneById(form.getCompany()));
				main.setCostSum(form.getCostSum());
				main.setTaxCostSum(form.getTaxCostSum());
				main.setAdjustCostSum(form.getAdjustCostSum());
				main.setTaxSum(form.getTaxSum());

//				String origianlFileName = null;
//				String savedFileName = null;
//				MultipartFile attach = form.getAttach();
//				if (attach != null) {
//					origianlFileName = attach.getOriginalFilename();
//					File file = UploadFileHelper.simpleUpload(attach, true, Constants.PATH_UPLOADS_STATEMENT);
	//
//					if (file != null)
//						savedFileName = file.getName();
//				}
	//
//				if (savedFileName != null) {
//					main.setAttachFileName(savedFileName);
//					main.setAttachOriginalName(origianlFileName);
//				}
			} else if (form.getState() == Constants.STATEMENT_STATE_REVIEW) {
				main.setReviewer(this.getLoginAccount());
				main.setReviewDate(new Date());
			} else if (form.getState() == Constants.STATEMENT_STATE_DEPLOY) {
				main.setDeployer(this.getLoginAccount());
				main.setDeployDate(new Date());
			} else if (form.getState() == Constants.STATEMENT_STATE_CONFIRM) {
				main.setConfirmer(this.getLoginAccount());
				main.setConfirmDate(new Date());
			} else if (form.getState() == Constants.STATEMENT_STATE_CANCEL) {
				main.setCanceler(this.getLoginAccount());
				main.setCancelDate(new Date());
				main.setState(Constants.STATEMENT_STATE_NEW);
			}
			
			switch (form.getState()) {
			case Constants.STATEMENT_STATE_NEW:
				toList.add(main.getMaker());
				action = "保存";
				break;
			case Constants.STATEMENT_STATE_SUBMIT:
				toList.add(main.getMaker());
				action = "提交";
				break;
			case Constants.STATEMENT_STATE_REVIEW:
				toList.add(main.getMaker());
				action = "审核";
				break;
			case Constants.STATEMENT_STATE_DEPLOY:
				toList.add(main.getMaker());
				toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
				action = "发布";
				break;
			case Constants.STATEMENT_STATE_CANCEL:
				toList.add(main.getMaker());
				toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));
				action = "撤回";
				break;
			case Constants.STATEMENT_STATE_CONFIRM:
				toList.add(main.getMaker());
				action = "确认";
				break;
			case Constants.STATEMENT_STATE_DENY:
				toList.add(main.getMaker());
				action = "退回";
				break;
			}
			
		} else {
			if (form.getInvoice_state() == Constants.INVOICE_STATE_DONE) {
				main.setInvoiceType(form.getInvoice_type());
				main.setInvoiceCode(form.getInvoice_code());
				main.setInvoiceMaker(this.getLoginAccount());
				main.setInvoiceMakeDate(new Date());
			} else if (form.getInvoice_state() == Constants.INVOICE_STATE_CONFIRMED) {
				main.setInvoiceConfirmer(this.getLoginAccount());
				main.setInvoiceConfirmDate(new Date());
			} else if (form.getInvoice_state() == Constants.INVOICE_STATE_CANCELED) {
				main.setInvoiceCanceler(this.getLoginAccount());
				main.setInvoiceCancelDate(new Date());
			} else if (form.getInvoice_state() == Constants.INVOICE_STATE_UPLOAD_ERP) {
				main.setErpInvoiceMakeName(this.getLoginAccount().getRealname());
				main.setErpInvoiceMakeDate(new Date());
				
//				main.setInvoiceType(form.getInvoice_type());
//				GenericJsonResponse<StatementMain> u8Response = this.u8invoice(main);
//				if (u8Response.getSuccess() == GenericJsonResponse.SUCCESS) {
//					main.setErpInvoiceMakeName(this.getLoginAccount().getRealname());
//					main.setErpInvoiceMakeDate(new Date());
//				} else {
//					return u8Response;
//				}
			}
			
			switch (form.getInvoice_state()) {
			case Constants.INVOICE_STATE_DONE:
				toList.add(main.getMaker());
				action = "确认开票";
				break;
			case Constants.INVOICE_STATE_CONFIRMED:
				toList.add(main.getMaker());
				action = "审批通过";
				break;
			case Constants.INVOICE_STATE_CANCELED:
				toList.add(main.getMaker());
				action = "审批退回";
				break;
			case Constants.INVOICE_STATE_UPLOAD_ERP:
				toList.add(main.getMaker());
				action = "传递ERP";
				break;			
			}

			main.setInvoiceState(form.getInvoice_state());
			
		}
		

		if (action != null) {
			String title = String.format("对账单【%s】已由【%s】%s，请及时查阅和处理！", main.getCode(),
					this.getLoginAccount().getRealname(), action);

			this.sendmessage(title, toList, String.format("/statement/%s/read", main.getCode()));
		}
		
		this.addOpertionHistory(main.getCode(), action, form.getContent());
		
		main = statementMainRepository.save(main);

		GenericJsonResponse<StatementMain> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null,
				main);

		if (form.getState() <= Constants.STATEMENT_STATE_SUBMIT) {

//			postUnLock(main);
			List<StatementDetail> detailList = statementDetailRepository.findByCode(main.getCode());
			for (StatementDetail detail : detailList) {
				PurchaseInDetail purchaseInDetail = purchaseInDetailRepository
						.findOneById(detail.getPiDetailId());

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
					detail.setPiDetailId(Long.parseLong(row.get("pi_detail_id")));
					detail.setAdjustTaxCost(Double.parseDouble(row.get("adjust_tax_cost")));
					detail.setRowNo(i++);
					detail = statementDetailRepository.save(detail);
				}
			}
		}

		if (main.getState() <= Constants.STATEMENT_STATE_SUBMIT) {
			List<StatementDetail> detailList = statementDetailRepository.findByCode(main.getCode());
			for (StatementDetail detail : detailList) {
				PurchaseInDetail purchaseInDetail = purchaseInDetailRepository
						.findOneById(detail.getPiDetailId());

				if (purchaseInDetail != null) {
					purchaseInDetail.setState(Constants.PURCHASE_IN_STATE_START);
					purchaseInDetailRepository.save(purchaseInDetail);
				}
			}
//		} else if (main.getState() == Constants.STATEMENT_STATE_INVOICE_PUBLISH) {
//			List<StatementDetail> detailList = statementDetailRepository.findByCode(main.getCode());
//			for (StatementDetail detail : detailList) {
//				PurchaseInDetail purchaseInDetail = purchaseInDetailRepository
//						.findOneById(detail.getPiDetailId());
//
//				if (purchaseInDetail != null) {
//					purchaseInDetail.setState(Constants.PURCHASE_IN_STATE_FINISH);
//					purchaseInDetailRepository.save(purchaseInDetail);
//				}
//			}
		}

//		if (form.getState() <= Constants.STATEMENT_STATE_SUBMIT) {
//			postLock(main);
//		}

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
		post.setCptcode(main.getType() == 1 ? "01" : "05");
		post.setCpbvmaker(this.getLoginAccount().getRealname());
		post.setIpbvtaxrate(main.getTaxRate());
		post.setIdiscountaxtype(main.getInvoiceType() == 1 ? "0" : "1");

		List<U8InvoicePostEntry> entryList = new ArrayList<>();

		List<StatementDetail> detailList = statementDetailRepository.findByCode(main.getCode());

		double chargeBack = 0D, closedMoneySum = 0D, closedTaxMoneySum = 0D, closedQuantitySum = 0D;


		int i = 1, index = 0;
		double invoiceMoneySum = 0D, invoiceTaxMoneySum = 0D;

		for (StatementDetail detail : detailList) {
			PurchaseInDetail purchaseInDetail = purchaseInDetailRepository.findOneById(detail.getPiDetailId());

			index++;
			if (purchaseInDetail == null)
				continue;

			U8InvoicePostEntry entry = new U8InvoicePostEntry();

			

			entryList.add(entry);
			i++;
		}
		
		boolean is_bug = false;
		String bug_message = "";
		
		if ((closedQuantitySum == 0) && (invoiceTaxMoneySum == 0)) {
			is_bug = true;
			// bug_message = "合计数量和合计发票金额不可同时0";
		}
		else if ((closedQuantitySum > 0) && (invoiceTaxMoneySum < 0)) {
			is_bug = true;
		}
		else if ((closedQuantitySum < 0) && (invoiceTaxMoneySum > 0)) {
			is_bug = true;
		}
		else if ((closedQuantitySum >= 0) && (invoiceTaxMoneySum >= 0)) {
			post.setIsBlue("1");
		}
		else if ((closedQuantitySum <= 0) && (invoiceTaxMoneySum <= 0)) {
			post.setIsBlue("0");
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

//	@GetMapping("/{code}/download")
//	public ResponseEntity<Resource> download(@PathVariable("code") String code) {
//		StatementMain main = this.statementMainRepository.findOneByCode(code);
//		if (main == null)
//			show404();
//
//		return download(Constants.PATH_UPLOADS_STATEMENT + File.separator + main.getAttachFileName(),
//				main.getAttachOriginalName());
//	}

	private String createLockJsonString(StatementMain main) {
		String jsonString = "";
		List<StatementDetail> list = statementDetailRepository.findByCode(main.getCode());

		List<Map<String, String>> postData = new ArrayList();

		for (StatementDetail detail : list) {
			Map<String, String> map = new HashMap<>();
			PurchaseInDetail purchaseInDetail = purchaseInDetailRepository.findOneById(detail.getPiDetailId());

			if (purchaseInDetail == null)
				continue;

			map.put("autoid", String.valueOf(purchaseInDetail.getAutoId()));
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
