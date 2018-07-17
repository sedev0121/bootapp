package com.srm.platform.vendor.controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.PriceRepository;
import com.srm.platform.vendor.repository.VenPriceAdjustDetailRepository;
import com.srm.platform.vendor.repository.VenPriceAdjustMainRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.Utils;
import com.srm.platform.vendor.utility.VenPriceAdjustSearchItem;
import com.srm.platform.vendor.utility.VenPriceDetailItem;
import com.srm.platform.vendor.utility.VenPriceSaveForm;

@Controller
@RequestMapping(path = "/inquery")
@PreAuthorize("hasRole('ROLE_VENDOR') or hasAuthority('询价管理-查看列表')")
public class InqueryController extends CommonController {

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private PriceRepository priceRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private VenPriceAdjustMainRepository venPriceAdjustMainRepository;

	@Autowired
	private VenPriceAdjustDetailRepository venPriceAdjustDetailRepository;

	// 查询列表
	@GetMapping({ "", "/" })
	public String index() {
		return "inquery/index";
	}

	// 新建
	@PreAuthorize("hasAuthority('询价管理-新建/发布')")
	@GetMapping({ "/add" })
	public String add(Model model) {
		VenPriceAdjustMain main = new VenPriceAdjustMain(accountRepository);
		model.addAttribute("main", main);
		return "inquery/edit";
	}

	// 详细
	@GetMapping({ "/{ccode}/edit" })
	public String edit(@PathVariable("ccode") String ccode, Model model) {
		model.addAttribute("main", this.venPriceAdjustMainRepository.findOneByCcode(ccode));
		return "inquery/edit";
	}

	// 询价单商品列表API
	@RequestMapping(value = "/{mainId}/details", produces = "application/json")
	public @ResponseBody List<VenPriceDetailItem> details_ajax(@PathVariable("mainId") String mainId) {
		List<VenPriceDetailItem> list = venPriceAdjustDetailRepository.findDetailsByMainId(mainId);

		return list;
	}

	// 删除API
	@GetMapping("/{ccode}/delete")
	public @ResponseBody Boolean delete_ajax(@PathVariable("ccode") String ccode) {
		VenPriceAdjustMain main = venPriceAdjustMainRepository.findOneByCcode(ccode);
		if (main != null)
			venPriceAdjustMainRepository.delete(main);
		return true;
	}

	// 查询列表API
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<VenPriceAdjustSearchItem> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");
		String vendor = requestParams.getOrDefault("vendor", "");
		String stateStr = requestParams.getOrDefault("state", "0");
		String inventory = requestParams.getOrDefault("inventory", "");
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
			result = venPriceAdjustMainRepository.findBySearchTerm(Constants.CREATE_TYPE_VENDOR, vendor, inventory,
					request);
		} else {
			result = venPriceAdjustMainRepository.findBySearchTerm(Constants.CREATE_TYPE_BUYER, vendor, inventory,
					request);
		}

		return result;
	}

	// 更新API
	@Transactional
	@PostMapping("/update")
	public @ResponseBody VenPriceAdjustMain update_ajax(VenPriceSaveForm form, Principal principal) {
		VenPriceAdjustMain venPriceAdjustMain = new VenPriceAdjustMain();

		venPriceAdjustMain.setCreatetype(isVendor() ? Constants.CREATE_TYPE_VENDOR : Constants.CREATE_TYPE_BUYER);

		venPriceAdjustMain.setCcode(form.getCcode());

		Example<VenPriceAdjustMain> example = Example.of(venPriceAdjustMain);
		Optional<VenPriceAdjustMain> result = venPriceAdjustMainRepository.findOne(example);
		if (result.isPresent())
			venPriceAdjustMain = result.get();

		if ((venPriceAdjustMain.getIverifystate() == null
				|| venPriceAdjustMain.getIverifystate() == Constants.STATE_NEW)
				&& form.getState() <= Constants.STATE_SUBMIT) {
			venPriceAdjustMain.setType(form.getType());
			venPriceAdjustMain.setIsupplytype(form.getProvide_type());
			venPriceAdjustMain.setItaxrate(form.getTax_rate());

			venPriceAdjustMain.setDstartdate(form.getStart_date());
			venPriceAdjustMain.setDenddate(form.getEnd_date());
			venPriceAdjustMain.setDmakedate(new Date());
			venPriceAdjustMain.setVendor(vendorRepository.findOneByCode(form.getVendor()));
			venPriceAdjustMain.setMaker(accountRepository.findOneById(form.getMaker()));
		}

		Account account = this.getLoginAccount();

		if (form.getState() == Constants.STATE_VERIFY || (venPriceAdjustMain.getIverifystate() != null
				&& venPriceAdjustMain.getIverifystate() == Constants.STATE_PASS
				&& form.getState() == Constants.STATE_CANCEL)) {
			venPriceAdjustMain.setVerifier(account);
			venPriceAdjustMain.setDverifydate(new Date());
		}
		if (form.getState() == Constants.STATE_PUBLISH) {
			venPriceAdjustMain.setPublisher(account);
			venPriceAdjustMain.setDpublishdate(new Date());
		}

		venPriceAdjustMain.setIverifystate(form.getState());
		venPriceAdjustMain = venPriceAdjustMainRepository.save(venPriceAdjustMain);

		if (form.getState() <= Constants.STATE_SUBMIT && form.getTable() != null) {
			venPriceAdjustDetailRepository
					.deleteInBatch(venPriceAdjustDetailRepository.findByMainId(venPriceAdjustMain.getCcode()));

			for (Map<String, String> row : form.getTable()) {
				VenPriceAdjustDetail detail = new VenPriceAdjustDetail();
				detail.setMain(venPriceAdjustMain);
				detail.setInventory(inventoryRepository.findByCode(row.get("cinvcode")));
				detail.setCbodymemo(row.get("cbodymemo"));
				detail.setIunitprice(Float.parseFloat(row.get("iunitprice")));
				String max = row.get("fmaxquantity");
				String min = row.get("fminquantity");
				if (max != null && !max.isEmpty())
					detail.setFmaxquantity(Float.parseFloat(max));

				if (min != null && !min.isEmpty())
					detail.setFminquantity(Float.parseFloat(min));

				if (row.get("ivalid") != null && !row.get("ivalid").isEmpty())
					detail.setIvalid(Integer.parseInt(row.get("ivalid")));

				String startDateStr = row.get("dstartdate");
				String endDateStr = row.get("denddate");
				detail.setDstartdate(Utils.parseDate(startDateStr));
				detail.setDenddate(Utils.getNextDate(endDateStr));

				detail.setItaxrate(Float.parseFloat(row.get("itaxrate")));
				detail.setItaxunitprice(Float.parseFloat(row.get("itaxunitprice")));

				venPriceAdjustDetailRepository.save(detail);
			}
		}

		if (form.getState() == Constants.STATE_PUBLISH) {
			updatePriceTable(venPriceAdjustMain);
		}

		return venPriceAdjustMain;
	}

	// 更新价格表
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
}
