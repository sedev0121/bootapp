package com.srm.platform.vendor.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.OperationHistory;
import com.srm.platform.vendor.utility.GenericJsonResponse;

// 供应商管理
@Controller
@RequestMapping(path = "/operation_history")
public class OperationHistoryController extends CommonController {


	// 查询列表API
	@RequestMapping(value = "/list/{targetType}/{targetId}", produces = "application/json")
	public @ResponseBody List<OperationHistory> list_ajax(@PathVariable("targetType") String targetType, @PathVariable("targetId") String targetId) {

		List<OperationHistory> result = new ArrayList<OperationHistory>();

		if (targetType != null && targetId != null) {
			result = operationHistoryRepository.findByTarget(targetType, targetId);
		}

		return result;
	}

	// 用户修改
	@Transactional
	@PostMapping("/update")
	public @ResponseBody GenericJsonResponse<OperationHistory> update_ajax(@RequestParam Map<String, String> requestParams) {

		String action = requestParams.getOrDefault("action", null);
		String content = requestParams.getOrDefault("content", null);
		String id = requestParams.getOrDefault("id", null);
		String targetType = requestParams.getOrDefault("target_type", null);
		String targetId = requestParams.getOrDefault("target_id", null);
		
		OperationHistory operationHistory = new OperationHistory();
		if (id != null) {
			operationHistory = operationHistoryRepository.findOneById(Long.valueOf(id));

		}
		
		operationHistory.setTargetId(targetId);
		operationHistory.setTargetType(targetType);		
		operationHistory.setAction(action);
		operationHistory.setContent(content);	
		operationHistory.setAccount(this.getLoginAccount());

		operationHistory = operationHistoryRepository.save(operationHistory);
		
		GenericJsonResponse<OperationHistory> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null, operationHistory);
		
		return jsonResponse;
	}
}
