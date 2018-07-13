package com.srm.platform.vendor.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.StatementDetail;
import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.StatementDetailRepository;
import com.srm.platform.vendor.repository.StatementMainRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.StatementDetailItem;
import com.srm.platform.vendor.utility.StatementSaveForm;
import com.srm.platform.vendor.utility.StatementSearchItem;

@Controller
@RequestMapping(path = "/statement")

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
	public String add(Model model) {
		StatementMain main = new StatementMain(accountRepository);
		model.addAttribute("main", main);
		return "statement/edit";
	}

	@GetMapping({ "/{code}/edit" })
	public String edit(@PathVariable("code") String code, Model model) {
		model.addAttribute("main", this.statementMainRepository.findOneByCode(code));
		return "statement/edit";
	}

	@GetMapping("/{code}/delete")
	public @ResponseBody Boolean delete(@PathVariable("code") String code) {
		StatementMain main = statementMainRepository.findOneByCode(code);
		if (main != null)
			statementMainRepository.delete(main);
		return true;
	}

	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<StatementSearchItem> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");
		String vendor = requestParams.getOrDefault("vendor", "");
		String stateStr = requestParams.getOrDefault("state", "0");
		String code = requestParams.getOrDefault("code", "");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);

		Integer state = Integer.parseInt(stateStr);
		Date startDate = null, endDate = null;
		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			if (start_date != null && !start_date.isEmpty())
				startDate = dateFormatter.parse(start_date);
			if (end_date != null && !end_date.isEmpty()) {
				dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
				endDate = dateFormatter.parse(end_date);
				Calendar cal = Calendar.getInstance();
				cal.setTime(endDate);
				cal.add(Calendar.DATE, 1);
				endDate = cal.getTime();
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

		Page<StatementSearchItem> result = null;

		if (this.isVendor()) {
			result = statementMainRepository.findBySearchTermForVendor(code,
					this.getLoginAccount().getVendor().getCode(), request);
		} else {
			result = statementMainRepository.findBySearchTerm(code, vendor, request);
		}
		return result;
	}

	@RequestMapping(value = "/{code}/details", produces = "application/json")
	public @ResponseBody List<StatementDetailItem> details_ajax(@PathVariable("code") String code) {
		List<StatementDetailItem> list = statementDetailRepository.findDetailsByCode(code);

		return list;
	}

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
