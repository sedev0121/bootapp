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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.Notice;
import com.srm.platform.vendor.repository.NoticeRepository;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.NoticeSearchResult;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/alert")
public class AlertController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NoticeRepository noticeRepository;

	// 用户管理->列表
	@GetMapping({ "/", "" })
	public String index(Model model) {
		return "alert/list";
	}

	// 用户管理->修改
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable("id") Long id, Model model) {
		Notice notice = noticeRepository.findOneById(id);
		if (notice == null || notice.getType() != Constants.NOTICE_TYPE_ALERT
				|| notice.getState() != Constants.NOTICE_STATE_PUBLISH)
			show404();

		if (!isVisibleNotice(id)) {
			show403();
		}

		setReadDate(id);
		model.addAttribute("notice", notice);
		return "alert/edit";
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

		String selectQuery = "SELECT distinct a.*, b.realname create_name, c.name create_unitname, d.realname verify_name ";
		String countQuery = "select count(distinct a.id) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "FROM notice a left join account b on a.create_account=b.id left join unit c on a.create_unit=c.id "
				+ "left join account d on d.id=a.verify_account left join notice_read e on a.id=e.notice_id where type=3 ";

		Map<String, Object> params = new HashMap<>();

		bodyQuery += " and e.to_account_id=:to_account";
		params.put("to_account", this.getLoginAccount().getId());

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

}
