package com.srm.platform.vendor.service;

import java.time.Instant;
import java.util.Collections;

import javax.annotation.PostConstruct;

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

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.PermissionGroup;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.PermissionGroupRepository;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AccountService implements UserDetailsService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PermissionGroupRepository permissionGroupRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostConstruct
	protected void initialize() {

		Account temp = accountRepository.findOneByUsername("buyer");
		if (temp == null) {
			temp = new Account("buyer", "111", "ROLE_BUYER");
			temp.setReal_name("采购员");
			temp.setEmail("buyer@gmail.com");
			temp.setSkype("buyer");
			temp.setQq("buyer@qq.com");
			temp.setAddress("北京市朝阳区建国门外大街12号14-3");
			temp.setTel("010-8828-1111");
			temp.setMobile("15940800833");
			temp.setWangwang("buyer@wangwang.com");
			temp.setGtalk("buyer@gtalk.com");
			temp.setYahoo("buyer@yahoo.com");
			temp.setEntry_time(Instant.now());
			save(temp);
		}
		temp = accountRepository.findOneByUsername("vendor");
		if (temp == null) {
			temp = new Account("vendor", "111", "ROLE_VENDOR");
			temp.setReal_name("采购员");
			temp.setEmail("vendor@gmail.com");
			temp.setSkype("vendor");
			temp.setQq("vendor@qq.com");
			temp.setAddress("北京市朝阳区建国门外大街12号14-1");
			temp.setTel("010-8828-1112");
			temp.setMobile("15940800834");
			temp.setWangwang("vendor@wangwang.com");
			temp.setGtalk("vendor@gtalk.com");
			temp.setYahoo("vendor@yahoo.com");
			temp.setEntry_time(Instant.now());
			save(temp);
		}
		temp = accountRepository.findOneByUsername("admin");
		if (temp == null) {
			temp = new Account("admin", "111", "ROLE_ADMIN");
			temp.setReal_name("管理员");
			temp.setEmail("admin@gmail.com");
			temp.setSkype("admin");
			temp.setQq("admin@qq.com");
			temp.setAddress("北京市朝阳区建国门外大街12号14-4");
			temp.setTel("010-8828-1121");
			temp.setMobile("15940801833");
			temp.setWangwang("admin@wangwang.com");
			temp.setGtalk("admin@gtalk.com");
			temp.setYahoo("admin@yahoo.com");
			temp.setEntry_time(Instant.now());
			save(temp);
		}

		PermissionGroup tempGroup = permissionGroupRepository.findOneByName("权限组1");
		if (tempGroup == null) {
			tempGroup = new PermissionGroup("权限组1", "权限组1");
			permissionGroupRepository.save(tempGroup);
		}
		tempGroup = permissionGroupRepository.findOneByName("权限组2");
		if (tempGroup == null) {
			tempGroup = new PermissionGroup("权限组2", "权限组2");
			permissionGroupRepository.save(tempGroup);
		}
		tempGroup = permissionGroupRepository.findOneByName("权限组3");
		if (tempGroup == null) {
			tempGroup = new PermissionGroup("权限组3", "权限组3");
			permissionGroupRepository.save(tempGroup);
		}
	}

	@Transactional
	public Account save(Account account) {
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		accountRepository.save(account);
		return account;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = accountRepository.findOneByUsername(username);
		if (account == null) {
			throw new UsernameNotFoundException("user not found");
		}
		return createUser(account);
	}

	public void signin(Account account) {
		SecurityContextHolder.getContext().setAuthentication(authenticate(account));
	}

	private Authentication authenticate(Account account) {
		return new UsernamePasswordAuthenticationToken(createUser(account), null,
				Collections.singleton(createAuthority(account)));
	}

	private User createUser(Account account) {
		return new User(account.getUsername(), account.getPassword(), Collections.singleton(createAuthority(account)));
	}

	private GrantedAuthority createAuthority(Account account) {
		return new SimpleGrantedAuthority(account.getRole());
	}

}
