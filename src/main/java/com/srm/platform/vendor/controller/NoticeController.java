package com.srm.platform.vendor.controller;

import java.io.File;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.multipart.MultipartFile;

import com.srm.platform.vendor.model.Notice;
import com.srm.platform.vendor.repository.NoticeRepository;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.NoticeSearchResult;
import com.srm.platform.vendor.utility.UploadFileHelper;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/notice")
public class NoticeController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NoticeRepository noticeRepository;

	// 用户管理->列表
	@GetMapping({ "/", "" })
	public String index(Model model) {
		return "notice/list";
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
		String state = requestParams.getOrDefault("state", null);
		String create_account = requestParams.getOrDefault("create_account", null);

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
				+ "left join account d on d.id=a.verify_account left join notice_read e on a.id=e.notice_id where type=1 ";

		List<String> unitList = this.getDefaultUnitList();
		Map<String, Object> params = new HashMap<>();

		params.put("create_account", this.getLoginAccount().getId());
		params.put("to_account", this.getLoginAccount().getId());

		if (isVendor()) {
			bodyQuery += " and e.to_account_id=:to_account and a.state=:state";
			params.put("to_account", this.getLoginAccount().getId());
		} else {
			if (this.hasAuthority("公告通知-发布")) {
				bodyQuery += " and ((a.create_unit in :unitList and a.state=1) or create_account=:create_account or (e.to_account_id=:to_account and a.state=3))";
				params.put("unitList", unitList);
				params.put("to_account", this.getLoginAccount().getId());
			} else {
				bodyQuery += " and (create_account=:create_account or (e.to_account_id=:to_account and a.state=3))";
				params.put("create_account", this.getLoginAccount().getId());
				params.put("to_account", this.getLoginAccount().getId());
			}
		}

		if (!search.trim().isEmpty()) {
			bodyQuery += " and (a.title like CONCAT('%',:search, '%') or a.content like CONCAT('%',:search, '%')) ";
			params.put("search", search.trim());
		}

		if (startDate != null) {
			bodyQuery += " and a.verify_date>=:startDate";
			params.put("startDate", startDate);
		}
		if (endDate != null) {
			bodyQuery += " and a.verify_date<:endDate";
			params.put("endDate", endDate);
		}
		if (create_account != null) {
			bodyQuery += " and b.realname like CONCAT('%',:createAccount, '%')";
			params.put("createAccount", create_account);
		}
		if (state != null && !state.equals("-1")) {
			bodyQuery += " and a.state=:state";
			params.put("state", state);
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

	// 用户管理->修改
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable("id") Long id, Model model) {
		Notice notice = noticeRepository.findOneById(id);
		if (notice == null)
			show404();

		model.addAttribute("notice", notice);
		return "notice/edit";
	}

	// 新建
	@GetMapping("/add")
	@PreAuthorize("hasAuthority('ROLE_BUYER')")
	public String add(Model model) {
		Notice notice = new Notice();
		notice.setUnit(this.getLoginAccount().getUnit());
		notice.setCreateAccount(this.getLoginAccount());
		model.addAttribute("notice", notice);
		return "notice/edit";
	}

	// 删除
	@GetMapping("/{id}/delete")
	@PreAuthorize("hasAuthority('ROLE_BUYER')")
	public @ResponseBody Boolean delete(@PathVariable("id") Long id, Model model) {
		Notice notice = noticeRepository.findOneById(id);
		noticeRepository.delete(notice);
		return true;
	}

	@GetMapping("/{id}/deleteattach")
	@PreAuthorize("hasAuthority('ROLE_BUYER')")
	public @ResponseBody Boolean deleteAttach(@PathVariable("id") Long id, HttpServletRequest request) {
		Notice notice = noticeRepository.findOneById(id);
		String applicationPath = request.getServletContext().getRealPath("");
		File attach = new File(applicationPath + File.separator + notice.getAttachFileName());
		if (attach.exists())
			attach.delete();
		notice.setAttachFileName(null);
		notice.setAttachOriginalName(null);
		noticeRepository.save(notice);
		return true;
	}

	// 用户修改
	@Transactional
	@PostMapping("/update")
	@PreAuthorize("hasAuthority('ROLE_BUYER')")
	public @ResponseBody Notice update_ajax(@RequestParam(value = "attach", required = false) MultipartFile attach,
			HttpServletRequest request, @RequestParam Map<String, String> requestParams) {

		String origianlFileName = null;
		String savedFileName = null;
		if (attach != null) {
			origianlFileName = attach.getOriginalFilename();
			File file = UploadFileHelper.simpleUpload(attach, request, true, Constants.PATH_UPLOADS_NOTICE);
			logger.info(attach.getOriginalFilename());
			if (file != null)
				savedFileName = file.getName();
		}

		String id = requestParams.get("id");
		String title = requestParams.get("title");
		String content = requestParams.get("content");

		Notice notice = new Notice();

		if (id != null && !id.isEmpty()) {
			notice = noticeRepository.findOneById(Long.parseLong(id));

		}

		notice.setTitle(title);
		notice.setContent(content);
		notice.setCreateDate(new Date());
		if (savedFileName != null) {
			notice.setAttachFileName(savedFileName);
			notice.setAttachOriginalName(origianlFileName);
		}

		notice.setUnit(this.getLoginAccount().getUnit());
		notice.setCreateAccount(this.getLoginAccount());

		notice = noticeRepository.save(notice);

		return notice;
	}

}
