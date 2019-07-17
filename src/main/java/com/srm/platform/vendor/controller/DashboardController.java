package com.srm.platform.vendor.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.srm.platform.vendor.searchitem.NoticeSearchResult;

@Controller
@RequestMapping(path = "/")

public class DashboardController extends CommonController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping({ "/", "" })
	@PreAuthorize("isAuthenticated()")
	public String index(Model model) {
		List<NoticeSearchResult> noticeList = getLastNotice();
		List<NoticeSearchResult> alertList = getLastAlert();
		List<NoticeSearchResult> messageList = getLastMessage();

		model.addAttribute("noticeList", noticeList);
		model.addAttribute("alertList", alertList);
		model.addAttribute("messageList", messageList);
		return "index";
	}

	@SuppressWarnings("unchecked")
	private List<NoticeSearchResult> getLastNotice() {
		String selectQuery = "SELECT distinct a.*, b.realname create_name, '' create_unitname, d.realname verify_name, e.read_date FROM notice a left join account b on a.create_account=b.id "
				+ "left join account d on d.id=a.verify_account left join notice_read e on a.id=e.notice_id where type=1 and a.state=3 ";

		Map<String, Object> params = new HashMap<>();

		selectQuery += " and e.to_account_id=:to_account ";
		params.put("to_account", this.getLoginAccount().getId());

		selectQuery += "order by verify_date desc ";
		Query q = em.createNativeQuery(selectQuery, "NoticeSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		return q.setFirstResult(0).setMaxResults(5).getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<NoticeSearchResult> getLastMessage() {
		String selectQuery = "SELECT distinct a.*, b.realname create_name, '' create_unitname, d.realname verify_name, e.read_date FROM notice a left join account b on a.create_account=b.id "
				+ "left join account d on d.id=a.verify_account left join notice_read e on a.id=e.notice_id where type=2 and a.state=3 ";

		Map<String, Object> params = new HashMap<>();

		selectQuery += " and e.to_account_id=:to_account ";
		params.put("to_account", this.getLoginAccount().getId());

		selectQuery += "order by create_date desc ";
		Query q = em.createNativeQuery(selectQuery, "NoticeSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		return q.setFirstResult(0).setMaxResults(5).getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<NoticeSearchResult> getLastAlert() {
		String selectQuery = "SELECT distinct a.*, b.realname create_name, '' create_unitname, d.realname verify_name, e.read_date FROM notice a left join account b on a.create_account=b.id "
				+ "left join account d on d.id=a.verify_account left join notice_read e on a.id=e.notice_id where type=3 and a.state=3 ";

		Map<String, Object> params = new HashMap<>();

		selectQuery += " and e.to_account_id=:to_account ";
		params.put("to_account", this.getLoginAccount().getId());

		selectQuery += "order by create_date desc ";
		Query q = em.createNativeQuery(selectQuery, "NoticeSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		return q.setFirstResult(0).setMaxResults(5).getResultList();

	}

}
