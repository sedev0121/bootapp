package com.srm.platform.vendor.controller;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
import com.srm.platform.vendor.model.PermissionGroupUser;
import com.srm.platform.vendor.model.ProvideClass;
import com.srm.platform.vendor.model.Unit;
import com.srm.platform.vendor.model.UnitProvide;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.UnitProvideRepository;
import com.srm.platform.vendor.repository.UnitRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.utility.AccountSaveForm;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.SearchItem;
import com.srm.platform.vendor.utility.UnitNode;
import com.srm.platform.vendor.utility.UnitSaveForm;
import com.srm.platform.vendor.utility.UploadFileHelper;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/unit")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UnitController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UnitRepository unitRepository;

	@Autowired
	private UnitProvideRepository unitProvideRepository;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private AccountRepository accountRepository;

	// 组织架构管理
	@GetMapping({ "/", "" })
	public String unit() {
		return "admin/unit/index";
	}

	// 组织架构管理->下级组织列表
	@GetMapping("/{parent_id}/children")
	public @ResponseBody List<Map<String, Object>> list_ajax(@PathVariable("parent_id") Long parent_id) {
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
	@Transactional
	@GetMapping("/{id}/delete")
	public @ResponseBody GenericJsonResponse<Unit> delete_ajax(@PathVariable("id") Long id) {
		Unit unit = unitRepository.findOneById(id);

		String childrenUnitIds = unitRepository.findChildrenByGroupId(id);
		List<String> childUnitList = new ArrayList<>();
		if (childrenUnitIds != null) {
			childUnitList.addAll(Arrays.asList(childrenUnitIds.split(",")));
		}
		childUnitList.add(String.valueOf(unit.getId()));

		List<Account> accountList = accountRepository.findAccountsByUnitIdList(childUnitList);
		if (accountList.size() > 0) {
			GenericJsonResponse<Unit> response = new GenericJsonResponse<>(GenericJsonResponse.FAILED,
					accountList.size() + "个用户在使用该组织或下属组织。", null);
			return response;
		}

		unitProvideRepository.deleteByUnitId(unit.getId());
		unitRepository.delete(unit);
		if (childrenUnitIds != null) {
			unitRepository.deleteByChildIds(childrenUnitIds.split(","));
		}

		
		return new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, null);
	}

	// 组织架构管理->改名
	@Transactional
	@GetMapping("/{id}/rename/{name}")
	public @ResponseBody Unit rename_ajax(@PathVariable("id") Long id, @PathVariable("name") String name) {
		Unit unit = unitRepository.findOneById(id);
		unit.setName(name);
		unit = unitRepository.save(unit);
		return unit;
	}

	// 组织架构管理->移动
	@Transactional
	@GetMapping("/{id}/move/{parent_id}")
	public @ResponseBody Unit move_ajax(@PathVariable("id") Long id, @PathVariable("parent_id") Long parent_id) {
		Unit unit = unitRepository.findOneById(id);
		unit.setParentId(parent_id);
		unit = unitRepository.save(unit);
		return unit;
	}

	// 组织架构管理->新建
	@Transactional
	@GetMapping("/add/{parent_id}/{name}")
	public @ResponseBody Unit add_ajax(@PathVariable("parent_id") Long parentId, @PathVariable("name") String name) {
		Unit unit = new Unit(name, parentId);
		unitRepository.save(unit);
		return unit;
	}

	@ResponseBody
	@RequestMapping(value = "/tree", produces = "application/json")
	public UnitNode tree() {
		Unit rootUnit = unitRepository.findOneById(1L);

		UnitNode rootNode = new UnitNode(rootUnit.getId(), rootUnit.getName(), rootUnit.getParentId());

		rootNode = this.setChildNode(rootNode);

		return rootNode;
	}

	private UnitNode setChildNode(UnitNode node) {
		List<Unit> units = unitRepository.findByParentId(node.getId());

		if (!units.isEmpty()) {
			for (Unit unit : units) {
				UnitNode tempNode = new UnitNode(unit.getId(), unit.getName(), unit.getParentId());
				node.addNode(tempNode);
				this.setChildNode(tempNode);
			}
		}
		return node;

	}

	@ResponseBody
	@RequestMapping(value = "/get/{id}", produces = "application/json")
	public Map<String, Object> get_ajax(@PathVariable("id") Long id) {
		Unit unit = unitRepository.findOneById(id);
		List<ProvideClass> provideClassList = provideClassRepository.findProvideClassesByUnitId(id);
		Map<String, Object> response = new HashMap();
		response.put("unit", unit);
		response.put("provide_classes", provideClassList);
		return response;

	}

	// 用户修改
	@Transactional
	@PostMapping("/update")
	public @ResponseBody GenericJsonResponse<Unit> update_ajax(UnitSaveForm unitSaveForm) {
		Unit unit = unitRepository.findOneById(unitSaveForm.getId());
		unit.setName(unitSaveForm.getName());
		unitRepository.save(unit);

		unitProvideRepository.deleteByUnitId(unit.getId());

		List<Long> provideClassIdList = unitSaveForm.getProvideclasses();
		if (provideClassIdList != null) {
			for (Long id : provideClassIdList) {
				UnitProvide temp = new UnitProvide(id, unit.getId());
				unitProvideRepository.save(temp);
			}
		}

		GenericJsonResponse<Unit> response = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, unit);

		return response;
	}

}
