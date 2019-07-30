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
import com.srm.platform.vendor.model.AttachFile;
import com.srm.platform.vendor.model.DeliveryMain;
import com.srm.platform.vendor.model.Master;
import com.srm.platform.vendor.model.PurchaseInDetail;
import com.srm.platform.vendor.model.ContractDetail;
import com.srm.platform.vendor.model.ContractMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.saveform.ContractSaveForm;
import com.srm.platform.vendor.searchitem.ContractSearchResult;
import com.srm.platform.vendor.u8api.RestApiResponse;
import com.srm.platform.vendor.utility.AccountPermission;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.U8InvoicePostData;
import com.srm.platform.vendor.utility.U8InvoicePostEntry;
import com.srm.platform.vendor.utility.UploadFileHelper;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/contract")
public class ContractController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static Long LIST_FUNCTION_ACTION_ID = 21L;
	private static Long NEW_FUNCTION_ACTION_ID = 22L;
	private static Long REVIEW_FUNCTION_ACTION_ID = 23L;
	private static Long DEPLOY_FUNCTION_ACTION_ID = 24L;
	private static Long CANCEL_FUNCTION_ACTION_ID = 25L;
	private static Long CONFIRM_FUNCTION_ACTION_ID = 26L;
	private static Long ERP_FUNCTION_ACTION_ID = 27L;
	
	
	@PersistenceContext
	private EntityManager em;

	@Override
	protected String getOperationHistoryType() {
		return "contract";
	};
	
	// 查询列表
	@GetMapping({ "", "/" })
	@PreAuthorize("hasRole('ROLE_BUYER') and hasAuthority('对账单管理-查看列表') or hasRole('ROLE_VENDOR')")
	public String index() {
		return "contract/index";
	}

	@GetMapping({ "/add" })
	@PreAuthorize("hasAuthority('对账单管理-新建/提交')")
	public String add(Model model) {
		ContractMain main = new ContractMain();
		main.setMaker(this.getLoginAccount());		
		
		Master master = masterRepository.findOneByItemKey(Constants.KEY_AUTO_TASK_STATEMENT_DATE);
		if (master == null) {
			master = new Master();	
			master.setItemKey(Constants.KEY_AUTO_TASK_STATEMENT_DATE);
			master.setItemValue(Constants.DEFAULT_STATEMENT_DATE);
			masterRepository.save(master);
		}
		
		model.addAttribute("main", main);
		return "contract/edit";
	}

	@GetMapping({ "/{code}/edit" })
	public String edit(@PathVariable("code") String code, Model model) {
		ContractMain main = contractMainRepository.findOneByCode(code);
		if (main == null)
			show404();

		checkPermission(main, LIST_FUNCTION_ACTION_ID);
		
		model.addAttribute("main", main);
		return "contract/edit";
	}

	@GetMapping({ "/{code}/read/{msgid}" })
	public String read(@PathVariable("code") String code, @PathVariable("msgid") Long msgid, Model model) {
		setReadDate(msgid);
		return "redirect:/contract/" + code + "/edit";
	}

	@GetMapping("/{code}/delete")
	public @ResponseBody Boolean delete(@PathVariable("code") String code) {
		ContractMain main = contractMainRepository.findOneByCode(code);
		if (main != null) {
			List<ContractDetail> detailList = contractDetailRepository.findDetailsByCode(main.getCode());
			contractDetailRepository.deleteAll(detailList);
			contractMainRepository.delete(main);
		}

		return true;
	}

	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<ContractSearchResult> list_ajax(@RequestParam Map<String, String> requestParams) {
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

		String selectQuery = "select a.*, b.name vendor_name, b.address vendor_address, com.name company_name, d.realname make_name ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "from contract_main a left join vendor b on a.vendor_code=b.code  "
				+ "left join company com on a.company_id=com.id left join account d on a.make_id=d.id where 1=1 ";

		Map<String, Object> params = new HashMap<>();

		if (isVendor()) {
			Vendor vendor = this.getLoginAccount().getVendor();
			vendorStr = vendor.getCode();
			bodyQuery += " and b.code= :vendor";
			params.put("vendor", vendorStr);

			bodyQuery += " and (a.state=" + Constants.STATEMENT_STATE_DEPLOY + " or a.state >=" + Constants.STATEMENT_STATE_CONFIRM + ")";

		} else {
			String subWhere = " 1=0 ";
			
			AccountPermission accountPermission = this.getPermissionScopeOfFunction(LIST_FUNCTION_ACTION_ID);
			List<Long> allowedCompanyIdList = accountPermission.getCompanyList();
			if (!(allowedCompanyIdList == null || allowedCompanyIdList.size() == 0)) {
				subWhere += " or a.company_id in :companyList";
				params.put("companyList", allowedCompanyIdList);
			}

			List<String> allowedVendorCodeList = accountPermission.getVendorList();
			if (!(allowedVendorCodeList == null || allowedVendorCodeList.size() == 0)) {
				subWhere += " or a.vendor_code in :vendorList";
				params.put("vendorList", allowedVendorCodeList);
			}

			bodyQuery += " and (" + subWhere + ") ";
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
		q = em.createNativeQuery(selectQuery, "ContractSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<ContractSearchResult>(list, request, totalCount.longValue());

	}

	@RequestMapping(value = "/{code}/details", produces = "application/json")
	public @ResponseBody List<ContractDetail> details_ajax(@PathVariable("code") String code) {
		List<ContractDetail> list = contractDetailRepository.findDetailsByCode(code);

		return list;
	}
	
	@RequestMapping(value = "/{code}/attaches", produces = "application/json")
	public @ResponseBody List<AttachFile> listAttaches(@PathVariable("code") String code) {
		List<AttachFile> list = attachFileRepository.findAllByTypeCode(Constants.ATTACH_TYPE_STATEMENT, code);

		return list;
	}

	@Transactional
	@PostMapping("/update")
	public @ResponseBody GenericJsonResponse<ContractMain> update_ajax(ContractSaveForm form,
			BindingResult bindingResult) {
		ContractMain main = contractMainRepository.findOneByCode(form.getCode());

		if (main == null) {
			main = new ContractMain();
			main.setCode(form.getCode());
		}

		main.setState(form.getState());
		
		if (form.getState() <= Constants.STATEMENT_STATE_SUBMIT) {
			main.setDate(Utils.parseDate(form.getDate()));
			main.setMakeDate(new Date());
			main.setVendor(vendorRepository.findOneByCode(form.getVendor()));
			main.setMaker(this.getLoginAccount());
			main.setType(form.getType());
			main.setTaxRate(form.getTax_rate());
			main.setCompany(companyRepository.findOneById(form.getCompany()));
		} else if (form.getState() == Constants.STATEMENT_STATE_REVIEW) {
		}
		
		String action = null;
		List<Account> toList = new ArrayList<>();
		
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
		
		List<Long> attachIdList = form.getAttachIds();
		for(Long attachId : attachIdList) {
			logger.info("ID=" + attachId);
		}
		
		List<AttachFile> oldAttachList = attachFileRepository.findAllByTypeCode(Constants.ATTACH_TYPE_STATEMENT, main.getCode());
		List<AttachFile> newAttachList = new ArrayList<AttachFile>();
		if (attachIdList == null) {
			attachFileRepository.deleteAll(oldAttachList);
		} else {
			for(AttachFile attach : oldAttachList) {
				if (!attachIdList.contains(attach.getId())) {
					deleteAttach(Constants.PATH_UPLOADS_STATEMENT + File.separator + attach.getFilename());
					attachFileRepository.delete(attach);
				} else {
					newAttachList.add(attach);
				}
			}
		}
		
		int index = 1;
		for(AttachFile attach : newAttachList) {
			attach.setRowNo(index++);
			attachFileRepository.save(attach);
		}
		
		List<MultipartFile> attachList = form.getAttach();
		if (attachList != null) {
			for(MultipartFile attach : attachList) {
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
		

		if (action != null) {
			String title = String.format("对账单【%s】已由【%s】%s，请及时查阅和处理！", main.getCode(),
					this.getLoginAccount().getRealname(), action);

			this.sendmessage(title, toList, String.format("/contract/%s/read", main.getCode()));
		}
		
		this.addOpertionHistory(main.getCode(), action, form.getContent());
		
		main = contractMainRepository.save(main);

		GenericJsonResponse<ContractMain> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null,
				main);


		return jsonResponse;
	}

	@GetMapping("/{code}/download/{rowNo}")
	public ResponseEntity<Resource> download(@PathVariable("code") String code, @PathVariable("rowNo") Integer rowNo) {
		AttachFile attach = this.attachFileRepository.findOneByTypeCodeAndRowNo(Constants.ATTACH_TYPE_STATEMENT, code, rowNo);
		if (attach == null) {
			show404();
		}
		return download(Constants.PATH_UPLOADS_STATEMENT + File.separator + attach.getFilename(),
				attach.getOriginalName());
	}

	
	private void checkPermission(ContractMain main, Long functionActionId) {
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

	private boolean hasPermission(ContractMain main, Long functionActionId) {
		AccountPermission accountPermission = this.getPermissionScopeOfFunction(functionActionId);
		List<Long> allowedCompanyIdList = accountPermission.getCompanyList();
		List<String> allowedVendorCodeList = accountPermission.getVendorList();

		boolean isValid = false;

		if (!(allowedCompanyIdList == null || allowedCompanyIdList.size() == 0) && main.getCompany() != null
				&& allowedCompanyIdList.contains(main.getCompany().getId())) {
			isValid = true;
		} else if (!(allowedVendorCodeList == null || allowedVendorCodeList.size() == 0) && main.getVendor() != null
				&& allowedVendorCodeList.contains(main.getVendor().getCode())) {
			isValid = true;
		}

		return isValid;
	}
	
}
