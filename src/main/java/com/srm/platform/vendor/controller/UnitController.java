package com.srm.platform.vendor.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.Unit;
import com.srm.platform.vendor.repository.UnitRepository;
import com.srm.platform.vendor.utility.UnitNode;

@Controller
@RequestMapping(path = "/unit")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UnitController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UnitRepository unitRepository;

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
	@GetMapping("/{id}/delete")
	public @ResponseBody Boolean delete_ajax(@PathVariable("id") Long id) {
		Unit unit = unitRepository.findOneById(id);

		String childrenUnitIds = unitRepository.findChildrenByGroupId(id);

		unitRepository.delete(unit);
		unitRepository.deleteByChildIds(childrenUnitIds.split(","));

		return true;
	}

	// 组织架构管理->改名
	@GetMapping("/{id}/rename/{name}")
	public @ResponseBody Unit rename_ajax(@PathVariable("id") Long id, @PathVariable("name") String name) {
		Unit unit = unitRepository.findOneById(id);
		unit.setName(name);
		unit = unitRepository.save(unit);
		return unit;
	}

	// 组织架构管理->移动
	@GetMapping("/{id}/move/{parent_id}")
	public @ResponseBody Unit move_ajax(@PathVariable("id") Long id, @PathVariable("parent_id") Long parent_id) {
		Unit unit = unitRepository.findOneById(id);
		unit.setParentId(parent_id);
		unit = unitRepository.save(unit);
		return unit;
	}

	// 组织架构管理->新建
	@GetMapping("/add/{parent_id}/{name}")
	public @ResponseBody Unit add_ajax(@PathVariable("parent_id") Long parentId, @PathVariable("name") String name) {
		Unit unit = new Unit(name, parentId);
		unitRepository.save(unit);
		return unit;
	}

	@ResponseBody
	@RequestMapping(value = "/tree", produces = "application/json")
	public UnitNode tree() {
		List<Unit> units = unitRepository.findAll(Sort.by(Direction.ASC, "id"));

		UnitNode root = null, tempNode;
		Unit temp;
		for (int i = 0; i < units.size(); i++) {
			temp = units.get(i);
			tempNode = new UnitNode(temp.getId(), temp.getName(), temp.getParentId());
			if (i == 0) {
				root = tempNode;
			} else {
				root.addNode(tempNode);
			}
		}

		return root;
	}

}
