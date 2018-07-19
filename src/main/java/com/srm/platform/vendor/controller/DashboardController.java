package com.srm.platform.vendor.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.utility.NoticeSearchResult;

@Controller
@RequestMapping(path = "/")

public class DashboardController extends CommonController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Autowired
	private HttpSession httpSession;

	@GetMapping({ "/", "" })
	@PreAuthorize("isAuthenticated()")
	public String index(Model model) {
		List<NoticeSearchResult> noticeList = getLastNotice();

		model.addAttribute("noticeList", noticeList);
		return "index";
	}

	@SuppressWarnings("unchecked")
	private List<NoticeSearchResult> getLastNotice() {
		String selectQuery = "SELECT a.*, b.realname create_name, c.name create_unitname FROM notice a left join account b on a.create_account=b.id left join unit c on a.create_unit=c.id where 1=1 ";

		String orderBy = " order by create_date desc ";

		List<String> unitList = this.getDefaultUnitList();
		Map<String, Object> params = new HashMap<>();

		if (isVendor()) {
			Vendor vendor = this.getLoginAccount().getVendor();
			String vendorStr = vendor == null ? "0" : vendor.getCode();

		} else {
			// bodyQuery += " and c.unit_id in :unitList";
			// params.put("unitList", unitList);
		}

		selectQuery += orderBy;
		Query q = em.createNativeQuery(selectQuery, "NoticeSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		return q.setFirstResult(0).setMaxResults(5).getResultList();
	}

}
