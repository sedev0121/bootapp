package com.srm.platform.vendor.controller;

import java.io.File;
import java.math.BigInteger;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

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

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Box;
import com.srm.platform.vendor.model.DeliveryDetail;
import com.srm.platform.vendor.model.DeliveryMain;
import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.model.Master;
import com.srm.platform.vendor.model.PurchaseOrderDetail;
import com.srm.platform.vendor.model.PurchaseOrderMain;
import com.srm.platform.vendor.model.Task;
import com.srm.platform.vendor.model.VenPriceAdjustMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.saveform.DeliverySaveForm;
import com.srm.platform.vendor.searchitem.BoxExportResult;
import com.srm.platform.vendor.searchitem.BoxSearchResult;
import com.srm.platform.vendor.searchitem.DeliverySearchResult;
import com.srm.platform.vendor.searchitem.PurchaseOrderSearchResult;
import com.srm.platform.vendor.searchitem.TaskLogSearchResult;
import com.srm.platform.vendor.searchitem.TaskSearchResult;
import com.srm.platform.vendor.u8api.RestApiClient;
import com.srm.platform.vendor.u8api.RestApiResponse;
import com.srm.platform.vendor.utility.AccountPermission;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.Utils;

@Controller
@RequestMapping(path = "/task")
public class TaskController extends CommonController {

	private static Long LIST_FUNCTION_ACTION_ID = 18L;

	@Override
	protected String getOperationHistoryType() {
		return "task";
	};

	// 查询列表
	@GetMapping({ "", "/" })
	public String index() {
		return "task/index";
	}

	// 新建
	@GetMapping({ "/add" })
	public String add(Model model) {
		DeliveryMain main = new DeliveryMain();
		main.setVendor(getLoginAccount().getVendor());
		model.addAttribute("main", main);
		return "task/edit";
	}

	// 查询列表API
	@RequestMapping(value = "/list", produces = "application/json")
	public @ResponseBody Page<TaskSearchResult> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");

		String code = requestParams.getOrDefault("code", "");
		String start_date = requestParams.getOrDefault("start_date", null);
		String end_date = requestParams.getOrDefault("end_date", null);

		Date startDate = Utils.parseDate(start_date);
		Date endDate = Utils.getNextDate(end_date);

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		String selectQuery = "SELECT a.*, b.realname maker ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "FROM task a left join account b on a.make_id=b.id where 1=1 ";

		Map<String, Object> params = new HashMap<>();

		if (!code.trim().isEmpty()) {
			bodyQuery += " and a.code like CONCAT('%',:code, '%') ";
			params.put("code", code.trim());
		}

		if (startDate != null) {
			bodyQuery += " and a.statement_date>=:startDate";
			params.put("startDate", startDate);
		}
		if (endDate != null) {
			bodyQuery += " and a.statement_date<:endDate";
			params.put("endDate", endDate);
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "TaskSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<TaskSearchResult>(list, request, totalCount.longValue());
	}

	// 查询列表API
	@RequestMapping(value = "/{id}/log", produces = "application/json")
	public @ResponseBody Page<TaskLogSearchResult> list_ajax(@PathVariable("id") Long id,
			@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "ccode");
		String dir = requestParams.getOrDefault("dir", "asc");

		String search = requestParams.getOrDefault("search", "");

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);

		String selectQuery = "SELECT a.*, c.name vendor_name, d.type, e.name company_name ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "FROM task_log a left join task b on a.task_id=b.id left join vendor c on a.vendor_code=c.code "
				+ "left join statement_main d on a.statement_code=d.code left join company e on d.company_id=e.id where a.task_id=:id ";

		Map<String, Object> params = new HashMap<>();
		params.put("id", id);

		if (!search.trim().isEmpty()) {
			bodyQuery += " and (a.vendor_code like CONCAT('%',:search, '%') or c.name like CONCAT('%',:search, '%') )";
			params.put("search", search.trim());
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "TaskLogSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<TaskLogSearchResult>(list, request, totalCount.longValue());
	}
	
	// 更新API
	@GetMapping("/auto_setting")
	public @ResponseBody GenericJsonResponse<Map<String, String>> getAutoSetting() {

		Master statementDateMaster = masterRepository.findOneByItemKey(Constants.KEY_AUTO_TASK_STATEMENT_DATE);
		Master startDateMaster = masterRepository.findOneByItemKey(Constants.KEY_AUTO_TASK_START_DATE);
		Master startTimeMaster = masterRepository.findOneByItemKey(Constants.KEY_AUTO_TASK_START_TIME);

		Map<String, String> data = new HashMap<String, String>();
		data.put("statement_date", statementDateMaster == null? "" : statementDateMaster.getItemValue());
		data.put("start_date", startDateMaster == null? "" : startDateMaster.getItemValue());
		data.put("start_time", startTimeMaster == null? "" : startTimeMaster.getItemValue());
		GenericJsonResponse<Map<String, String>> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, data);

		return jsonResponse;
	}

	// 更新API
	@Transactional
	@PostMapping("/update")
	public @ResponseBody GenericJsonResponse<Task> update_ajax(@RequestParam Map<String, String> requestParams,
			Principal principal) {

		String statement_date = requestParams.getOrDefault("statement_date", null);

		Date statementDate = Utils.parseDate(statement_date);

		Task task = new Task();
		task.setCode(Utils.generateTaskCode());
		task.setMakeDate(new Date());
		task.setStatementDate(statementDate);
		task.setMaker(this.getLoginAccount());
		task = this.taskRepository.save(task);

		GenericJsonResponse<Task> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, task);

		return jsonResponse;
	}
	
	// 更新API
	@Transactional
	@PostMapping("/update_auto_setting")
	public @ResponseBody GenericJsonResponse<String> updateAutoSetting(@RequestParam Map<String, String> requestParams,
			Principal principal) {

		String statementDate = requestParams.getOrDefault("statement_date", null);
		String startDate = requestParams.getOrDefault("start_date", null);
		String startHour = requestParams.getOrDefault("start_hour", null);
		String startMin = requestParams.getOrDefault("start_min", null);

		//对账时间
		Master master = masterRepository.findOneByItemKey(Constants.KEY_AUTO_TASK_STATEMENT_DATE);
		if (master == null) {
			master = new Master();	
			master.setItemKey(Constants.KEY_AUTO_TASK_STATEMENT_DATE);
		}		
		master.setItemValue(statementDate);
		masterRepository.save(master);
		
		//自动运行时间
		master = masterRepository.findOneByItemKey(Constants.KEY_AUTO_TASK_START_DATE);
		if (master == null) {
			master = new Master();	
			master.setItemKey(Constants.KEY_AUTO_TASK_START_DATE);
		}		
		master.setItemValue(startDate);
		masterRepository.save(master);
		
		//自动运行时间
		master = masterRepository.findOneByItemKey(Constants.KEY_AUTO_TASK_START_TIME);
		if (master == null) {
			master = new Master();	
			master.setItemKey(Constants.KEY_AUTO_TASK_START_TIME);
		}		
		master.setItemValue(startHour + ":" + startMin);
		masterRepository.save(master);

		GenericJsonResponse<String> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, "ok");

		return jsonResponse;
	}

}
