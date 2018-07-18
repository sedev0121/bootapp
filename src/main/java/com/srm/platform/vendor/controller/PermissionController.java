package com.srm.platform.vendor.controller;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import com.srm.platform.vendor.model.Action;
import com.srm.platform.vendor.model.Function;
import com.srm.platform.vendor.model.FunctionAction;
import com.srm.platform.vendor.model.PermissionGroup;
import com.srm.platform.vendor.model.PermissionGroupFunctionUnit;
import com.srm.platform.vendor.model.PermissionGroupUser;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.FunctionActionRepository;
import com.srm.platform.vendor.repository.FunctionRepository;
import com.srm.platform.vendor.repository.PermissionGroupFunctionActionRepository;
import com.srm.platform.vendor.repository.PermissionGroupFunctionUnitRepository;
import com.srm.platform.vendor.repository.PermissionGroupRepository;
import com.srm.platform.vendor.repository.PermissionGroupUserRepository;
import com.srm.platform.vendor.utility.AccountSearchItem;
import com.srm.platform.vendor.utility.IGroupFunctionUnit;

@Controller
@RequestMapping(path = "/permission_group")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class PermissionController extends CommonController {
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
	private PermissionGroupUserRepository permissionGroupUserReopsitory;

	// 权限组管理->列表
	@GetMapping("/list")
	public @ResponseBody Page<PermissionGroup> list_ajax(@RequestParam Map<String, String> requestParams) {
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
	@GetMapping({ "/", "" })
	public String permission_group(Model model) {
		return "admin/permission_group/list";
	}

	// 权限组管理->修改
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable("id") Long id, Model model) {
		PermissionGroup temp = permissionGroupRepository.findOneById(id);

		if (temp == null)
			show404();

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
	@GetMapping("/{id}/edit_perm")
	public String edit_perm(@PathVariable("id") Long id, Model model) {
		PermissionGroup temp = permissionGroupRepository.findOneById(id);

		if (temp == null)
			show404();

		List<Function> functionList = functionRepository.findAll();
		List<FunctionAction> functionActionList = functionActionRepository.findAll();
		List<IGroupFunctionUnit> unitList = permissionGroupFunctionUnitRepository.findUnitsByGroupId(id);

		for (int i = 0; i < functionList.size(); i++) {
			Function tempF = functionList.get(i);
			List<Action> actionList = tempF.getActions();
			List<Action> newActionList = new ArrayList<>();
			for (int j = 0; j < actionList.size(); j++) {
				Action tempA = actionList.get(j);
				for (int k = 0; k < temp.getFunctionActions().size(); k++) {
					FunctionAction tempG = temp.getFunctionActions().get(k);

					if (tempG.getFunctionId() == tempF.getId() && tempG.getActionId() == tempA.getId()) {
						tempA = Action.clone(tempA);
						tempA.setAvailable(true);
						break;
					}
				}
				for (int k = 0; k < functionActionList.size(); k++) {
					FunctionAction tempG = functionActionList.get(k);
					if (tempG.getFunctionId() == tempF.getId() && tempG.getActionId() == tempA.getId()) {
						tempA = Action.clone(tempA);
						tempA.setFunctionActionId(tempG.getId());
						break;
					}
				}
				newActionList.add(tempA);
			}
			tempF.setActions(newActionList);

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
	@GetMapping("/add")
	public String add(Model model) {
		model.addAttribute("permission_group", new PermissionGroup());
		return "admin/permission_group/edit";
	}

	// 权限组管理->更新
	@Transactional
	@PostMapping("/update")
	public @ResponseBody PermissionGroup update_ajax(@RequestParam Map<String, String> requestParams) {

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
	@Transactional
	@PostMapping("/update/function")
	public @ResponseBody PermissionGroup update_function_ajax(@RequestParam(value = "id") Long groupId,
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
	@Transactional
	@GetMapping("/{id}/delete")
	public @ResponseBody Boolean delete_ajax(@PathVariable("id") Long id) {

		PermissionGroup temp = permissionGroupRepository.findOneById(id);

		permissionGroupRepository.delete(temp);

		return true;
	}

}
