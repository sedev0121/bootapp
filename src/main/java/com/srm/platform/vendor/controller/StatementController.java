package com.srm.platform.vendor.controller;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

import com.srm.platform.vendor.model.StatementDetail;
import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.StatementDetailRepository;
import com.srm.platform.vendor.repository.StatementMainRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.StatementDetailItem;
import com.srm.platform.vendor.utility.StatementSaveForm;
import com.srm.platform.vendor.utility.StatementSearchResult;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/statement")
@PreAuthorize("hasRole('ROLE_VENDOR') or hasAuthority('对账单管理-查看列表')")
public class StatementController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private StatementMainRepository statementMainRepository;

	@Autowired
	private StatementDetailRepository statementDetailRepository;

	// 查询列表
	@GetMapping({ "", "/" })
	public String index() {
		return "statement/index";
	}

	@GetMapping({ "/add" })
	@PreAuthorize("hasAuthority('对账单管理-新建/发布')")
	public String add(Model model) {
		StatementMain main = new StatementMain(accountRepository);
		model.addAttribute("main", main);
		return "statement/edit";
	}

	@GetMapping({ "/{code}/edit" })
	public String edit(@PathVariable("code") String code, Model model) {
		StatementMain main = this.statementMainRepository.findOneByCode(code);
		if (main == null)
			show404();

		model.addAttribute("main", main);
		return "statement/edit";
	}

	@GetMapping("/{code}/delete")
	@PreAuthorize("hasAuthority('对账单管理-删除')")
	public @ResponseBody Boolean delete(@PathVariable("code") String code) {
		StatementMain main = statementMainRepository.findOneByCode(code);
		if (main != null)
			statementMainRepository.delete(main);
		return true;
	}

	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<StatementSearchResult> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");
		String vendorStr = requestParams.getOrDefault("vendor", "");
		String stateStr = requestParams.getOrDefault("state", "0");
		String code = requestParams.getOrDefault("code", "");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);

		Integer state = Integer.parseInt(stateStr);
		Date startDate = Utils.parseDate(start_date);
		Date endDate = Utils.getNextDate(end_date);

		switch (order) {
		case "vendor_name":
			order = "b.name";
			break;
		case "vendor_code":
			order = "b.code";
			break;
		case "verifier":
			order = "d.realname";
			break;
		case "maker":
			order = "c.realname";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		String selectQuery = "select a.*, b.name vendor_name, c.realname maker, d.realname verifier ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "from statement_main a left join vendor b on a.vendor_code=b.code left join account c on a.maker_id=c.id "
				+ "left join account d on a.verifier_id=d.id where 1=1 ";

		List<String> unitList = this.getDefaultUnitList();
		Map<String, Object> params = new HashMap<>();

		if (isVendor()) {
			Vendor vendor = this.getLoginAccount().getVendor();
			vendorStr = vendor == null ? "0" : vendor.getCode();
			bodyQuery += " and b.code= :vendor";
			params.put("vendor", vendorStr);

			bodyQuery += " and a.state>1";

		} else {
			bodyQuery += " and b.unit_id in :unitList";
			params.put("unitList", unitList);
			if (!vendorStr.trim().isEmpty()) {
				bodyQuery += " and (b.name like CONCAT('%',:vendor, '%') or b.code like CONCAT('%',:vendor, '%')) ";
				params.put("vendor", vendorStr.trim());
			}
		}

		if (!code.trim().isEmpty()) {
			bodyQuery += " and a.code like CONCAT('%',:code, '%') ";
			params.put("code", code.trim());
		}

		if (state > 0) {
			bodyQuery += " and state=:state";
			params.put("state", state);
		}

		if (startDate != null) {
			bodyQuery += " and a.makedate>=:startDate";
			params.put("startDate", startDate);
		}
		if (endDate != null) {
			bodyQuery += " and a.makedate<:endDate";
			params.put("endDate", endDate);
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "StatementSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<StatementSearchResult>(list, request, totalCount.longValue());

	}

	@RequestMapping(value = "/{code}/details", produces = "application/json")
	public @ResponseBody List<StatementDetailItem> details_ajax(@PathVariable("code") String code) {
		List<StatementDetailItem> list = statementDetailRepository.findDetailsByCode(code);

		return list;
	}

	@Transactional
	@PostMapping("/update")
	public @ResponseBody StatementMain update_ajax(StatementSaveForm form) {
		StatementMain main = new StatementMain();
		main.setCode(form.getCode());

		Example<StatementMain> example = Example.of(main);
		Optional<StatementMain> result = statementMainRepository.findOne(example);
		if (result.isPresent())
			main = result.get();

		if ((main.getState() == null || main.getState() == Constants.STATEMENT_STATE_NEW)
				&& form.getState() <= Constants.STATEMENT_STATE_SUBMIT) {

			main.setMakedate(new Date());
			main.setVendor(vendorRepository.findOneByCode(form.getVendor()));
			main.setMaker(accountRepository.findOneById(form.getMaker()));
			main.setRemark(form.getRemark());
		} else if (form.getState() >= Constants.STATEMENT_STATE_CONFIRM) {

			main.setVerifier(this.getLoginAccount());
			main.setVerifydate(new Date());
		}

		if (this.isVendor() && form.getInvoice_code() != null && !form.getInvoice_code().isEmpty()) {
			main.setInvoiceCode(form.getInvoice_code());
		}

		main.setState(form.getState());
		main = statementMainRepository.save(main);

		if (form.getState() <= Constants.STATEMENT_STATE_SUBMIT && form.getTable() != null) {
			statementDetailRepository.deleteInBatch(statementDetailRepository.findByCode(main.getCode()));

			for (Map<String, String> row : form.getTable()) {
				StatementDetail detail = new StatementDetail();
				detail.setCode(main.getCode());
				detail.setPurchaseInDetailId(Long.parseLong(row.get("purchase_in_detail_id")));

				String closedQuantity = row.get("closed_quantity");
				String closedPrice = row.get("closed_price");
				String closedMoney = row.get("closed_money");
				String closedTaxPrice = row.get("closed_tax_price");
				String closedTaxMoney = row.get("closed_tax_money");

				if (closedQuantity != null && !closedQuantity.isEmpty())
					detail.setClosedQuantity(Float.parseFloat(closedQuantity));

				if (closedPrice != null && !closedPrice.isEmpty())
					detail.setClosedPrice(Float.parseFloat(closedPrice));

				if (closedMoney != null && !closedMoney.isEmpty())
					detail.setClosedMoney(Float.parseFloat(closedMoney));

				if (closedTaxPrice != null && !closedTaxPrice.isEmpty())
					detail.setClosedTaxPrice(Float.parseFloat(closedTaxPrice));

				if (closedTaxMoney != null && !closedTaxMoney.isEmpty())
					detail.setClosedTaxMoney(Float.parseFloat(closedTaxMoney));

				statementDetailRepository.save(detail);
			}
		}

		return main;
	}

}
