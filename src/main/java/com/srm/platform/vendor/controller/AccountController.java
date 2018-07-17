package com.srm.platform.vendor.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.UnitRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.service.AccountService;
import com.srm.platform.vendor.utility.AccountSearchItem;

@Controller
@RequestMapping(path = "/account")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AccountController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountRepository accountRepository;

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
	public @ResponseBody Page<Account> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");

		if (order.equals("vendor"))
			order = "v.name";

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);
		Page<Account> result = accountRepository.findBySearchTerm(search, request);

		return result;
	}

	// 用户管理->修改
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable("id") Long id, Model model) {
		model.addAttribute("account", accountRepository.findOneById(id));
		return "admin/account/edit";
	}

	// 用户管理->新建
	@GetMapping("/add")
	public String add(Model model) {
		model.addAttribute("account", new Account());
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
	@PostMapping("/update")
	public @ResponseBody Account update_ajax(@RequestParam Map<String, String> requestParams) {
		String id = requestParams.get("id");
		String username = requestParams.get("username");
		String realname = requestParams.get("realname");
		String skype = requestParams.get("skype");
		String qq = requestParams.get("qq");
		String unit = requestParams.get("unit");
		String yahoo = requestParams.get("yahoo");
		String wangwang = requestParams.get("wangwang");
		String mobile = requestParams.get("mobile");
		String tel = requestParams.get("tel");
		String address = requestParams.get("address");
		String gtalk = requestParams.get("gtalk");
		String email = requestParams.get("email");
		String password = requestParams.get("password");
		String role = requestParams.get("role");
		String duty = requestParams.get("duty");
		String vendorCode = requestParams.get("vendor");

		Account account;
		if (id != null && !id.isEmpty()) {
			account = accountRepository.findOneById(Long.parseLong(id));
		} else {
			account = new Account();
		}

		account.setUsername(username);
		account.setRealname(realname);
		account.setSkype(skype);
		account.setQq(qq);
		account.setYahoo(yahoo);
		account.setWangwang(wangwang);
		account.setMobile(mobile);
		account.setTel(tel);
		account.setAddress(address);
		account.setGtalk(gtalk);
		account.setEmail(email);
		account.setRole(role);
		account.setDuty(duty);
		account.setUnit(unitRepository.findOneById(Long.parseLong(unit)));

		if (vendorCode != null && !vendorCode.isEmpty())
			account.setVendor(vendorRepository.findOneByCode(vendorCode));
		else
			account.setVendor(null);

		if (password != null) {
			account.setPassword(password);
			account = accountService.save(account);
		} else {
			account = accountRepository.save(account);
		}

		return account;
	}

	@ResponseBody
	@RequestMapping(value = "/search/{keyword}", produces = "application/json")
	public Page<AccountSearchItem> search_ajax(@PathVariable("keyword") String keyword) {
		PageRequest request = PageRequest.of(0, 15, Direction.ASC, "realname");

		return accountRepository.findForAutoComplete(keyword, request);
	}
}
