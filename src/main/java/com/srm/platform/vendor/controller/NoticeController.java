package com.srm.platform.vendor.controller;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Notice;
import com.srm.platform.vendor.model.NoticeRead;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.NoticeReadRepository;
import com.srm.platform.vendor.repository.NoticeRepository;
import com.srm.platform.vendor.repository.UnitRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.searchitem.AccountSearchResult;
import com.srm.platform.vendor.searchitem.NoticeReadSearchResult;
import com.srm.platform.vendor.searchitem.NoticeSearchResult;
import com.srm.platform.vendor.searchitem.PermissionUnit;
import com.srm.platform.vendor.searchitem.SearchItem;
import com.srm.platform.vendor.searchitem.VendorSearchItem;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.UploadFileHelper;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/notice")
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VENDOR') or hasAuthority('公告通知-查看列表')")
public class NoticeController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NoticeRepository noticeRepository;

	@Autowired
	private NoticeReadRepository noticeReadRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private VendorRepository vendorRepository;
	
	@Autowired
	private UnitRepository unitRepository;


	// 用户管理->列表
	@GetMapping({ "/", "" })
	public String index(Model model) {
		return "notice/list";
	}

	// 用户管理->列表
	@GetMapping("/list")
	public @ResponseBody Page<NoticeSearchResult> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "deploydate");
		String dir = requestParams.getOrDefault("dir", "desc");
		String search = requestParams.getOrDefault("search", "");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);
		String state = requestParams.getOrDefault("state", null);
		String create_account = requestParams.getOrDefault("create_account", null);

		Date startDate = Utils.parseDate(start_date);
		Date endDate = Utils.getNextDate(end_date);

		switch (order) {
		case "create_name":
			order = "b.realname";
			break;
		case "create_unitname":
			order = "c.name";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		String selectQuery = "SELECT distinct a.*, b.realname create_name, c.name create_unitname, d.realname verify_name, e.read_date read_date ";
		String countQuery = "select count(distinct a.id) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "FROM notice a left join account b on a.create_account=b.id left join unit c on a.create_unit=c.id "
				+ "left join account d on d.id=a.verify_account left join notice_read e on a.id=e.notice_id and e.to_account_id=:to_account where type=1 ";

		List<String> unitList = this.getDefaultUnitList();
		Map<String, Object> params = new HashMap<>();

		if (isVendor()) {
			bodyQuery += " and e.to_account_id=:to_account and a.state=3";
			params.put("to_account", this.getLoginAccount().getId());
		} else {
			if (this.hasAuthority("公告通知-发布")) {
				bodyQuery += " and ((a.create_unit in :unitList and a.state=2) or create_account=:create_account or (e.to_account_id=:to_account and a.state=3))";
				params.put("unitList", unitList);
				params.put("create_account", this.getLoginAccount().getId());
				params.put("to_account", this.getLoginAccount().getId());
			} else {
				bodyQuery += " and (create_account=:create_account or (e.to_account_id=:to_account and a.state=3))";
				params.put("create_account", this.getLoginAccount().getId());
				params.put("to_account", this.getLoginAccount().getId());
			}
		}

		if (!search.trim().isEmpty()) {
			bodyQuery += " and (a.title like CONCAT('%',:search, '%') or a.content like CONCAT('%',:search, '%')) ";
			params.put("search", search.trim());
		}

		if (startDate != null) {
			bodyQuery += " and a.verify_date>=:startDate";
			params.put("startDate", startDate);
		}
		if (endDate != null) {
			bodyQuery += " and a.verify_date<:endDate";
			params.put("endDate", endDate);
		}
		if (create_account != null && !create_account.isEmpty()) {
			bodyQuery += " and b.realname like CONCAT('%',:createAccount, '%')";
			params.put("createAccount", create_account);
		}
		if (state != null && !state.equals("-1")) {
			bodyQuery += " and a.state=:state";
			params.put("state", state);
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "NoticeSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<NoticeSearchResult>(list, request, totalCount.longValue());
	}

	// 用户管理->修改
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable("id") Long id, Model model) {
		Notice notice = noticeRepository.findOneById(id);
		checkPermission(notice);

		setReadDate(id);
		model.addAttribute("notice", notice);
		return "notice/edit";
	}

	// 新建
	@GetMapping("/add")
	@PreAuthorize("hasRole('ROLE_BUYER') and hasAuthority('公告通知-新建')")
	public String add(Model model) {
		Notice notice = new Notice();
		notice.setUnit(this.getLoginAccount().getUnit());
		notice.setCreateAccount(this.getLoginAccount());
		model.addAttribute("notice", notice);
		model.addAttribute("vendorList", "");
		return "notice/edit";
	}

	// 删除
	@GetMapping("/{id}/delete")
	@PreAuthorize("hasRole('ROLE_BUYER') and hasAuthority('公告通知-新建')")
	public @ResponseBody Boolean delete(@PathVariable("id") Long id, Model model) {
		Notice notice = noticeRepository.findOneById(id);
		noticeRepository.delete(notice);
		return true;
	}

	@GetMapping("/{id}/deleteattach")
	@PreAuthorize("hasRole('ROLE_BUYER') and hasAuthority('公告通知-新建')")
	public @ResponseBody Boolean deleteAttach(@PathVariable("id") Long id) {
		Notice notice = noticeRepository.findOneById(id);
		
		File attach = new File(UploadFileHelper.getUploadDir(Constants.PATH_UPLOADS_NOTICE) + File.separator + notice.getAttachFileName());
		if (attach.exists())
			attach.delete();
		notice.setAttachFileName(null);
		notice.setAttachOriginalName(null);
		noticeRepository.save(notice);
		return true;
	}

	// 用户修改
	@Transactional
	@PostMapping("/update")
	@PreAuthorize("hasRole('ROLE_BUYER') and (hasAuthority('公告通知-新建') or hasAuthority('公告通知-发布'))")
	public @ResponseBody Notice update_ajax(@RequestParam(value = "attach", required = false) MultipartFile attach,
			@RequestParam Map<String, String> requestParams) {

		String origianlFileName = null;
		String savedFileName = null;
		if (attach != null) {
			origianlFileName = attach.getOriginalFilename();
			File file = UploadFileHelper.simpleUpload(attach, true, Constants.PATH_UPLOADS_NOTICE);

			if (file != null)
				savedFileName = file.getName();
		}

		String id = requestParams.get("id");
		String title = requestParams.get("title");
		String content = requestParams.get("content");
		String stateStr = requestParams.get("state");
		String to_all_vendor = requestParams.get("to_all_vendor");
		String to_unit_account = requestParams.get("to_unit_account");
		String vendor_list = requestParams.get("vendor_list");
		String account_list = requestParams.get("account_list");
		Integer state = Integer.parseInt(stateStr);
		Notice notice = new Notice();

		if (id != null && !id.isEmpty()) {
			notice = noticeRepository.findOneById(Long.parseLong(id));
		}

		notice.setState(state);

		if (state <= 2) {
			notice.setTitle(title);
			notice.setType(Constants.NOTICE_TYPE_USER);
			notice.setContent(content);
			notice.setCreateDate(new Date());
			if (savedFileName != null) {
				notice.setAttachFileName(savedFileName);
				notice.setAttachOriginalName(origianlFileName);
			}

			notice.setUnit(this.getLoginAccount().getUnit());
			notice.setCreateAccount(this.getLoginAccount());
			if (to_all_vendor != null) {
				notice.setToAllVendor(1);
				notice.setVendorCodeList(null);
			} else {
				notice.setToAllVendor(0);
				if (vendor_list != null && !vendor_list.trim().isEmpty()) {
					notice.setVendorCodeList(vendor_list);
				} else {
					notice.setVendorCodeList(null);
				}
			}
			if (to_unit_account != null) {
				notice.setToUnitAccount(1);				
				if (account_list != null && !account_list.trim().isEmpty()) {
					notice.setAccountIdList(account_list);
				} else {
					notice.setAccountIdList(null);
				}
			} else {
				notice.setToUnitAccount(0);
				notice.setAccountIdList(null);
			}

		} else {
			notice.setVerifyAccount(this.getLoginAccount());
			notice.setVerifyDate(new Date());

			if (state == 3) {
				List<Account> toAccountList = new ArrayList<>();
				if (notice.getToAllVendor() == 1) {
					List<String> unitIdList = getAllUnitsOfId(notice.getUnit().getId());

					toAccountList.addAll(accountRepository.findAllVendorsByUnitIdList(unitIdList));

				} else {
					if (notice.getVendorCodeList() != null && !notice.getVendorCodeList().isEmpty()) {
						List<String> vendorCodeList = Arrays.asList(StringUtils.split(notice.getVendorCodeList(), ","));
						toAccountList.addAll(accountRepository.findAccountsByVendorCodeList(vendorCodeList));
					}
				}

				if (notice.getToUnitAccount() == 1) {
					toAccountList.addAll(
							accountRepository.findAllByIdList(convertListStrToLongList(notice.getAccountIdList())));
				}

				for (Account account : toAccountList) {
					NoticeRead noticeRead = new NoticeRead();
					noticeRead.setNotice(notice);
					noticeRead.setAccount(account);
					noticeReadRepository.save(noticeRead);
				}
			}
		}

		notice = noticeRepository.save(notice);

		return notice;
	}

	@GetMapping("/readlist/{id}")
	public @ResponseBody Page<NoticeReadSearchResult> readlist_ajax(@PathVariable("id") Long id,
			@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "deploydate");
		String dir = requestParams.getOrDefault("dir", "desc");
		String search = requestParams.getOrDefault("search", "");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);

		Date startDate = Utils.parseDate(start_date);
		Date endDate = Utils.getNextDate(end_date);

		switch (order) {
		case "unitname":
			order = "d.name";
			break;
		case "vendortname":
			order = "c.name";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		String selectQuery = "select b.realname, c.name vendorname, d.name unitname, a.read_date  ";
		String countQuery = "select count(b.realname) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "from notice_read a left join account b on a.to_account_id=b.id left join vendor c on b.vendor_code=c.code "
				+ "left join unit d on b.unit_id=d.id where b.realname is not null and a.notice_id=:noticeId ";

		Map<String, Object> params = new HashMap<>();
		params.put("noticeId", id);

		if (!search.trim().isEmpty()) {
			bodyQuery += " and (b.realname like CONCAT('%',:search, '%') or c.name like CONCAT('%',:search, '%') or d.name like CONCAT('%',:search, '%')) ";
			params.put("search", search.trim());
		}

		if (startDate != null) {
			bodyQuery += " and a.read_date>=:startDate";
			params.put("startDate", startDate);
		}
		if (endDate != null) {
			bodyQuery += " and a.read_date<:endDate";
			params.put("endDate", endDate);
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "NoticeReadSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<NoticeReadSearchResult>(list, request, totalCount.longValue());
	}

	@GetMapping("/{id}/account/list")
	public @ResponseBody List<AccountSearchResult> accountList_ajax(@PathVariable("id") String noticeId) {

		if (noticeId == null || "null".equals(noticeId)) {
			return new ArrayList<>();
		}

		Notice notice = noticeRepository.findOneById(Long.valueOf(noticeId));

		List<Long> idList = new ArrayList<>();
		String accountIdListStr = notice.getAccountIdList();
		if (accountIdListStr != null) {
			idList = convertListStrToLongList(accountIdListStr);
		} else {
			return new ArrayList<>();
		}

		if (idList.isEmpty())
			return new ArrayList<>();

		String selectQuery = "SELECT t.*, u.name unitname, v.name vendorname FROM account t left join unit u on t.unit_id=u.id "
				+ "left join vendor v on t.vendor_code=v.code where t.id in :idList ";

		Map<String, Object> params = new HashMap<>();

		params.put("idList", idList);

		Query q = em.createNativeQuery(selectQuery, "AccountSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		return q.getResultList();

	}
	@GetMapping("/{id}/vendor/list")
	public @ResponseBody List<VendorSearchItem> vendorList_ajax(@PathVariable("id") String noticeId) {

		if (noticeId == null || "null".equals(noticeId)) {
			return new ArrayList<>();
		}

		Notice notice = noticeRepository.findOneById(Long.valueOf(noticeId));

		String vendorCodeListStr = notice.getVendorCodeList();
		String[] vendorList = null;
		if (vendorCodeListStr !=null && !vendorCodeListStr.trim().isEmpty()) {
			vendorList = StringUtils.split(vendorCodeListStr, ",");
		}
		
		if (vendorList == null) {
			return new ArrayList<>();
		}
		
		return vendorRepository.findVendorsByCodeList(vendorList);

	}

	@GetMapping("/account/list")
	public @ResponseBody Page<AccountSearchResult> accountListForSelect_ajax(
			@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");
		String stateStr = requestParams.getOrDefault("state", "");
		String role = requestParams.getOrDefault("role", "");

		Integer state = Integer.parseInt(stateStr);

		if (order.equals("vendorname"))
			order = "v.name";
		if (order.equals("unitname"))
			order = "u.name";

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		String selectQuery = "SELECT t.*, u.name unitname, v.name vendorname ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "FROM account t left join unit u on t.unit_id=u.id left join vendor v on t.vendor_code=v.code where role<>'ROLE_VENDOR' ";

		Map<String, Object> params = new HashMap<>();

		if (!search.trim().isEmpty()) {
			bodyQuery += " and (u.name LIKE CONCAT('%',:search, '%') or t.username LIKE CONCAT('%',:search, '%') or t.realname LIKE CONCAT('%',:search, '%') or t.duty LIKE CONCAT('%',:search, '%') or t.email LIKE CONCAT('%',:search, '%')) ";
			params.put("search", search.trim());
		}

		if (state >= 0) {
			bodyQuery += " and state=:state";
			params.put("state", state);
		}

		if (!role.trim().isEmpty()) {
			bodyQuery += " and role=:role";
			params.put("role", role);
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "AccountSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<AccountSearchResult>(list, request, totalCount.longValue());

	}

	@GetMapping("/{id}/download")
	public ResponseEntity<Resource> download(@PathVariable("id") Long id) {
		Notice notice = noticeRepository.findOneById(id);
		checkPermission(notice);

		return download(Constants.PATH_UPLOADS_NOTICE + File.separator + notice.getAttachFileName(),
				notice.getAttachOriginalName());
	}

	private void checkPermission(Notice notice) {
		if (notice == null || notice.getType() != Constants.NOTICE_TYPE_USER)
			show404();

		boolean isVisible = false;

		if (isAuthorizedUnit(notice.getCreateAccount().getUnit().getId()) && this.hasAuthority("公告通知-发布")) {
			isVisible = true;
		} else if (notice.getCreateAccount().getId() == this.getLoginAccount().getId()) {
			isVisible = true;
		} else if (isVisibleNotice(notice.getId())) {
			isVisible = true;
		}

		if (!isVisible)
			show403();

	}
	
	
	public List<String> getAllUnitsOfId(Long unitId) {
		String unitList = String.valueOf(unitId);
		unitList = StringUtils.append(unitList, "," + searchChildren(unitList));

		return Arrays.asList(StringUtils.split(unitList, ","));
	}

	private String searchChildren(String parentIdList) {
		String childList = "";
		List<PermissionUnit> unitList = unitRepository.findChildrenByParentId(StringUtils.split(parentIdList, ","));
		for (PermissionUnit unit : unitList) {
			if (unit != null)
				childList = StringUtils.append(childList, "," + unit.getUnits());
		}

		if (childList.isEmpty()) {
			return childList;
		} else {
			childList = StringUtils.append(childList, "," + searchChildren(childList));
			return childList;
		}
	}

}
