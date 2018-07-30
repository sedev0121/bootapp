package com.srm.platform.vendor.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.thymeleaf.util.StringUtils;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Notice;
import com.srm.platform.vendor.model.NoticeRead;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.NoticeReadRepository;
import com.srm.platform.vendor.repository.NoticeRepository;
import com.srm.platform.vendor.service.SessionCounter;
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
	public NoticeRepository noticeRepository;

	@Autowired
	public NoticeReadRepository noticeReadRepository;

	@Autowired
	public AccountRepository accountRepository;

	@Autowired
	public HttpSession httpSession;

	@Autowired
	public SessionCounter sessionCounter;

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

	public void show403() {
		throw new AccessDeniedException("access denied");
	}

	public void checkVendor(Vendor vendor) {
		List<String> unitList = getDefaultUnitList();

		if ((isVendor() && !vendor.getCode().equals(this.getLoginAccount().getVendor().getCode())) || (!isVendor()
				&& (vendor.getUnit() == null || !unitList.contains(String.valueOf(vendor.getUnit().getId()))))) {
			show403();
		}
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

	public boolean isAuthorizedUnit(Long unitId) {
		for (String unitIdStr : getDefaultUnitList()) {
			if (Long.valueOf(unitIdStr) == unitId) {
				return true;
			}
		}
		return false;
	}

	public boolean isVisibleNotice(Long noticeId) {
		boolean isVisible = false;
		List<NoticeRead> readList = noticeReadRepository.findListByNoticeId(noticeId);
		for (NoticeRead read : readList) {
			if (read.getAccount().getId() == this.getLoginAccount().getId()) {
				isVisible = true;
				break;
			}
		}

		return isVisible;
	}

	public void setReadDate(Long noticeId) {
		NoticeRead noticeRead = noticeReadRepository.findOneByNoticeAndAccount(noticeId,
				this.getLoginAccount().getId());
		if (noticeRead != null) {
			noticeRead.setReadDate(new Date());
			noticeReadRepository.save(noticeRead);
		}
	}

	public void sendmessage(String title, List<Account> toList) {
		Notice notice = new Notice();
		notice.setState(Constants.NOTICE_STATE_PUBLISH);
		notice.setType(Constants.NOTICE_TYPE_SYSTEM);
		notice.setTitle(title);
		notice.setContent(title);
		notice.setCreateDate(new Date());
		notice = noticeRepository.save(notice);

		for (Account account : toList) {
			NoticeRead noticeRead = new NoticeRead();
			noticeRead.setNotice(notice);
			noticeRead.setAccount(account);
			noticeReadRepository.save(noticeRead);
		}
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

}
