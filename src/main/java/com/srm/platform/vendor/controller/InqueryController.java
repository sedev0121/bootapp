package com.srm.platform.vendor.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Notice;
import com.srm.platform.vendor.model.PurchaseInDetail;
import com.srm.platform.vendor.model.StatementDetail;
import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.model.VenPriceAdjustDetail;
import com.srm.platform.vendor.model.VenPriceAdjustMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.InquerySearchResult;
import com.srm.platform.vendor.utility.U8InvoicePostData;
import com.srm.platform.vendor.utility.U8InvoicePostEntry;
import com.srm.platform.vendor.utility.UploadFileHelper;
import com.srm.platform.vendor.utility.Utils;
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
	private AccountRepository accountRepository;

	// 查询列表
	@GetMapping({ "", "/" })
	public String index() {
		return "inquery/index";
	}

	// 新建
	@PreAuthorize("hasAuthority('询价管理-新建/发布') or hasRole('ROLE_VENDOR')")
	@GetMapping({ "/add" })
	public String add(Model model) {
		VenPriceAdjustMain main = new VenPriceAdjustMain(accountRepository);
		model.addAttribute("main", main);
		return "inquery/edit";
	}

	// 详细
	@GetMapping({ "/{ccode}/edit" })
	public String edit(@PathVariable("ccode") String ccode, Model model) {
		VenPriceAdjustMain main = venPriceAdjustMainRepository.findOneByCcode(ccode);
		if (main == null)
			show404();

		// checkVendor(main.getVendor());

		model.addAttribute("main", main);
		return "inquery/edit";
	}

	@GetMapping({ "/{ccode}/read/{msgid}" })
	public String read(@PathVariable("ccode") String ccode, @PathVariable("msgid") Long msgid, Model model) {
		setReadDate(msgid);
		return "redirect:/inquery/" + ccode + "/edit";
	}

	// 询价单商品列表API
	@RequestMapping(value = "/{mainId}/details", produces = "application/json")
	public @ResponseBody List<VenPriceDetailItem> details_ajax(@PathVariable("mainId") String mainId) {
		List<VenPriceDetailItem> list = venPriceAdjustDetailRepository.findDetailsByMainId(mainId);

		return list;
	}

	@GetMapping("/{ccode}/deleteattach")
	@PreAuthorize("hasAuthority('询价管理-新建/发布') or hasRole('ROLE_VENDOR')")
	public @ResponseBody Boolean deleteAttach(@PathVariable("ccode") String ccode) {
		VenPriceAdjustMain main = venPriceAdjustMainRepository.findOneByCcode(ccode);

		File attach = new File(UploadFileHelper.getUploadDir(Constants.PATH_UPLOADS_INQUERY) + File.separator
				+ main.getAttachFileName());
		if (attach.exists())
			attach.delete();
		main.setAttachFileName(null);
		main.setAttachOriginalName(null);
		venPriceAdjustMainRepository.save(main);
		return true;
	}

	// 删除API
	@PreAuthorize("hasAuthority('询价管理-删除')")
	@GetMapping("/{ccode}/delete")
	@Transactional
	public @ResponseBody Boolean delete_ajax(@PathVariable("ccode") String ccode) {
		VenPriceAdjustMain main = venPriceAdjustMainRepository.findOneByCcode(ccode);
		if (main != null) {
			venPriceAdjustDetailRepository.DeleteByMainId(main.getCcode());
			venPriceAdjustMainRepository.delete(main);
		}

		return true;
	}

	// 查询列表API
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<InquerySearchResult> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");
		String vendorStr = requestParams.getOrDefault("vendor", "");
		String stateStr = requestParams.getOrDefault("state", "0");
		String inventory = requestParams.getOrDefault("inventory", "");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);

		Integer state = Integer.parseInt(stateStr);
		Date startDate = Utils.parseDate(start_date);
		Date endDate = Utils.parseDate(end_date);

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

		String selectQuery = "select distinct a.*, c.code vendorcode, c.name vendorname, e.realname makername, f.realname verifiername ";
		String countQuery = "select count( distinct a.ccode ) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "from venpriceadjust_main a left join venpriceadjust_detail b on a.ccode = b.mainid "
				+ "left join vendor c on a.cvencode=c.code left join inventory d on b.cinvcode=d.code "
				+ "left join account e on a.maker_id=e.id left join account f on a.cverifier_id=f.id "
				+ "where a.createtype= :createType ";

		if (!isVendor()) {
			bodyQuery = "from venpriceadjust_main a left join venpriceadjust_detail b on a.ccode = b.mainid "
					+ "left join vendor c on a.cvencode=c.code left join inventory d on b.cinvcode=d.code "
					+ "left join account e on a.maker_id=e.id left join account f on a.cverifier_id=f.id "
					+ "where c.code in :vendorList and a.createtype= :createType ";
		}
		List<String> vendorList = this.getVendorListOfUser();
		Map<String, Object> params = new HashMap<>();

		if (isVendor()) {
			Vendor vendor = this.getLoginAccount().getVendor();
			vendorStr = vendor == null ? "0" : vendor.getCode();
			bodyQuery += " and c.code= :vendor";
			params.put("vendor", vendorStr);
			params.put("createType", Constants.CREATE_TYPE_VENDOR);
		} else {
			params.put("vendorList", vendorList);
			if (!vendorStr.trim().isEmpty()) {
				bodyQuery += " and (c.name like CONCAT('%',:vendor, '%') or c.code like CONCAT('%',:vendor, '%')) ";
				params.put("vendor", vendorStr.trim());
			}
			params.put("createType", Constants.CREATE_TYPE_BUYER);
		}

		if (!inventory.trim().isEmpty()) {
			bodyQuery += " and (d.name like CONCAT('%',:inventory, '%') or d.code like CONCAT('%',:inventory, '%')) ";
			params.put("inventory", inventory.trim());
		}

		if (state > 0) {
			bodyQuery += " and iverifystate=:state";
			params.put("state", state);
		}

		if (startDate != null) {
			bodyQuery += " and a.dstartdate=:startDate";
			params.put("startDate", startDate);
		}
		if (endDate != null) {
			bodyQuery += " and a.denddate=:endDate";
			params.put("endDate", endDate);
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "InquerySearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<InquerySearchResult>(list, request, totalCount.longValue());

	}

	// 更新API
	@Transactional
	@PostMapping("/update")
	public @ResponseBody GenericJsonResponse<VenPriceAdjustMain> update_ajax(VenPriceSaveForm form,
			@RequestParam(value = "attach", required = false) MultipartFile attach, Principal principal) {

		String origianlFileName = null;
		String savedFileName = null;
		if (attach != null) {
			origianlFileName = attach.getOriginalFilename();
			File file = UploadFileHelper.simpleUpload(attach, true, Constants.PATH_UPLOADS_INQUERY);

			if (file != null)
				savedFileName = file.getName();
		}

		VenPriceAdjustMain venPriceAdjustMain = venPriceAdjustMainRepository.findOneByCcode(form.getCcode());

		if (venPriceAdjustMain == null) {
			venPriceAdjustMain = new VenPriceAdjustMain();
			venPriceAdjustMain.setCreatetype(isVendor() ? Constants.CREATE_TYPE_VENDOR : Constants.CREATE_TYPE_BUYER);
			venPriceAdjustMain.setCcode(form.getCcode());
		}

		if ((venPriceAdjustMain.getIverifystate() == null
				|| venPriceAdjustMain.getIverifystate() == Constants.STATE_NEW)
				&& form.getState() <= Constants.STATE_CONFIRM) {
			venPriceAdjustMain.setType(form.getType());
			venPriceAdjustMain.setIsupplytype(form.getProvide_type());
			venPriceAdjustMain.setItaxrate(form.getTax_rate());

			venPriceAdjustMain.setDstartdate(form.getStart_date());
			venPriceAdjustMain.setDenddate(form.getEnd_date());
			venPriceAdjustMain.setDmakedate(new Date());
			venPriceAdjustMain.setVendor(vendorRepository.findOneByCode(form.getVendor()));
			venPriceAdjustMain.setMaker(accountRepository.findOneById(form.getMaker()));

			if (savedFileName != null) {
				venPriceAdjustMain.setAttachFileName(savedFileName);
				venPriceAdjustMain.setAttachOriginalName(origianlFileName);
			}
		}

		Account account = this.getLoginAccount();

		if (form.getState() == Constants.STATE_VERIFY || (venPriceAdjustMain.getIverifystate() != null
				&& venPriceAdjustMain.getIverifystate() == Constants.STATE_PASS
				&& form.getState() == Constants.STATE_CANCEL)) {
			venPriceAdjustMain.setVerifier(account);
			venPriceAdjustMain.setDverifydate(new Date());
		}
		if (form.getState() == Constants.STATE_PUBLISH) {
			GenericJsonResponse<VenPriceAdjustMain> u8Response = this.u8VenPriceAdjust(venPriceAdjustMain);
			if (u8Response.getSuccess() == GenericJsonResponse.SUCCESS) {
				venPriceAdjustMain.setPublisher(account);
				venPriceAdjustMain.setDpublishdate(new Date());
			} else {
				return u8Response;
			}

		}
		int state = form.getState();
		if (venPriceAdjustMain.getCreatetype() == Constants.CREATE_TYPE_VENDOR
				&& form.getState() == Constants.STATE_SUBMIT) {
			state = Constants.STATE_CONFIRM;
		}
		venPriceAdjustMain.setIverifystate(state);
		venPriceAdjustMain = venPriceAdjustMainRepository.save(venPriceAdjustMain);

		GenericJsonResponse<VenPriceAdjustMain> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS,
				null, venPriceAdjustMain);

		String action = null;
		String type = "询价单";

		List<Account> toList = new ArrayList<>();

		String url = String.format("/inquery/%s/read", venPriceAdjustMain.getCcode());

		switch (form.getState()) {
		case Constants.STATE_SUBMIT:
			action = "提交";
			if (venPriceAdjustMain.getCreatetype() == Constants.CREATE_TYPE_VENDOR) {
				List<String> idList = new ArrayList();
				idList.add(String.valueOf(venPriceAdjustMain.getVendor().getUnit().getId()));
				toList.addAll(accountRepository.findAccountsByUnitIdList(idList));
				type = "报价单";
			} else {
				toList.addAll(accountRepository.findAccountsByVendor(venPriceAdjustMain.getVendor().getCode()));
			}
			url = String.format("/quote/%s/read", venPriceAdjustMain.getCcode());
			break;
		case Constants.STATE_CONFIRM:
			toList.add(venPriceAdjustMain.getMaker());
			action = "确认";
			break;
		case Constants.STATE_PASS:
			toList.add(venPriceAdjustMain.getMaker());
			action = "通过";
			break;
		case Constants.STATE_VERIFY:
			toList.add(venPriceAdjustMain.getMaker());
			action = "审核";
			break;
		case Constants.STATE_PUBLISH:
			toList.add(venPriceAdjustMain.getMaker());
			action = "归档";
			break;
		case Constants.STATE_CANCEL:
			toList.add(venPriceAdjustMain.getMaker());
			action = "退回";
			break;
		}

		String title = String.format("%s【%s】已由【%s】%s，请及时查阅和处理！", type, venPriceAdjustMain.getCcode(),
				account.getRealname(), action);
		this.sendmessage(title, toList, url);

		if (form.getState() <= Constants.STATE_PASS && form.getTable() != null) {
			venPriceAdjustDetailRepository
					.deleteInBatch(venPriceAdjustDetailRepository.findByMainId(venPriceAdjustMain.getCcode()));

			for (Map<String, String> row : form.getTable()) {
				VenPriceAdjustDetail detail = new VenPriceAdjustDetail();
				detail.setMain(venPriceAdjustMain);
				detail.setInventory(inventoryRepository.findByCode(row.get("cinvcode")));
				detail.setCbodymemo(row.get("cbodymemo"));
				if (row.get("iunitprice") != null && !row.get("iunitprice").isEmpty()) {
					detail.setIunitprice(Float.parseFloat(row.get("iunitprice")));
				}

				if (row.get("ivalid") != null && !row.get("ivalid").isEmpty()) {
					detail.setIvalid(Integer.parseInt(row.get("ivalid")));
				} else {
					detail.setIvalid(0);
				}
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
				if (startDateStr != null && !startDateStr.isEmpty())
					detail.setDstartdate(Utils.parseDate(startDateStr));
				if (endDateStr != null && !endDateStr.isEmpty())
					detail.setDenddate(Utils.parseDate(endDateStr));

				if (row.get("rowno") != null && !row.get("rowno").isEmpty())
					detail.setRowno(Integer.parseInt(row.get("rowno")));

				if (row.get("itaxrate") != null && !row.get("itaxrate").isEmpty())
					detail.setItaxrate(Float.parseFloat(row.get("itaxrate")));
				if (row.get("itaxunitprice") != null && !row.get("itaxunitprice").isEmpty())
					detail.setItaxunitprice(Float.parseFloat(row.get("itaxunitprice")));

				venPriceAdjustDetailRepository.save(detail);
			}
		}

		if (form.getState() == Constants.STATE_PUBLISH) {
			updatePriceTable(venPriceAdjustMain);
		}

		return jsonResponse;
	}

	@GetMapping("/{ccode}/download")
	public ResponseEntity<Resource> download(@PathVariable("ccode") String ccode) {
		VenPriceAdjustMain main = venPriceAdjustMainRepository.findOneByCcode(ccode);
		if (main == null)
			show404();

		return download(Constants.PATH_UPLOADS_INQUERY + File.separator + main.getAttachFileName(),
				main.getAttachOriginalName());
	}

}
