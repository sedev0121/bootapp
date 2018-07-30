package com.srm.platform.vendor.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.PermissionGroup;
import com.srm.platform.vendor.model.PermissionGroupUser;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.PermissionGroupRepository;
import com.srm.platform.vendor.repository.PermissionGroupUserRepository;
import com.srm.platform.vendor.repository.UnitRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.service.AccountService;
import com.srm.platform.vendor.utility.AccountSaveForm;
import com.srm.platform.vendor.utility.AccountSearchItem;
import com.srm.platform.vendor.utility.AccountSearchResult;

@Controller
@RequestMapping(path = "/account")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AccountController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PermissionGroupRepository permissionGroupRepository;
	@Autowired
	private PermissionGroupUserRepository permissionGroupUserRepository;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private UnitRepository unitRepository;

	@Autowired
	private AccountService accountService;

	// 用户管理->列表
	@GetMapping({ "/", "" })
	public String index(Model model) {

		PageRequest request = PageRequest.of(0, 2);
		Page<Account> result = accountRepository.findAll(request);
		result.getTotalElements();
		logger.info(result.toString());
		model.addAttribute("accounts", result.getContent());
		return "admin/account/list";
	}

	// 用户管理->列表
	@GetMapping("/list")
	public @ResponseBody Page<AccountSearchResult> list_ajax(@RequestParam Map<String, String> requestParams) {
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
		Page<Account> result = accountRepository.findBySearchTerm(search, request);

		String selectQuery = "SELECT t.*, u.name unitname, v.name vendorname ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "FROM account t left join unit u on t.unit_id=u.id left join vendor v on t.vendor_code=v.code where 1=1 ";

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

	// 用户管理->修改
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable("id") Long id, Model model) {
		Account account = accountRepository.findOneById(id);
		if (account == null)
			show404();

		PermissionGroupUser temp = new PermissionGroupUser();
		temp.setAccountId(account.getId());

		Example<PermissionGroupUser> example = Example.of(temp);
		List<PermissionGroupUser> resultList = permissionGroupUserRepository.findAll(example);

		List<PermissionGroup> groupList = new ArrayList<>();
		for (PermissionGroupUser group : resultList) {
			groupList.add(permissionGroupRepository.findOneById(group.getGroupId()));
		}

		ObjectMapper mapper = new ObjectMapper();
		String jsonGroupString = "";
		try {
			jsonGroupString = mapper.writeValueAsString(groupList);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("account", account);
		model.addAttribute("groupList", jsonGroupString);
		return "admin/account/edit";
	}

	// 用户管理->新建
	@GetMapping("/add")
	public String add(Model model) {
		model.addAttribute("account", new Account());
		model.addAttribute("groupList", "[]");
		return "admin/account/edit";
	}

	// 用户管理->删除
	@GetMapping("/{id}/delete")
	public @ResponseBody Boolean delete(@PathVariable("id") Long id, Model model) {
		Account account = accountRepository.findOneById(id);
		accountRepository.delete(account);
		return true;
	}

	// 用户修改
	@Transactional
	@PostMapping("/update")
	public @ResponseBody Account update_ajax(AccountSaveForm accountSaveForm) {

		Account account = new Account();
		if (accountSaveForm.getId() != null) {
			account = accountRepository.findOneById(accountSaveForm.getId());

		}

		account.setUsername(accountSaveForm.getUsername());
		account.setRealname(accountSaveForm.getRealname());
		account.setWeixin(accountSaveForm.getWeixin());
		account.setQq(accountSaveForm.getQq());
		account.setYahoo(accountSaveForm.getYahoo());
		account.setWangwang(accountSaveForm.getWangwang());
		account.setMobile(accountSaveForm.getMobile());
		account.setTel(accountSaveForm.getTel());
		account.setAddress(accountSaveForm.getAddress());
		account.setGtalk(accountSaveForm.getGtalk());
		account.setEmail(accountSaveForm.getEmail());
		account.setRole(accountSaveForm.getRole());
		account.setDuty(accountSaveForm.getDuty());
		account.setUnit(unitRepository.findOneById(accountSaveForm.getUnit()));

		if (accountSaveForm.getState() != null) {
			account.setState(1);
			account.setStartDate(new Date());
		} else {
			account.setState(0);
			account.setStopDate(new Date());
		}

		if (accountSaveForm.getVendor() != null && !accountSaveForm.getVendor().isEmpty())
			account.setVendor(vendorRepository.findOneByCode(accountSaveForm.getVendor()));
		else
			account.setVendor(null);

		account = accountRepository.save(account);

		permissionGroupUserRepository.deleteByAccountId(account.getId());

		List<Long> permissionList = accountSaveForm.getPermission();
		if (permissionList != null) {
			for (Long id : permissionList) {
				PermissionGroupUser temp = new PermissionGroupUser();
				temp.setAccountId(account.getId());
				temp.setGroupId(id);

				Example<PermissionGroupUser> example = Example.of(temp);
				Optional<PermissionGroupUser> result = permissionGroupUserRepository.findOne(example);
				if (result.isPresent()) {
					temp = result.get();
				}

				permissionGroupUserRepository.save(temp);
			}
		}

		return account;
	}

	@ResponseBody
	@RequestMapping(value = "/search/{keyword}", produces = "application/json")
	public Page<AccountSearchItem> search_ajax(@PathVariable("keyword") String keyword) {
		PageRequest request = PageRequest.of(0, 15, Direction.ASC, "realname");

		return accountRepository.findForAutoComplete(keyword, request);
	}

	// 用户修改
	@Transactional
	@GetMapping("/checkuser")
	public @ResponseBody Boolean checkUser_ajax(@RequestParam("id") Long id,
			@RequestParam("username") String username) {
		Account account = accountRepository.findOneByUsername(username);

		if (account != null && account.getId() != id) {
			return false;
		} else {
			return true;
		}
	}
}
