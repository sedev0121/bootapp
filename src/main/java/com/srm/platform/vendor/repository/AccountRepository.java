package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.utility.AccountSearchItem;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	Account findOneByUsername(String username);

	Account findOneByEmail(String email);

	Account findOneByMobile(String mobile);

	Account findOneByWeixin(String weixin);

	Account findOneById(Long id);

	@Query(value = "SELECT t.*, u.name unitname FROM account t left join unit u on t.unit_id=u.id left join vendor v on t.vendor_code=v.code WHERE "
			+ "u.name LIKE %?1% or t.username LIKE %?1% or t.realname LIKE %?1% or t.duty LIKE %?1% or t.email LIKE %?1%", countQuery = "SELECT count(t.id) FROM account t left join unit u on t.unit_id=u.id left join vendor v on t.vendor_code=v.code WHERE "
					+ "u.name LIKE %?1% or t.username LIKE %?1% or t.realname LIKE %?1% or t.duty LIKE %?1% or t.email LIKE %?1%", nativeQuery = true)
	Page<Account> findBySearchTerm(String search, Pageable pageable);

	@Query(value = "SELECT id, username, realname FROM account t WHERE "
			+ "t.role='ROLE_BUYER' and t.username LIKE %?1% or t.realname LIKE %?1%", nativeQuery = true)
	Page<AccountSearchItem> findForAutoComplete(String search, Pageable pageable);

	@Query(value = "SELECT id, username, realname FROM account t WHERE " + "t.username in ?1", nativeQuery = true)
	List<AccountSearchItem> findAccountsByUsernames(List<String> usernames);

	@Query(value = "SELECT * FROM account where vendor_code=?1", nativeQuery = true)
	List<Account> findAccountsByVendor(String vendorCode);

	@Query(value = "SELECT * FROM account where unit_id in ?1", nativeQuery = true)
	List<Account> findAccountsByUnitIdList(List<String> unitIdList);

	@Query(value = "SELECT * FROM account where role='ROLE_VENDOR' and unit_id in ?1", nativeQuery = true)
	List<Account> findAllVendorsByUnitIdList(List<String> unitIdList);

	@Query(value = "SELECT * FROM account where vendor_code in ?1", nativeQuery = true)
	List<Account> findAccountsByVendorCodeList(List<String> vendorCodeList);

	@Query(value = "SELECT * FROM account where role<>'ROLE_VENDOR'", nativeQuery = true)
	List<Account> findAllExceptVendor();

	@Query(value = "SELECT * FROM account where id in ?1", nativeQuery = true)
	List<Account> findAllByIdList(List<Long> idList);
}