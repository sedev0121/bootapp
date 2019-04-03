package com.srm.platform.vendor.controller;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.searchitem.AccountSearchResult;

@Controller
@RequestMapping(path = "/system")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class SystemController extends CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// 用户管理->列表
	@GetMapping({ "/", "" })
	public String index(Model model) {
		return "admin/system/list";
	}

	@GetMapping("/list")
	public @ResponseBody Page<AccountSearchResult> list_ajax(@RequestParam Map<String, String> requestParams) {
		int rows_per_page = Integer.parseInt(requestParams.getOrDefault("rows_per_page", "10"));
		int page_index = Integer.parseInt(requestParams.getOrDefault("page_index", "1"));
		String order = requestParams.getOrDefault("order", "name");
		String dir = requestParams.getOrDefault("dir", "asc");
		String search = requestParams.getOrDefault("search", "");
		String stateStr = requestParams.getOrDefault("state", "");
		String role = requestParams.getOrDefault("role", "");

		Integer state = Integer.parseInt(stateStr);

		if (order.equals("vendorname"))
			order = "v.name";
		if (order.equals("unitname"))
			order = "u.name";

		page_index--;
		PageRequest request = PageRequest.of(page_index, rows_per_page,
				dir.equals("asc") ? Direction.ASC : Direction.DESC, order);
		Page<Account> result = accountRepository.findBySearchTerm(search, request);

		String selectQuery = "SELECT t.*, u.name unitname, v.name vendorname ";
		String countQuery = "select count(*) ";
		String orderBy = " order by " + order + " " + dir;

		String bodyQuery = "FROM account t left join unit u on t.unit_id=u.id left join vendor v on t.vendor_code=v.code where t.id in :list ";

		Map<String, Object> params = new HashMap<>();

		List<Long> onlineAccountList = sessionCounter.getActiveAccountList();
		params.put("list", onlineAccountList);

		if (!search.trim().isEmpty()) {
			bodyQuery += " and (u.name LIKE CONCAT('%',:search, '%') or t.username LIKE CONCAT('%',:search, '%') or t.realname LIKE CONCAT('%',:search, '%') or t.duty LIKE CONCAT('%',:search, '%') or t.email LIKE CONCAT('%',:search, '%')) ";
			params.put("search", search.trim());
		}

		if (state >= 0) {
			bodyQuery += " and state=:state";
			params.put("state", state);
		}

		if (!role.trim().isEmpty()) {
			bodyQuery += " and role=:role";
			params.put("role", role);
		}

		countQuery += bodyQuery;
		Query q = em.createNativeQuery(countQuery);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		BigInteger totalCount = (BigInteger) q.getSingleResult();

		selectQuery += bodyQuery + orderBy;
		q = em.createNativeQuery(selectQuery, "AccountSearchResult");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List list = q.setFirstResult((int) request.getOffset()).setMaxResults(request.getPageSize()).getResultList();

		return new PageImpl<AccountSearchResult>(list, request, totalCount.longValue());

	}

}
