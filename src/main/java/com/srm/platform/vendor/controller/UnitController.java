package com.srm.platform.vendor.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.Unit;
import com.srm.platform.vendor.repository.UnitRepository;

@Controller
@RequestMapping(path = "/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UnitController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UnitRepository unitRepository;

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

}
