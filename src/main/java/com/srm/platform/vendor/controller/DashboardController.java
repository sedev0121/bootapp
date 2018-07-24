package com.srm.platform.vendor.controller;

import java.util.Date;
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
import com.srm.platform.vendor.utility.MessageSearchResult;
import com.srm.platform.vendor.utility.NoticeSearchResult;
import com.srm.platform.vendor.utility.PurchaseOrderDetailSearchResult;
import com.srm.platform.vendor.utility.Utils;

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
		List<PurchaseOrderDetailSearchResult> alertList = getLastAlert();
		List<MessageSearchResult> messageList = getLastMessage();

		model.addAttribute("noticeList", noticeList);
		model.addAttribute("alertList", alertList);
		model.addAttribute("messageList", messageList);
		return "index";
	}

	@SuppressWarnings("unchecked")
	private List<NoticeSearchResult> getLastNotice() {
		String selectQuery = "SELECT a.*, b.realname create_name, c.name create_unitname, c.name verify_name FROM notice a left join account b on a.create_account=b.id left join unit c on a.create_unit=c.id where 1=1 ";

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

	@SuppressWarnings("unchecked")
	private List<MessageSearchResult> getLastMessage() {
		String selectQuery = "SELECT * FROM message where 1=1 ";

		String orderBy = " order by create_date desc ";

		Map<String, Object> params = new HashMap<>();

		selectQuery += orderBy;
		Query q = em.createNativeQuery(selectQuery, "MessageSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		return q.setFirstResult(0).setMaxResults(5).getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<PurchaseOrderDetailSearchResult> getLastAlert() {
		String selectQuery = "select a.*, d.code vendorcode, (a.quantity-ifnull(a.shipped_quantity,0)) remain_quantity, d.name vendorname, c.name inventoryname, c.specs, e.name unitname "
				+ "from purchase_order_detail a left join purchase_order_main b on a.code = b.code "
				+ "left join inventory c on a.inventorycode=c.code left join vendor d on b.vencode=d.code "
				+ "left join measurement_unit e on c.main_measure=e.code where b.srmstate=2 and (a.quantity-ifnull(a.shipped_quantity,0))>0 ";

		String orderBy = " order by confirmdate desc ";

		List<String> unitList = this.getDefaultUnitList();
		Map<String, Object> params = new HashMap<>();

		if (isVendor()) {
			Vendor vendor = this.getLoginAccount().getVendor();
			String vendorStr = vendor == null ? "0" : vendor.getCode();
			selectQuery += " and d.code= :vendor";
			params.put("vendor", vendorStr);
		} else {
			selectQuery += " and d.unit_id in :unitList";
			params.put("unitList", unitList);
		}

		selectQuery += " and a.confirmdate<:confirmdate";
		params.put("confirmdate", Utils.getAlertDate(new Date()));

		selectQuery += orderBy;
		Query q = em.createNativeQuery(selectQuery, "PurchaseOrderDetailSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		return q.setFirstResult(0).setMaxResults(5).getResultList();

	}

}
