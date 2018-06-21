package com.srm.platform.vendor.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.thymeleaf.util.StringUtils;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Action;
import com.srm.platform.vendor.model.Function;
import com.srm.platform.vendor.model.FunctionAction;
import com.srm.platform.vendor.model.PermissionGroup;
import com.srm.platform.vendor.model.PermissionGroupFunctionUnit;
import com.srm.platform.vendor.model.PermissionGroupUser;
import com.srm.platform.vendor.model.Unit;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.FunctionActionRepository;
import com.srm.platform.vendor.repository.FunctionRepository;
import com.srm.platform.vendor.repository.PermissionGroupFunctionActionRepository;
import com.srm.platform.vendor.repository.PermissionGroupFunctionUnitRepository;
import com.srm.platform.vendor.repository.PermissionGroupRepository;
import com.srm.platform.vendor.repository.PermissionGroupUserRepository;
import com.srm.platform.vendor.repository.UnitRepository;
import com.srm.platform.vendor.service.AccountService;
import com.srm.platform.vendor.utility.AccountSearchItem;
import com.srm.platform.vendor.utility.IGroupFunctionUnit;

@Controller
@RequestMapping(path = "/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private PermissionGroupRepository permissionGroupRepository;

	@Autowired
	private FunctionRepository functionRepository;
	@Autowired
	private FunctionActionRepository functionActionRepository;
	@Autowired
	private PermissionGroupFunctionUnitRepository permissionGroupFunctionUnitRepository;

	@Autowired
	private PermissionGroupFunctionActionRepository permissionGroupFunctionActionRepository;

	@Autowired
	private UnitRepository unitRepository;

	@Autowired
	private AccountService accountService;

	@Autowired
	private PermissionGroupUserRepository permissionGroupUserReopsitory;

	// Dashboard
	@GetMapping({ "", "/" })
	public String home() {
		return "admin/index";
	}

	// 用户管理->列表
	@GetMapping("/account")
	public String account_list(Model model) {

		PageRequest request = PageRequest.of(0, 2);
		Page<Account> result = accountRepository.findAll(request);
		result.getTotalElements();
		logger.info(result.toString());
		model.addAttribute("accounts", result.getContent());
		return "admin/account/list";
	}

	// 用户管理->列表
	@GetMapping("/account_ajax")
	public @ResponseBody Page<Account> account_list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);
		Page<Account> result = accountRepository.findBySearchTerm(search, request);

		return result;
	}

	// 用户管理->修改
	@GetMapping("/account/{id}/edit")
	public String account_edit(@PathVariable("id") Long id, Model model) {
		model.addAttribute("account", accountRepository.findOneById(id));
		return "admin/account/edit";
	}

	// 用户管理->新建
	@GetMapping("/account/add")
	public String account_add(Model model) {
		model.addAttribute("account", new Account());
		return "admin/account/edit";
	}

	// 用户管理->删除
	@GetMapping("/account/{id}/delete")
	public @ResponseBody Boolean account_delete(@PathVariable("id") Long id, Model model) {
		Account account = accountRepository.findOneById(id);
		accountRepository.delete(account);
		return true;
	}

	// 用户修改
	@PostMapping("/account/update")
	public @ResponseBody Account account_update_ajax(@RequestParam Map<String, String> requestParams) {
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

		if (password != null) {
			account.setPassword(password);
			account = accountService.save(account);
		} else {
			account = accountRepository.save(account);
		}

		return account;
	}

	// 组织架构管理
	@GetMapping("/unit")
	public String unit() {
		return "admin/unit/index";
	}

	// 组织架构管理->下级组织列表
	@GetMapping("/unit/{parent_id}/children")
	public @ResponseBody List<Map<String, Object>> unit_list_ajax(@PathVariable("parent_id") Long parent_id) {
		List<Unit> children = unitRepository.findByParentId(parent_id);

		Unit temp;
		List<Unit> tempChildren;

		Map<String, Object> row = new HashMap<>();
		List<Map<String, Object>> response = new ArrayList<>();
		for (int i = 0; i < children.size(); i++) {

			temp = children.get(i);
			tempChildren = unitRepository.findByParentId(temp.getId());
			row = new HashMap<>();
			row.put("id", temp.getId());
			row.put("text", temp.getName());
			row.put("children", tempChildren.size() > 0 ? true : false);
			response.add(row);
		}

		return response;
	}

	// 组织架构管理->删除
	@GetMapping("/unit/{id}/delete")
	public @ResponseBody Boolean unit_delete_ajax(@PathVariable("id") Long id) {
		Unit unit = unitRepository.findOneById(id);

		String childrenUnitIds = unitRepository.findChildrenByGroupId(id);

		unitRepository.delete(unit);
		unitRepository.deleteByChildIds(childrenUnitIds.split(","));

		return true;
	}

	// 组织架构管理->改名
	@GetMapping("/unit/{id}/rename/{name}")
	public @ResponseBody Unit unit_rename_ajax(@PathVariable("id") Long id, @PathVariable("name") String name) {
		Unit unit = unitRepository.findOneById(id);
		unit.setName(name);
		unit = unitRepository.save(unit);
		return unit;
	}

	// 组织架构管理->移动
	@GetMapping("/unit/{id}/move/{parent_id}")
	public @ResponseBody Unit unit_move_ajax(@PathVariable("id") Long id, @PathVariable("parent_id") Long parent_id) {
		Unit unit = unitRepository.findOneById(id);
		unit.setParentId(parent_id);
		unit = unitRepository.save(unit);
		return unit;
	}

	// 组织架构管理->新建
	@GetMapping("/unit/add/{parent_id}/{name}")
	public @ResponseBody Unit unit_add_ajax(@PathVariable("parent_id") Long parentId,
			@PathVariable("name") String name) {
		Unit unit = new Unit(name, parentId);
		unitRepository.save(unit);
		return unit;
	}

	// 权限组管理->列表
	@GetMapping("/permission_group_ajax")
	public @ResponseBody Page<PermissionGroup> permission_group_list_ajax(
			@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "3"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);
		Page<PermissionGroup> result = permissionGroupRepository.findBySearchTerm(search, request);

		return result;
	}

	// 权限组管理
	@GetMapping("/permission_group")
	public String permission_group(Model model) {
		return "admin/permission_group/list";
	}

	// 权限组管理->修改
	@GetMapping("/permission_group/{id}/edit")
	public String permission_group_edit(@PathVariable("id") Long id, Model model) {
		PermissionGroup temp = permissionGroupRepository.findOneById(id);
		model.addAttribute("permission_group", temp);

		List<AccountSearchItem> accounts = permissionGroupRepository.findAccountsInGroupById(id);
		List<String> accountList = new ArrayList<>();
		for (int i = 0; i < accounts.size(); i++) {
			AccountSearchItem item = accounts.get(i);
			logger.info(item.toString());
			accountList.add(item.getRealname() + "(" + item.getUsername() + ")");
		}
		model.addAttribute("accounts", StringUtils.join(accountList, ","));
		return "admin/permission_group/edit";
	}

	// 权限组管理->修改
	@GetMapping("/permission_group/{id}/edit_perm")
	public String permission_group_edit_perm(@PathVariable("id") Long id, Model model) {
		PermissionGroup temp = permissionGroupRepository.findOneById(id);
		List<Function> functionList = functionRepository.findAll();
		List<FunctionAction> functionActionList = functionActionRepository.findAll();
		List<IGroupFunctionUnit> unitList = permissionGroupFunctionUnitRepository.findUnitsByGroupId(id);

		for (int i = 0; i < functionList.size(); i++) {
			Function tempF = functionList.get(i);
			List<Action> actionList = tempF.getActions();
			for (int j = 0; j < actionList.size(); j++) {
				Action tempA = actionList.get(j);
				tempA.setAvailable(false);
				for (int k = 0; k < temp.getFunctionActions().size(); k++) {
					FunctionAction tempG = temp.getFunctionActions().get(k);

					if (tempG.getFunctionId() == tempF.getId() && tempG.getActionId() == tempA.getId()) {
						tempA.setAvailable(true);
						break;
					}
				}
				for (int k = 0; k < functionActionList.size(); k++) {
					FunctionAction tempG = functionActionList.get(k);
					if (tempG.getFunctionId() == tempF.getId() && tempG.getActionId() == tempA.getId()) {
						tempA.setFunctionActionId(tempG.getId());
						break;
					}
				}
			}

			for (int j = 0; j < unitList.size(); j++) {
				IGroupFunctionUnit tempU = unitList.get(j);
				if (tempU.getFunctionId() == tempF.getId()) {
					tempF.setUnits(tempU.getUnits());
					break;
				}
			}

		}

		model.addAttribute("permission_group", temp);
		model.addAttribute("function_list", functionList);
		return "admin/permission_group/edit_perm";
	}

	// 权限组管理->新建
	@GetMapping("/permission_group/add")
	public String permission_group_add(Model model) {
		model.addAttribute("permission_group", new PermissionGroup());
		return "admin/permission_group/edit";
	}

	// 权限组管理->更新
	@PostMapping("/permission_group/update")
	public @ResponseBody PermissionGroup permission_group_update_ajax(@RequestParam Map<String, String> requestParams) {

		String name = requestParams.get("name");
		String description = requestParams.get("description");
		String id = requestParams.get("id");
		String accounts = requestParams.get("accounts");

		PermissionGroup group;
		if (id != null && !id.isEmpty()) {
			group = permissionGroupRepository.findOneById(Long.parseLong(id));
		} else {
			group = new PermissionGroup();
		}

		group.setName(name);
		group.setDescription(description);

		group = permissionGroupRepository.save(group);

		String[] account_list = StringUtils.split(accounts, ",");
		List<String> usernameList = new ArrayList<>();
		for (int i = 0; i < account_list.length; i++) {
			String item = account_list[i];
			if (item.lastIndexOf("(") >= 0 && item.lastIndexOf(")") >= 0) {
				usernameList.add(item.substring(item.lastIndexOf("(") + 1, item.lastIndexOf(")")));
			}
		}

		List<AccountSearchItem> accountItems = accountRepository.findAccountsByUsernames(usernameList);

		logger.info(StringUtils.join(usernameList, ","));
		logger.info(accountItems.toString());
		permissionGroupUserReopsitory.deleteByGroupId(group.getId());
		for (int i = 0; i < accountItems.size(); i++) {
			permissionGroupUserReopsitory.save(new PermissionGroupUser(group.getId(), accountItems.get(i).getId()));
		}

		return group;
	}

	// 权限组管理->更新
	@PostMapping("/permission_group/update/function")
	public @ResponseBody PermissionGroup permission_group_update_function_ajax(@RequestParam(value = "id") Long groupId,
			@RequestParam(value = "functions[]", required = false) Long[] functions,
			@RequestParam Map<String, String> units) {

		logger.info(StringUtils.join(functions, ","));

		permissionGroupFunctionActionRepository.deleteByGroupId(groupId);
		PermissionGroup group = permissionGroupRepository.findOneById(groupId);
		if (functions != null) {
			List<FunctionAction> list = functionActionRepository.findAllById(Arrays.asList(functions));
			logger.info(list.toString());
			group.setFunctionActions(list);

			group = permissionGroupRepository.save(group);
		}

		permissionGroupFunctionUnitRepository.deleteByGroupId(groupId);

		PermissionGroupFunctionUnit tempUnit;
		Iterator<Entry<String, String>> it = units.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> temp = it.next();
			if (temp.getKey().startsWith("units_") && temp.getValue() != null && !temp.getValue().isEmpty()) {
				String function_id = temp.getKey().substring("units_".length());
				String[] unit_ids = temp.getValue().split(",");

				for (int i = 0; i < unit_ids.length; i++) {
					if (unit_ids[i] != null && !unit_ids[i].isEmpty()) {
						tempUnit = new PermissionGroupFunctionUnit(groupId, Long.parseLong(function_id),
								Long.parseLong(unit_ids[i]));
						permissionGroupFunctionUnitRepository.save(tempUnit);
					}
				}
			}
		}

		return group;
	}

	// 权限组管理->删除
	@GetMapping("/permission_group/{id}/delete")
	public @ResponseBody Boolean permission_group_delete_ajax(@PathVariable("id") Long id) {

		PermissionGroup temp = permissionGroupRepository.findOneById(id);

		permissionGroupRepository.delete(temp);

		return true;
	}

}
