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
import com.srm.platform.vendor.model.Action;
import com.srm.platform.vendor.model.Function;
import com.srm.platform.vendor.model.FunctionAction;
import com.srm.platform.vendor.model.PermissionGroup;
import com.srm.platform.vendor.model.Unit;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.ActionRepository;
import com.srm.platform.vendor.repository.FunctionActionRepository;
import com.srm.platform.vendor.repository.FunctionRepository;
import com.srm.platform.vendor.repository.PermissionGroupRepository;
import com.srm.platform.vendor.repository.UnitRepository;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AccountService implements UserDetailsService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PermissionGroupRepository permissionGroupRepository;

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

	@PostConstruct
	protected void initialize() {

		Unit tempUnit = unitRepository.findOneByName("美尔凯特");
		if (tempUnit == null) {
			tempUnit = new Unit("美尔凯特", 0L);
			tempUnit.setId(1L);
			tempUnit = unitRepository.save(tempUnit);
		}

		Long root_id = tempUnit.getId();
		tempUnit = unitRepository.findOneByName("采购部");
		if (tempUnit == null) {
			tempUnit = new Unit("采购部", root_id);
			tempUnit = unitRepository.save(tempUnit);
		}

		Long buyerId = tempUnit.getId();
		tempUnit = unitRepository.findOneByName("采购一组");
		if (tempUnit == null) {
			tempUnit = new Unit("采购一组", buyerId);
			tempUnit = unitRepository.save(tempUnit);
		}

		tempUnit = unitRepository.findOneByName("采购二组");
		if (tempUnit == null) {
			tempUnit = new Unit("采购二组", buyerId);
			tempUnit = unitRepository.save(tempUnit);
		}

		tempUnit = unitRepository.findOneByName("提供商");
		if (tempUnit == null) {
			tempUnit = new Unit("提供商", root_id);
			tempUnit = unitRepository.save(tempUnit);
		}

		tempUnit = unitRepository.findOneByName("销售部");
		if (tempUnit == null) {
			tempUnit = new Unit("销售部", root_id);
			tempUnit = unitRepository.save(tempUnit);
		}

		Account temp = accountRepository.findOneByUsername("buyer");
		if (temp == null) {
			temp = new Account("buyer", "111", "ROLE_BUYER");
			temp.setRealname("采购员");
			temp.setDuty("采购经理");
			temp.setEmail("buyer@gmail.com");
			temp.setSkype("buyer");
			temp.setQq("buyer@qq.com");
			temp.setAddress("北京市朝阳区建国门外大街12号14-3");
			temp.setTel("010-8828-1111");
			temp.setMobile("15940800833");
			temp.setWangwang("buyer@wangwang.com");
			temp.setGtalk("buyer@gtalk.com");
			temp.setYahoo("buyer@yahoo.com");
			temp.setUnit(tempUnit);
			temp.setEntryTime(Instant.now());
			save(temp);
		}
		temp = accountRepository.findOneByUsername("vendor");
		if (temp == null) {
			temp = new Account("vendor", "111", "ROLE_VENDOR");
			temp.setRealname("供应商");
			temp.setDuty("销售经理");
			temp.setEmail("vendor@gmail.com");
			temp.setSkype("vendor");
			temp.setQq("vendor@qq.com");
			temp.setAddress("北京市朝阳区建国门外大街12号14-1");
			temp.setTel("010-8828-1112");
			temp.setMobile("15940800834");
			temp.setWangwang("vendor@wangwang.com");
			temp.setGtalk("vendor@gtalk.com");
			temp.setYahoo("vendor@yahoo.com");
			temp.setUnit(tempUnit);
			temp.setEntryTime(Instant.now());
			save(temp);
		}
		temp = accountRepository.findOneByUsername("admin");
		if (temp == null) {
			temp = new Account("admin", "111", "ROLE_ADMIN");
			temp.setRealname("管理员");
			temp.setDuty("副总经理");
			temp.setEmail("admin@gmail.com");
			temp.setSkype("admin");
			temp.setQq("admin@qq.com");
			temp.setAddress("北京市朝阳区建国门外大街12号14-4");
			temp.setTel("010-8828-1121");
			temp.setMobile("15940801833");
			temp.setWangwang("admin@wangwang.com");
			temp.setGtalk("admin@gtalk.com");
			temp.setYahoo("admin@yahoo.com");
			temp.setUnit(tempUnit);
			temp.setEntryTime(Instant.now());
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

		Function tempFunction = functionRepository.findOneByName("供应商档案管理");
		if (tempFunction == null) {
			tempFunction = new Function("供应商档案管理");
			functionRepository.save(tempFunction);
		}
		tempFunction = functionRepository.findOneByName("询价管理");
		if (tempFunction == null) {
			tempFunction = new Function("询价管理");
			functionRepository.save(tempFunction);
		}
		tempFunction = functionRepository.findOneByName("报价管理");
		if (tempFunction == null) {
			tempFunction = new Function("报价管理");
			functionRepository.save(tempFunction);
		}
		tempFunction = functionRepository.findOneByName("采购订单发布");
		if (tempFunction == null) {
			tempFunction = new Function("采购订单发布");
			functionRepository.save(tempFunction);
		}
		tempFunction = functionRepository.findOneByName("对账单管理");
		if (tempFunction == null) {
			tempFunction = new Function("对账单管理");
			functionRepository.save(tempFunction);
		}
		tempFunction = functionRepository.findOneByName("出货看板");
		if (tempFunction == null) {
			tempFunction = new Function("出货看板");
			functionRepository.save(tempFunction);
		}

		Action tempAction = actionRepository.findOneByName("查询");
		if (tempAction == null) {
			tempAction = new Action("查询");
			actionRepository.save(tempAction);
		}
		tempAction = actionRepository.findOneByName("新建");
		if (tempAction == null) {
			tempAction = new Action("新建");
			actionRepository.save(tempAction);
		}
		tempAction = actionRepository.findOneByName("审核");
		if (tempAction == null) {
			tempAction = new Action("审核");
			actionRepository.save(tempAction);
		}
		tempAction = actionRepository.findOneByName("退回");
		if (tempAction == null) {
			tempAction = new Action("退回");
			actionRepository.save(tempAction);
		}
		tempAction = actionRepository.findOneByName("归档");
		if (tempAction == null) {
			tempAction = new Action("归档");
			actionRepository.save(tempAction);
		}
		tempAction = actionRepository.findOneByName("拒绝");
		if (tempAction == null) {
			tempAction = new Action("拒绝");
			actionRepository.save(tempAction);
		}
		tempAction = actionRepository.findOneByName("导出");
		if (tempAction == null) {
			tempAction = new Action("导出");
			actionRepository.save(tempAction);
		}

		tempFunction = functionRepository.findOneByName("供应商档案管理");
		tempAction = actionRepository.findOneByName("查询");
		FunctionAction tempFunctionAction = functionActionRepository.findOne(tempFunction.getId(), tempAction.getId());
		if (tempFunctionAction == null) {
			tempFunctionAction = new FunctionAction(tempFunction.getId(), tempAction.getId());
			functionActionRepository.save(tempFunctionAction);
		}
		tempFunction = functionRepository.findOneByName("询价管理");
		tempAction = actionRepository.findOneByName("查询");
		tempFunctionAction = functionActionRepository.findOne(tempFunction.getId(), tempAction.getId());
		if (tempFunctionAction == null) {
			tempFunctionAction = new FunctionAction(tempFunction.getId(), tempAction.getId());
			functionActionRepository.save(tempFunctionAction);
		}
		tempFunction = functionRepository.findOneByName("询价管理");
		tempAction = actionRepository.findOneByName("新建");
		tempFunctionAction = functionActionRepository.findOne(tempFunction.getId(), tempAction.getId());
		if (tempFunctionAction == null) {
			tempFunctionAction = new FunctionAction(tempFunction.getId(), tempAction.getId());
			functionActionRepository.save(tempFunctionAction);
		}
		tempFunction = functionRepository.findOneByName("询价管理");
		tempAction = actionRepository.findOneByName("归档");
		tempFunctionAction = functionActionRepository.findOne(tempFunction.getId(), tempAction.getId());
		if (tempFunctionAction == null) {
			tempFunctionAction = new FunctionAction(tempFunction.getId(), tempAction.getId());
			functionActionRepository.save(tempFunctionAction);
		}
		tempFunction = functionRepository.findOneByName("询价管理");
		tempAction = actionRepository.findOneByName("审核");
		tempFunctionAction = functionActionRepository.findOne(tempFunction.getId(), tempAction.getId());
		if (tempFunctionAction == null) {
			tempFunctionAction = new FunctionAction(tempFunction.getId(), tempAction.getId());
			functionActionRepository.save(tempFunctionAction);
		}
		tempFunction = functionRepository.findOneByName("报价管理");
		tempAction = actionRepository.findOneByName("查询");
		tempFunctionAction = functionActionRepository.findOne(tempFunction.getId(), tempAction.getId());
		if (tempFunctionAction == null) {
			tempFunctionAction = new FunctionAction(tempFunction.getId(), tempAction.getId());
			functionActionRepository.save(tempFunctionAction);
		}
		tempFunction = functionRepository.findOneByName("采购订单发布");
		tempAction = actionRepository.findOneByName("查询");
		tempFunctionAction = functionActionRepository.findOne(tempFunction.getId(), tempAction.getId());
		if (tempFunctionAction == null) {
			tempFunctionAction = new FunctionAction(tempFunction.getId(), tempAction.getId());
			functionActionRepository.save(tempFunctionAction);
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
