package com.srm.platform.vendor.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Notice;
import com.srm.platform.vendor.model.NoticeRead;
import com.srm.platform.vendor.model.PurchaseOrderMain;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.NoticeReadRepository;
import com.srm.platform.vendor.repository.NoticeRepository;
import com.srm.platform.vendor.repository.PurchaseOrderMainRepository;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.PurchaseOrderDetailSearchResult;

@RestController
@RequestMapping(path = "/check")
public class CheckController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private PurchaseOrderMainRepository purchaseOrderMainRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private NoticeRepository noticeRepository;

	@Autowired
	private NoticeReadRepository noticeReadRepository;

	@GetMapping({ "", "/" })
	public boolean index() {

		return alert();
	}

	@GetMapping({ "/alert" })
	public boolean alert() {
		String selectQuery = "select a.*, d.code vendorcode, (a.quantity-ifnull(a.shipped_quantity,0)) remain_quantity, d.name vendorname, c.name inventoryname, c.specs, e.name unitname "
				+ "from purchase_order_detail a left join purchase_order_main b on a.code = b.code "
				+ "left join inventory c on a.inventorycode=c.code left join vendor d on b.vencode=d.code "
				+ "left join measurement_unit e on c.main_measure=e.code where b.state<>2 and b.srmstate=2 and (a.quantity-ifnull(a.shipped_quantity,0))>0 and arrivedate<:today ";

		Query q = em.createNativeQuery(selectQuery, "PurchaseOrderDetailSearchResult");
		q.setParameter("today", new Date());

		List<PurchaseOrderDetailSearchResult> list = q.getResultList();

		for (PurchaseOrderDetailSearchResult item : list) {

			long diffInMillies = Math.abs(new Date().getTime() - item.getArrivedate().getTime());
			long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

			String alertPattern = "订单【%s】【%s】交货已逾期%d天，请及时处理!";
			String alert = String.format(alertPattern, item.getCode(), item.getInventoryname(), diff);

			Notice notice = new Notice();
			notice.setState(Constants.NOTICE_STATE_PUBLISH);
			notice.setType(Constants.NOTICE_TYPE_ALERT);
			notice.setTitle(alert);
			notice.setContent(alert);
			notice.setCreateDate(new Date());

			notice = noticeRepository.save(notice);

			PurchaseOrderMain main = purchaseOrderMainRepository.findOneByCode(item.getCode());

			List<Account> toList = new ArrayList<>();
			toList.add(main.getDeployer());
			toList.addAll(accountRepository.findAccountsByVendor(main.getVendor().getCode()));

			for (Account account : toList) {
				NoticeRead noticeRead = new NoticeRead();
				noticeRead.setNotice(notice);
				noticeRead.setAccount(account);
				noticeReadRepository.save(noticeRead);

			}

		}
		return true;

	}
}
