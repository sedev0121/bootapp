package com.srm.platform.vendor.controller;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.AttachFile;
import com.srm.platform.vendor.model.Master;
import com.srm.platform.vendor.model.PurchaseInDetail;
import com.srm.platform.vendor.model.StatementDetail;
import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.saveform.StatementSaveForm;
import com.srm.platform.vendor.searchitem.StatementDetailItem;
import com.srm.platform.vendor.searchitem.StatementSearchResult;
import com.srm.platform.vendor.u8api.RestApiResponse;
import com.srm.platform.vendor.utility.AccountPermission;
import com.srm.platform.vendor.utility.AccountPermissionInfo;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.UploadFileHelper;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/statement")
public class StatementController extends CommonController {

	private static Long LIST_FUNCTION_ACTION_ID = 21L;

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
		if (!checkSecondPassword()) {
			return "second_password";
		}
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

		if (!checkSecondPassword()) {
			return "second_password";
		}

		checkPermission(main, LIST_FUNCTION_ACTION_ID);

		model.addAttribute("main", main);
		return "statement/edit";
	}

	@GetMapping({ "/{code}/read/{msgid}" })
	public String read(@PathVariable("code") String code, @PathVariable("msgid") Long msgid, Model model) {
		setReadDate(msgid);
		return "redirect:/statement/" + code + "/edit";
	}

	@GetMapping("/{code}/delete")
	public @ResponseBody Boolean delete(@PathVariable("code") String code) {
		StatementMain main = statementMainRepository.findOneByCode(code);
		if (main != null) {
			List<StatementDetail> detailList = statementDetailRepository.findByCode(main.getCode());
			if (detailList != null) {
				for (StatementDetail detail : detailList) {
					PurchaseInDetail purchaseInDetail = purchaseInDetailRepository.findOneById(detail.getPiDetailId());

					if (purchaseInDetail == null)
						continue;

					purchaseInDetail.setState(Constants.PURCHASE_IN_STATE_WAIT);
					purchaseInDetailRepository.save(purchaseInDetail);
				}
			}
			statementDetailRepository.deleteAll(detailList);
			statementMainRepository.delete(main);
			// postUnLock(main);
		}

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
		String invoiceStateStr = requestParams.getOrDefault("invoice_state", "0");
		String typeStr = requestParams.getOrDefault("type", "0");
		String code = requestParams.getOrDefault("code", "");
		String companyIdStr = requestParams.getOrDefault("company", "-1");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);

		Integer state = Integer.parseInt(stateStr);
		Integer invoiceState = Integer.parseInt(invoiceStateStr);
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
			vendorStr = vendor.getCode();
			bodyQuery += " and b.code= :vendor";
			params.put("vendor", vendorStr);

			bodyQuery += " and (a.state=" + Constants.STATEMENT_STATE_DEPLOY + " or a.state >="
					+ Constants.STATEMENT_STATE_CONFIRM + ")";

		} else {
			String subWhere = " 1=0 ";
			AccountPermissionInfo accountPermissionInfo = this.getPermissionScopeOfFunction(LIST_FUNCTION_ACTION_ID);
			if (accountPermissionInfo.isNoPermission()) {
				subWhere = " 1=0 ";
			} else if (accountPermissionInfo.isAllPermission()) {
				subWhere = " 1=1 ";
			} else {
				int index = 0; String key = "";
				for (AccountPermission accountPermission : accountPermissionInfo.getList()) {
					String tempSubWhere = " 1=1 ";
					List<String> allowedVendorCodeList = accountPermission.getVendorList();
					if (allowedVendorCodeList.size() > 0) {
						key = "vendorList" + index;
						tempSubWhere += " and a.vendor_code in :" + key;
						params.put(key, allowedVendorCodeList);
					}

					List<Long> allowedCompanyIdList = accountPermission.getCompanyList();
					if (allowedCompanyIdList.size() > 0) {
						key = "companyList" + index;
						tempSubWhere += " and a.company_id in :" + key;
						params.put(key, allowedCompanyIdList);
					}

					subWhere += " or (" + tempSubWhere + ") ";
					index++;
				}
			}
			

			bodyQuery += " and (" + subWhere + ") ";
		}

		if (!code.trim().isEmpty()) {
			bodyQuery += " and a.code like CONCAT('%',:code, '%') ";
			params.put("code", code.trim());
		}

		Long companyId = Long.valueOf(companyIdStr);
		if (companyId >= 0) {
			bodyQuery += " and com.id=:company";
			params.put("company", companyId);
		}

		if (state > 0) {
			bodyQuery += " and a.state=:state";
			params.put("state", state);
		}

		if (invoiceState > -1) {
			bodyQuery += " and a.invoice_state=:invoiceState";
			params.put("invoiceState", invoiceState);
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

		if (!vendorStr.trim().isEmpty()) {
			bodyQuery += " and (b.code like CONCAT('%',:vendor, '%') or b.name like CONCAT('%',:vendor, '%'))";
			params.put("vendor", vendorStr);
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

	@RequestMapping(value = "/{code}/attaches", produces = "application/json")
	public @ResponseBody List<AttachFile> listAttaches(@PathVariable("code") String code) {
		List<AttachFile> list = attachFileRepository.findAllByTypeCode(Constants.ATTACH_TYPE_STATEMENT, code);

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
				main.setDate(Utils.parseDate(form.getDate()));
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
			} else if (form.getState() == Constants.STATEMENT_STATE_CANCEL_CONFIRM) {
				main.setCanceler(this.getLoginAccount());
				main.setCancelDate(new Date());
				main.setState(Constants.STATEMENT_STATE_DEPLOY);
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
			case Constants.STATEMENT_STATE_CANCEL_CONFIRM:
				toList.add(main.getMaker());
				action = "确认取消";
				break;
			}
		} else {
			List<Long> attachIdList = form.getAttachIds();

			List<AttachFile> oldAttachList = attachFileRepository.findAllByTypeCode(Constants.ATTACH_TYPE_STATEMENT,
					main.getCode());
			List<AttachFile> newAttachList = new ArrayList<AttachFile>();
			if (attachIdList == null) {
				attachFileRepository.deleteAll(oldAttachList);
			} else {
				for (AttachFile attach : oldAttachList) {
					if (!attachIdList.contains(attach.getId())) {
						deleteAttach(Constants.PATH_UPLOADS_STATEMENT + File.separator + attach.getFilename());
						attachFileRepository.delete(attach);
					} else {
						newAttachList.add(attach);
					}
				}
			}

			int index = 1;
			for (AttachFile attach : newAttachList) {
				attach.setRowNo(index++);
				attachFileRepository.save(attach);
			}

			List<MultipartFile> attachList = form.getAttach();
			if (attachList != null) {
				for (MultipartFile attach : attachList) {
					if (attach != null) {
						String origianlFileName = attach.getOriginalFilename();
						File file = UploadFileHelper.simpleUpload(attach, true, Constants.PATH_UPLOADS_STATEMENT);

						String savedFileName = null;
						if (file != null) {
							savedFileName = file.getName();
						}

						AttachFile attachFile = new AttachFile();
						attachFile.setType(Constants.ATTACH_TYPE_STATEMENT);
						attachFile.setCode(main.getCode());
						attachFile.setFilename(savedFileName);
						attachFile.setOriginalName(origianlFileName);
						attachFile.setRowNo(index++);
						attachFileRepository.save(attachFile);
					}
				}
			}

			main.setInvoiceState(form.getInvoice_state());

			if (form.getInvoice_state() == Constants.INVOICE_STATE_DONE) {
				main.setInvoiceType(form.getInvoice_type());
				main.setInvoiceCode(form.getInvoice_code());
				main.setInvoiceMaker(this.getLoginAccount());
				main.setInvoiceMakeDate(new Date());
			} else if (form.getInvoice_state() == Constants.INVOICE_STATE_CONFIRMED) {
				main.setInvoiceType(form.getInvoice_type());
				main.setInvoiceCode(form.getInvoice_code());
				main.setInvoiceConfirmer(this.getLoginAccount());
				main.setInvoiceConfirmDate(new Date());
			} else if (form.getInvoice_state() == Constants.INVOICE_STATE_CANCELED) {
				main.setInvoiceType(form.getInvoice_type());
				main.setInvoiceCode(form.getInvoice_code());
				main.setInvoiceCanceler(this.getLoginAccount());
				main.setInvoiceCancelDate(new Date());
			} else if (form.getInvoice_state() == Constants.INVOICE_STATE_CANCEL_UPLOAD) {
				main.setInvoiceType(form.getInvoice_type());
				main.setInvoiceCode(form.getInvoice_code());
				// main.setInvoiceCanceler(this.getLoginAccount());
				// main.setInvoiceCancelDate(new Date());
			} else if (form.getInvoice_state() == Constants.INVOICE_STATE_UPLOAD_ERP) {
				main.setInvoiceType(form.getInvoice_type());
				main.setInvoiceCode(form.getInvoice_code());
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
			case Constants.INVOICE_STATE_CANCEL_UPLOAD:
				main.setInvoiceState(Constants.INVOICE_STATE_CONFIRMED);
				toList.add(main.getMaker());
				action = "审批撤消";
				break;
			}
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
			setPurchaseInDetailState(main, Constants.PURCHASE_IN_STATE_WAIT);
			statementDetailRepository.deleteInBatch(statementDetailRepository.findByCode(main.getCode()));
			if (form.getTable() != null) {
				int i = 1;
				for (Map<String, String> row : form.getTable()) {
					StatementDetail detail = new StatementDetail();
					detail.setCode(main.getCode());
					detail.setPiDetailId(Long.parseLong(row.get("pi_detail_id")));
					try {
						detail.setAdjustTaxCost(Double.parseDouble(row.get("adjust_tax_cost")));
					} catch (Exception e) {

					}

					try {
						detail.setTaxRate(Integer.parseInt(row.get("tax_rate")));
					} catch (Exception e) {

					}

					detail.setRowNo(i++);

					detail = statementDetailRepository.save(detail);
				}
			}
			setPurchaseInDetailState(main, Constants.PURCHASE_IN_STATE_START);
		}

		if (form.getInvoice_state() == Constants.INVOICE_STATE_UPLOAD_ERP) {
			setPurchaseInDetailState(main, Constants.PURCHASE_IN_STATE_FINISH);
		} else if (form.getInvoice_state() == Constants.INVOICE_STATE_CANCEL_UPLOAD) {
			setPurchaseInDetailState(main, Constants.PURCHASE_IN_STATE_START);
		}

		if (form.isInvoiceAction() && form.getInvoice_state() == Constants.INVOICE_STATE_UPLOAD_ERP) {
			GenericJsonResponse<StatementMain> u8Response = this.u8invoice(main);
			if (u8Response.getSuccess() == GenericJsonResponse.SUCCESS) {
				main.setErpInvoiceMakeName(this.getLoginAccount().getRealname());
				main.setErpInvoiceMakeDate(new Date());
				main = statementMainRepository.save(main);
			} else {
				main.setInvoiceState(Constants.INVOICE_STATE_CONFIRMED);
				main = statementMainRepository.save(main);
				return u8Response;
			}
		}

		return jsonResponse;
	}

	private GenericJsonResponse<StatementMain> u8invoice(StatementMain main) {

		GenericJsonResponse<StatementMain> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null,
				main);

		RestApiResponse response = apiClient.postForU8Iinvoice(createU8InvoicePostData(main));

		if (!response.isSuccess()) {
			response.getErrmsg();
			jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, response.getErrmsg(), main);
		}

		return jsonResponse;
	}

	private Map<String, Object> createU8InvoicePostData(StatementMain main) {
		Vendor vendor = main.getVendor();
		Map<String, Object> postParams = new HashMap<>();
		postParams.put("cPBVBillType", main.getInvoiceType() == 1 ? "01" : "02"); // 发票类型，01:专用发票02:普通发票
		postParams.put("cPTCode", main.getType() == 1 ? "01" : "02"); // 采购类型编码，01:采购 02:委外
		postParams.put("dPBVDate", Utils.formatDateTime(main.getInvoiceMakeDate())); // 开票日期
		postParams.put("cVenCode", vendor.getCode()); // 供应商编码
		postParams.put("cUnitCode", vendor.getCode()); // 代垫供应商编码
		postParams.put("cDepCode", "02"); // 部门编码
		postParams.put("cPersonCode", this.getLoginAccount().getId().toString()); // 业务员
		postParams.put("iPBVTaxRate", main.getTaxRate().toString()); // 表头税率，填个默认值就行
		postParams.put("cPBVMaker", main.getMaker().getRealname()); // 制单人
		postParams.put("cPBVCode", main.getInvoiceCode()); // 发票号
		postParams.put("cVenBank", vendor.getBankOpen()); // 银行名称
		postParams.put("cVenAccount", vendor.getBankAccNumber()); // 银行卡号
		postParams.put("cVenPerson", vendor.getContact()); // 联系人

		List<StatementDetailItem> list = statementDetailRepository.findDetailsByCode(main.getCode());
		List<Map<String, String>> listParams = new ArrayList<Map<String, String>>();
		for (StatementDetailItem detail : list) {
			Map<String, String> row = new HashMap<>();
			row.put("RdsId", detail.getPi_auto_id()); // 外购入库单行ID
			row.put("cInvCode", detail.getInventory_code()); // 存货编码
			row.put("dInDate", detail.getPi_date()); // 入库时间
			row.put("iPBVQuantity", detail.getPi_quantity()); // 开票数量

			String taxPriceStr = detail.getTax_price();
			String taxCostStr = detail.getTax_cost();
			String taxRateStr = detail.getTax_rate();
			String adjustTaxCostStr = detail.getAdjust_tax_cost();
			String quantityStr = detail.getPi_quantity();

			double quantity = 0, taxPrice = 0, taxCost = 0, taxRate = 0, adjustTaxCost = 0, price = 0, cost = 0,
					tax = 0, invoiceTaxPrice = 0, invoiceTaxCost = 0;

			try {
				quantity = Double.parseDouble(quantityStr);
			} catch (Exception e) {

			}

			try {
				taxPrice = Double.parseDouble(taxPriceStr);
			} catch (Exception e) {

			}

			try {
				taxCost = Double.parseDouble(taxCostStr);
			} catch (Exception e) {

			}

			try {
				taxRate = Double.parseDouble(taxRateStr);
			} catch (Exception e) {

			}

			try {
				adjustTaxCost = Double.parseDouble(adjustTaxCostStr);
			} catch (Exception e) {

			}

			invoiceTaxCost = taxCost + adjustTaxCost;
			invoiceTaxPrice = invoiceTaxCost / quantity;
			price = invoiceTaxPrice * 100 / (100 + taxRate);
			cost = price * quantity;
			tax = invoiceTaxCost - cost;

			row.put("iOriCost", Utils.priceNumber(price)); // 原币单价
			row.put("iOriTaxCost", Utils.priceNumber(invoiceTaxPrice)); // 原币含税单价
			row.put("iOriMoney", Utils.costNumber(cost)); // 原币金额
			row.put("iOriTaxPrice", Utils.costNumber(tax)); // 原币税额
			row.put("iOriSum", Utils.costNumber(invoiceTaxCost)); // 原币价税合计

			row.put("iCost", Utils.priceNumber(price)); // 本币单价
			row.put("iMoney", Utils.costNumber(cost)); // 本币金额
			row.put("iTaxPrice", Utils.costNumber(tax)); // 本币税额
			row.put("iSum", Utils.costNumber(invoiceTaxCost)); // 本币价税合计

			row.put("iTaxRate", detail.getTax_rate()); // 税率
			row.put("ivouchrowno", detail.getRow_no().toString()); // 行号
			row.put("cbMemo", detail.getConfirmed_memo()); // 行备注

			listParams.add(row);
		}

		postParams.put("list", listParams);

		return postParams;
	}

	@GetMapping("/{code}/download/{rowNo}")
	public ResponseEntity<Resource> download(@PathVariable("code") String code, @PathVariable("rowNo") Integer rowNo) {
		AttachFile attach = this.attachFileRepository.findOneByTypeCodeAndRowNo(Constants.ATTACH_TYPE_STATEMENT, code,
				rowNo);
		if (attach == null) {
			show404();
		}
		return download(Constants.PATH_UPLOADS_STATEMENT + File.separator + attach.getFilename(),
				attach.getOriginalName());
	}

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
		// String postJson = createLockJsonString(main);
		// String response = apiClient.postLock(String.format("{%s}", postJson));
	}

	private void postUnLock(StatementMain main) {
		// String postJson = createLockJsonString(main);
		// String response = apiClient.postUnLock(String.format("{%s}", postJson));
	}

	private void checkPermission(StatementMain main, Long functionActionId) {
		if (this.isVendor()) {
			if (!main.getVendor().getCode().equals(this.getLoginAccount().getVendor().getCode())) {
				show403();
			}
		} else {
			if (!hasPermission(main, functionActionId)) {
				show403();
			}
		}
	}

	private boolean hasPermission(StatementMain main, Long functionActionId) {
		boolean isValid = false;
		AccountPermissionInfo accountPermissionInfo = this.getPermissionScopeOfFunction(functionActionId);
		if (accountPermissionInfo.isNoPermission()) {
			isValid = false;
		} else if (accountPermissionInfo.isAllPermission()) {
			isValid = true;
		} else {
			if (main.getVendor() != null && main.getCompany() != null) {
				for (AccountPermission accountPermission : accountPermissionInfo.getList()) {

					List<String> allowedVendorCodeList = accountPermission.getVendorList();
					List<Long> allowedCompanyIdList = accountPermission.getCompanyList();

					if (allowedVendorCodeList.size() > 0 && !allowedVendorCodeList.contains(main.getVendor().getCode())) {
						continue;
					}

					if (allowedCompanyIdList.size() > 0 && !allowedCompanyIdList.contains(main.getCompany().getId())) {
						continue;
					}

					isValid = true;
					break;

				}
			}
		}

		return isValid;
	}

	@Transactional
	@GetMapping("/bulk_review")
	public @ResponseBody GenericJsonResponse<String> bulkReview() {
		GenericJsonResponse<String> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, null);
		if (!this.hasAuthority("对账单管理-审核")) {
			jsonResponse.setSuccess(GenericJsonResponse.SUCCESS);
			jsonResponse.setErrmsg("没有此权限");
		} else {
			Account account = this.getLoginAccount();
			Date today = new Date();
			List<StatementMain> list = statementMainRepository.findAllPending(Constants.STATEMENT_STATE_SUBMIT);
			list = filter(list);

			for (StatementMain item : list) {
				item.setState(Constants.STATEMENT_STATE_REVIEW);
				item.setReviewDate(today);
				item.setReviewer(account);

				statementMainRepository.save(item);
				this.addOpertionHistory(item.getCode(), "批量审核", null);
			}
		}

		return jsonResponse;
	}

	@Transactional
	@GetMapping("/bulk_deploy")
	public @ResponseBody GenericJsonResponse<String> bulkDeploy() {
		GenericJsonResponse<String> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, null);
		if (!this.hasAuthority("对账单管理-发布")) {
			jsonResponse.setSuccess(GenericJsonResponse.SUCCESS);
			jsonResponse.setErrmsg("没有此权限");
		} else {
			Account account = this.getLoginAccount();
			Date today = new Date();
			List<StatementMain> list = statementMainRepository.findAllPending(Constants.STATEMENT_STATE_REVIEW);
			list = filter(list);

			for (StatementMain item : list) {
				item.setState(Constants.STATEMENT_STATE_DEPLOY);
				item.setDeployDate(today);
				item.setDeployer(account);

				statementMainRepository.save(item);
				this.addOpertionHistory(item.getCode(), "批量发布", null);
			}
		}

		return jsonResponse;
	}

	private List<StatementMain> filter(List<StatementMain> list) {

		List<StatementMain> result = new ArrayList<StatementMain>();
		List<StatementMain> filteredList = new ArrayList<StatementMain>();

		AccountPermissionInfo accountPermissionInfo = this.getPermissionScopeOfFunction(LIST_FUNCTION_ACTION_ID);
		for (AccountPermission accountPermission : accountPermissionInfo.getList()) {

			List<Long> allowedCompanyIdList = accountPermission.getCompanyList();
			if (allowedCompanyIdList.size() > 0) {
				for (StatementMain item : list) {
					if (allowedCompanyIdList.contains(item.getCompany().getId())) {
						filteredList.add(item);
					}
				}
				list = filteredList;
			}

			filteredList = new ArrayList<StatementMain>();
			List<String> allowedVendorCodeList = accountPermission.getVendorList();
			if (allowedVendorCodeList.size() > 0) {
				for (StatementMain item : list) {
					if (allowedVendorCodeList.contains(item.getVendor().getCode())) {
						filteredList.add(item);
					}
				}
				list = filteredList;
			}

			result.addAll(list);
		}

		return list;
	}

	private void setPurchaseInDetailState(StatementMain main, int state) {
		List<StatementDetail> detailList = statementDetailRepository.findByCode(main.getCode());
		for (StatementDetail detail : detailList) {
			PurchaseInDetail purchaseInDetail = purchaseInDetailRepository.findOneById(detail.getPiDetailId());

			if (purchaseInDetail != null) {
				purchaseInDetail.setState(state);
				purchaseInDetailRepository.save(purchaseInDetail);
			}
		}
	}

	private boolean checkSecondPassword() {
		if (this.isVendor()) {
			Integer secondPassword = (Integer) httpSession.getAttribute("second_password");
			if (secondPassword == null || secondPassword != 1) {
				return false;
			}
		}

		return true;
	}
}
