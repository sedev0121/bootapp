package com.srm.platform.vendor.controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Price;
import com.srm.platform.vendor.model.VenPriceAdjustDetail;
import com.srm.platform.vendor.model.VenPriceAdjustMain;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.PriceRepository;
import com.srm.platform.vendor.repository.VenPriceAdjustDetailRepository;
import com.srm.platform.vendor.repository.VenPriceAdjustMainRepository;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.Utils;
import com.srm.platform.vendor.utility.VenPriceAdjustSearchItem;
import com.srm.platform.vendor.utility.VenPriceSaveForm;

@Controller
@RequestMapping(path = "/quote")
@PreAuthorize("hasRole('ROLE_VENDOR') or hasAuthority('报价管理-查看列表')")
public class QuoteController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private PriceRepository priceRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private VenPriceAdjustMainRepository venPriceAdjustMainRepository;

	@Autowired
	private VenPriceAdjustDetailRepository venPriceAdjustDetailRepository;

	private void updatePriceTable(VenPriceAdjustMain venPriceAdjustMain) {

		List<VenPriceAdjustDetail> list = venPriceAdjustDetailRepository.findByMainId(venPriceAdjustMain.getCcode());
		for (VenPriceAdjustDetail item : list) {
			Price price = new Price();
			price.setVendor(venPriceAdjustMain.getVendor());
			price.setInventory(item.getInventory());
			price.setCreateby(venPriceAdjustMain.getMaker().getId());
			price.setCreatedate(venPriceAdjustMain.getDmakedate());
			price.setFavdate(venPriceAdjustMain.getDstartdate());
			price.setFcanceldate(venPriceAdjustMain.getDenddate());
			price.setFnote(item.getCbodymemo());
			price.setFprice(item.getIunitprice());
			price.setFtax((float) item.getItaxrate());
			price.setFtaxprice(item.getItaxunitprice());
			price.setFisoutside(false);
			price.setFcheckdate(new Date());
			price.setDescription(item.getInventory().getSpecs());
			price.setFauxunit(item.getInventory().getPuunitName());
			priceRepository.save(price);
		}

	}

	// 查询列表
	@GetMapping({ "", "/" })
	public String index() {
		return "quote/index";
	}

	// 详细
	@GetMapping("/{ccode}/edit")
	public String edit(@PathVariable("ccode") String ccode, Model model) {
		model.addAttribute("main", this.venPriceAdjustMainRepository.findOneByCcode(ccode));
		return "quote/edit";
	}

	// 查询列表API
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<VenPriceAdjustSearchItem> list_ajax(Principal principal,
			@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");

		String stateStr = requestParams.getOrDefault("state", "0");
		String inventory = requestParams.getOrDefault("inventory", "");
		String vendor = requestParams.getOrDefault("vendor", "");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);

		Integer state = Integer.parseInt(stateStr);
		Date startDate = Utils.parseDate(start_date);
		Date endDate = Utils.getNextDate(end_date);

		switch (order) {
		case "vendorname":
			order = "c.name";
			break;
		case "vendorcode":
			order = "c.code";
			break;
		case "verifiername":
			order = "f.realname";
			break;
		case "makername":
			order = "e.realname";
			break;
		}
		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		Page<VenPriceAdjustSearchItem> result = null;
		if (isVendor()) {
			result = venPriceAdjustMainRepository.findBySearchTermForVendor(Constants.CREATE_TYPE_BUYER,
					this.getLoginAccount().getVendor().getCode(), inventory, request);

		} else {
			result = venPriceAdjustMainRepository.findBySearchTermForBuyer(Constants.CREATE_TYPE_VENDOR, vendor,
					inventory, request);
		}

		return result;
	}

	// 更新API
	@Transactional
	@PostMapping("/update")
	public @ResponseBody VenPriceAdjustMain update_ajax(VenPriceSaveForm form) {
		String ccode = form.getCcode();
		Integer state = form.getState();

		VenPriceAdjustMain venPriceAdjustMain = venPriceAdjustMainRepository.findOneByCcode(ccode);

		Account account = this.getLoginAccount();

		if (this.isVendor()) {
			if (state == Constants.STATE_CONFIRM || state == Constants.STATE_CANCEL || state == Constants.STATE_PASS) {
				venPriceAdjustMain.setReviewer(account);
				venPriceAdjustMain.setDreviewdate(new Date());
			}
		} else {
			if (state == Constants.STATE_VERIFY || (venPriceAdjustMain.getIverifystate() == Constants.STATE_PASS
					&& state == Constants.STATE_CANCEL)) {
				venPriceAdjustMain.setVerifier(account);
				venPriceAdjustMain.setDverifydate(new Date());
			}
			if (state == Constants.STATE_PUBLISH) {
				venPriceAdjustMain.setPublisher(account);
				venPriceAdjustMain.setDpublishdate(new Date());
			}
		}

		venPriceAdjustMain.setIverifystate(state);
		venPriceAdjustMain = venPriceAdjustMainRepository.save(venPriceAdjustMain);

		if (state == Constants.STATE_PASS || state == Constants.STATE_CONFIRM || state == Constants.STATE_CANCEL) {
			if (form.getTable() != null) {
				for (Map<String, String> row : form.getTable()) {
					Optional<VenPriceAdjustDetail> result = venPriceAdjustDetailRepository
							.findById(Long.parseLong(row.get("id")));
					if (result.isPresent()) {
						VenPriceAdjustDetail detail = result.get();
						detail.setIunitprice(Float.parseFloat(row.get("iunitprice")));
						detail.setItaxunitprice(Float.parseFloat(row.get("itaxunitprice")));
						detail.setCbodymemo(row.get("cbodymemo"));
						venPriceAdjustDetailRepository.save(detail);
					}

				}
			}
		}

		if (form.getState() == Constants.STATE_PUBLISH) {
			updatePriceTable(venPriceAdjustMain);
		}

		return venPriceAdjustMain;
	}

}
