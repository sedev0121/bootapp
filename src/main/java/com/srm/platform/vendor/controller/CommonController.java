package com.srm.platform.vendor.controller;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.thymeleaf.util.StringUtils;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.utility.Constants;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 7074132560318771710L;

}

@Controller
@PreAuthorize("isAuthenticated()")
public class CommonController {
	public final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	public EntityManager em;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private HttpSession httpSession;

	protected int currentPage;
	protected int maxResults;
	protected int pageSize;

	public boolean isVendor() {

		return hasAuthority("ROLE_VENDOR");
	}

	public boolean isAdmin() {

		return hasAuthority("ROLE_ADMIN");
	}

	public void show404() {
		throw new ResourceNotFoundException();
	}

	public boolean hasAuthority(String authority) {
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities();
		for (GrantedAuthority a : authorities) {
			if (a.getAuthority().equals(authority)) {
				return true;
			}
		}
		return false;
	}

	public List<String> getUnitListFor(String permissionKey) {
		String defaultUnitList = (String) httpSession.getAttribute(Constants.KEY_DEFAULT_UNIT_LIST);
		String value = (String) httpSession.getAttribute(permissionKey);
		if (value == null)
			value = "";

		value = StringUtils.append(value, defaultUnitList);

		List<String> result = Arrays.asList(StringUtils.split(value, ","));
		return result;

	}

	public List<String> getDefaultUnitList() {
		String defaultUnitList = (String) httpSession.getAttribute(Constants.KEY_DEFAULT_UNIT_LIST);
		List<String> result = Arrays.asList(StringUtils.split(defaultUnitList, ","));
		return result;

	}

	public Account getLoginAccount() {

		Account account = (Account) httpSession.getAttribute("account");

		if (account == null) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			account = accountRepository.findOneByUsername(authentication.getName());

			if (account != null) {
				httpSession.setAttribute("account", account);
			}
		}

		return account;
	}

	public <T> Page<T> query(Class returnClass, String query, String countQuery, Pageable pageable) {

		Query q = em.createNativeQuery(countQuery);

		BigInteger totalCount = (BigInteger) q.getSingleResult();
		q = em.createNativeQuery(query, returnClass.getName());

		List<T> list = q.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize())
				.getResultList();

		return new PageImpl(list, pageable, totalCount.longValue());
	}

}
