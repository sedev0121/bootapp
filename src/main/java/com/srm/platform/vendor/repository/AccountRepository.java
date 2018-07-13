package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.utility.AccountSearchItem;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	Account findOneByUsername(String username);

	Account findOneByEmail(String email);

	Account findOneById(Long id);

	@Query(value = "SELECT * FROM account t WHERE " + "t.username LIKE '% \\?1%'", nativeQuery = true)
	Page<Account> findBySearchTermNative(String searchTerm, Pageable pageable);

	@Query(value = "SELECT * FROM account t WHERE "
			+ "t.username LIKE CONCAT('%',:searchTerm, '%')", nativeQuery = true)
	Page<Account> findBySearchTermNative2(@Param("searchTerm") String searchTerm, Pageable pageable);

	@Query("select d from #{#entityName} d where d.username= :username")
	Page<Account> findByUsername2(@Param("username") final String username, Pageable pageable);

	@Query(value = "SELECT t.*, u.name unitname FROM account t left join unit u on t.unit_id=u.id left join vendor v on t.vendor_code=v.code WHERE "
			+ "u.name LIKE %?1% or t.username LIKE %?1% or t.realname LIKE %?1% or t.duty LIKE %?1% or t.email LIKE %?1%", nativeQuery = true)
	Page<Account> findBySearchTerm(String search, Pageable pageable);

	@Query(value = "SELECT id, username, realname FROM account t WHERE "
			+ "t.username LIKE %?1% or t.realname LIKE %?1%", nativeQuery = true)
	Page<AccountSearchItem> findForAutoComplete(String search, Pageable pageable);

	@Query(value = "SELECT id, username, realname FROM account t WHERE " + "t.username in ?1", nativeQuery = true)
	List<AccountSearchItem> findAccountsByUsernames(List<String> usernames);
}