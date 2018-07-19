package com.srm.platform.vendor.controller;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
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
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.NoticeRepository;
import com.srm.platform.vendor.utility.NoticeSearchResult;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/notice")
public class NoticeController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NoticeRepository noticeRepository;

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

		String selectQuery = "SELECT a.*, b.realname create_name, c.name create_unitname ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "FROM notice a left join account b on a.create_account=b.id left join unit c on a.create_unit=c.id where 1=1 ";

		List<String> unitList = this.getDefaultUnitList();
		Map<String, Object> params = new HashMap<>();

		if (isVendor()) {
			Vendor vendor = this.getLoginAccount().getVendor();
			String vendorStr = vendor == null ? "0" : vendor.getCode();

		} else {
			// bodyQuery += " and c.unit_id in :unitList";
			// params.put("unitList", unitList);
		}

		if (!search.trim().isEmpty()) {
			bodyQuery += " and (a.title like CONCAT('%',:search, '%') or a.content like CONCAT('%',:search, '%')) ";
			params.put("search", search.trim());
		}

		if (startDate != null) {
			bodyQuery += " and a.create_date>=:startDate";
			params.put("startDate", startDate);
		}
		if (endDate != null) {
			bodyQuery += " and a.create_date<:endDate";
			params.put("endDate", endDate);
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
		if (notice == null)
			show404();

		model.addAttribute("notice", notice);
		return "notice/edit";
	}

	// 用户管理->新建
	@GetMapping("/add")
	public String add(Model model) {
		model.addAttribute("notice", new Notice());
		return "notice/edit";
	}

	// 用户管理->删除
	@GetMapping("/{id}/delete")
	public @ResponseBody Boolean delete(@PathVariable("id") Long id, Model model) {
		Notice notice = noticeRepository.findOneById(id);
		noticeRepository.delete(notice);
		return true;
	}

	// 用户修改
	@Transactional
	@PostMapping("/update")
	public @ResponseBody Notice update_ajax(@RequestParam Map<String, String> requestParams) {
		String id = requestParams.get("id");
		String title = requestParams.get("title");
		String content = requestParams.get("content");

		Notice notice = new Notice();

		if (id != null && !id.isEmpty()) {
			notice = noticeRepository.findOneById(Long.parseLong(id));

		}

		notice.setTitle(title);
		notice.setContent(content);

		notice.setUnit(this.getLoginAccount().getUnit());
		notice.setAccount(this.getLoginAccount());

		notice = noticeRepository.save(notice);

		return notice;
	}

}
