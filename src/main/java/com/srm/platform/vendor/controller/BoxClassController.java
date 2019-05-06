package com.srm.platform.vendor.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

import com.srm.platform.vendor.model.Box;
import com.srm.platform.vendor.model.BoxClass;
import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.model.InventoryClass;
import com.srm.platform.vendor.model.PurchaseInDetail;
import com.srm.platform.vendor.model.StatementDetail;
import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.searchitem.InventorySearchItem;
import com.srm.platform.vendor.utility.AccountPermission;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/classofbox")
@PreAuthorize("hasAuthority('基础资料-查看列表')")
public class BoxClassController extends CommonController {

	@PersistenceContext
	private EntityManager em;


	@GetMapping({ "/", "" })
	public String index() {
		return "boxclass/index";
	}

	@Transactional
	@PostMapping("/update")
	public @ResponseBody GenericJsonResponse<BoxClass> update_ajax(@RequestParam Map<String, String> requestParams) {

		GenericJsonResponse<BoxClass> jsonResponse;
		
		String idStr = requestParams.get("id");
		String code = requestParams.get("code");
		String name = requestParams.get("name");
		
		BoxClass main;
		if (idStr == null || idStr.isEmpty()) {
			main = boxClassRepository.findOneByCode(code);
			if (main != null) {
				jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, "分类编码重复", null);
				return jsonResponse;
				
			} else {
				main = new BoxClass();
				main.setCode(code);
				main.setName(name);
				main = boxClassRepository.save(main);
			}
		} else {
			main = boxClassRepository.findOneByCode(code);
			Long id = Long.valueOf(idStr);
			if (main != null && main.getId() != id) {
				jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, "分类编码重复", null);
				return jsonResponse;
			} else {
				main = boxClassRepository.findOneById(id);	
				main.setCode(code);
				main.setName(name);
				main = boxClassRepository.save(main);
			}			
		}
		
		jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, main);	

		return jsonResponse;
	}
	
	@GetMapping("/{id}/delete")
	public @ResponseBody GenericJsonResponse<BoxClass> delete(@PathVariable("id") Long id) {
		GenericJsonResponse<BoxClass> jsonResponse;
		BoxClass main = boxClassRepository.findOneById(id);
		if (main != null) {
			List<Box> boxList = boxRepository.findByBoxClassId(id);
			if (boxList.size() > 0) {
				jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, "该箱码分类已被使用", null);
			} else {
				boxClassRepository.delete(main);
				jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, null);	
			}
		} else {
			jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, "找不到该箱码分类", null);
			
		}

		return jsonResponse;
	}
	
	
	@GetMapping("/all")
	public @ResponseBody List<Map<String, Object>> list_ajax() {
		List<BoxClass> children = boxClassRepository.findAllNodes();

		BoxClass temp;

		Map<String, Object> row = new HashMap<>();
		List<Map<String, Object>> response = new ArrayList<>();
		for (int i = 0; i < children.size(); i++) {

			temp = children.get(i);
			row = new HashMap<>();
			row.put("id", temp.getId());
			row.put("code", temp.getCode());
			row.put("name", temp.getName());
			row.put("text", String.format("(%s) %s", temp.getCode(), temp.getName()));
			row.put("children", false);
			response.add(row);
		}

		return response;
	}

}