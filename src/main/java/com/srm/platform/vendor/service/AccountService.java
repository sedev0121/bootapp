package com.srm.platform.vendor.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.PasswordResetToken;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.ActionRepository;
import com.srm.platform.vendor.repository.FunctionActionRepository;
import com.srm.platform.vendor.repository.FunctionRepository;
import com.srm.platform.vendor.repository.PasswordResetTokenRepository;
import com.srm.platform.vendor.repository.PermissionGroupFunctionUnitRepository;
import com.srm.platform.vendor.repository.PermissionGroupRepository;
import com.srm.platform.vendor.repository.UnitRepository;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.PermissionItem;
import com.srm.platform.vendor.utility.PermissionUnit;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AccountService implements UserDetailsService {

	public final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PasswordResetTokenRepository passwordTokenRepository;

	@Autowired
	private PermissionGroupRepository permissionGroupRepository;

	@Autowired
	private PermissionGroupFunctionUnitRepository permissionGroupFunctionUnitRepository;

	@Autowired
	private FunctionRepository functionRepository;

	@Autowired
	private ActionRepository actionRepository;

	@Autowired
	private UnitRepository unitRepository;

	@Autowired
	private FunctionActionRepository functionActionRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private HttpSession httpSession;

	@Transactional
	public Account save(Account account) {
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		accountRepository.save(account);
		return account;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = accountRepository.findOneByUsername(username);
		if (account == null || account.getState() != 1) {
			throw new UsernameNotFoundException("user not found");
		}
		return createUser(account);
	}

	public Account loadUserByEmail(String email) throws UsernameNotFoundException {
		Account account = accountRepository.findOneByEmail(email);
		return account;
	}

	public void createPasswordResetTokenForUser(Account account, String token) {
		PasswordResetToken myToken = new PasswordResetToken(account, token);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 3);
		myToken.setExpireDate(cal.getTime());
		passwordTokenRepository.save(myToken);
	}

	public String validatePasswordResetToken(long id, String token) {
		PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
		if ((passToken == null) || (passToken.getAccount().getId() != id)) {
			return "invalidToken";
		}

		Calendar cal = Calendar.getInstance();
		if ((passToken.getExpireDate().getTime() - cal.getTime().getTime()) <= 0) {
			return "expired";
		}

		return null;
	}

	public void signin(Account account) {
		SecurityContextHolder.getContext().setAuthentication(authenticate(account));
	}

	private Authentication authenticate(Account account) {
		return new UsernamePasswordAuthenticationToken(createUser(account), null, createAuthorities(account));
	}

	private User createUser(Account account) {
		httpSession.setAttribute("account", account);
		httpSession.setAttribute("realname", account.getRealname());
		return new User(account.getUsername(), account.getPassword(), createAuthorities(account));
	}

	private List<GrantedAuthority> createAuthorities(Account account) {

		List<PermissionUnit> permissionUnitList = permissionGroupFunctionUnitRepository
				.findPermissionUnitsForAccount(account.getId());
		for (PermissionUnit unit : permissionUnitList) {
			httpSession.setAttribute(unit.getName(), unit.getUnits());
		}

		String myUnitList = String.valueOf(account.getUnit().getId());
		myUnitList = StringUtils.append(myUnitList, "," + searchChildren(myUnitList));

		httpSession.setAttribute(Constants.KEY_DEFAULT_UNIT_LIST, myUnitList);

		List<PermissionItem> permissions = permissionGroupRepository.findPermissionForAccount(account.getId());
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(account.getRole()));

		for (PermissionItem item : permissions) {
			authorities.add(new SimpleGrantedAuthority(item.getFunction() + "-" + item.getAction()));
		}

		return authorities;
	}

	private String searchChildren(String parentIdList) {
		String childList = "";
		List<PermissionUnit> unitList = permissionGroupFunctionUnitRepository
				.findChildrenByParentId(StringUtils.split(parentIdList, ","));
		for (PermissionUnit unit : unitList) {
			if (unit != null)
				childList = StringUtils.append(childList, "," + unit.getUnits());
		}

		if (childList.isEmpty()) {
			return childList;
		} else {
			childList = StringUtils.append(childList, "," + searchChildren(childList));
			return childList;
		}
	}

}
