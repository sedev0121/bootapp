package com.srm.platform.vendor.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

import com.srm.platform.vendor.model.Notice;
import com.srm.platform.vendor.model.NoticeClass;
import com.srm.platform.vendor.utility.GenericJsonResponse;

@Controller
@RequestMapping(path = "/classofnotice")
@PreAuthorize("hasAuthority('采购动态-查看列表')")
public class NoticeClassController extends CommonController {

	@PersistenceContext
	private EntityManager em;


	@GetMapping({ "/", "" })
	public String index(Model model) {
		model.addAttribute("classId", "-1");
		return "noticeclass/index";
	}


	@Transactional
	@PostMapping("/update")
	public @ResponseBody GenericJsonResponse<NoticeClass> update_ajax(@RequestParam Map<String, String> requestParams) {

		GenericJsonResponse<NoticeClass> jsonResponse;
		
		String idStr = requestParams.get("id");
		String name = requestParams.get("name");
		String rankStr = requestParams.get("rank");
		
		NoticeClass main;
		if (idStr == null || idStr.isEmpty()) {
			main = new NoticeClass();
			main.setName(name);
			main.setRank(Integer.parseInt(rankStr));
			main = noticeClassRepository.save(main);
		} else {
			main = noticeClassRepository.findOneById(Long.parseLong(idStr));	
			main.setName(name);
			main.setRank(Integer.parseInt(rankStr));
			main = noticeClassRepository.save(main);			
		}
		
		jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, main);	

		return jsonResponse;
	}
	
	@GetMapping("/{id}/delete")
	public @ResponseBody GenericJsonResponse<NoticeClass> delete(@PathVariable("id") Long id) {
		GenericJsonResponse<NoticeClass> jsonResponse;
		NoticeClass main = noticeClassRepository.findOneById(id);
		if (main != null) {
			List<Notice> noticeList = noticeRepository.findAllByClassId(id);
			if (noticeList.size() > 0) {
				jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, "该采购动态分类已被使用", null);
			} else {
				noticeClassRepository.delete(main);
				jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, null);	
			}
		} else {
			jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, "找不到该采购动态分类", null);
			
		}

		return jsonResponse;
	}
	
	
	@GetMapping("/all")
	public @ResponseBody List<Map<String, Object>> list_ajax() {
		List<NoticeClass> children = noticeClassRepository.findAllNodes();

		NoticeClass temp;

		Map<String, Object> row = new HashMap<>();
		List<Map<String, Object>> response = new ArrayList<>();
		for (int i = 0; i < children.size(); i++) {

			temp = children.get(i);
			row = new HashMap<>();
			row.put("id", temp.getId());
			row.put("name", temp.getName());
			row.put("text", temp.getName());
			row.put("rank", temp.getRank());
			row.put("children", false);
			response.add(row);
		}

		return response;
	}

}
