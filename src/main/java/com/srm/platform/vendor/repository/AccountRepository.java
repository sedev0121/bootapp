package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.srm.platform.vendor.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	Account findOneByUsername(String username);

	Account findOneById(Long id);

	@Query(value = "SELECT * FROM account t WHERE " + "t.username LIKE '% \\?1%'", nativeQuery = true)
	Page<Account> findBySearchTermNative(String searchTerm, Pageable pageable);

	@Query(value = "SELECT * FROM account t WHERE "
			+ "t.username LIKE CONCAT('%',:searchTerm, '%')", nativeQuery = true)
	Page<Account> findBySearchTermNative2(@Param("searchTerm") String searchTerm, Pageable pageable);

	@Query("select d from #{#entityName} d where d.username= :username")
	Page<Account> findByUsername2(@Param("username") final String username, Pageable pageable);

	@Query(value = "SELECT * FROM account t WHERE "
			+ "t.username LIKE CONCAT('%',:search, '%') or t.real_name LIKE CONCAT('%',:search, '%') or t.email LIKE CONCAT('%',:search, '%')", nativeQuery = true)
	Page<Account> findBySearchTerm(@Param("search") String search, Pageable pageable);

	@Query(value = "SELECT id, real_name, username FROM account t WHERE "
			+ "t.username LIKE CONCAT('%',:search, '%') or t.real_name LIKE CONCAT('%',:search, '%')", nativeQuery = true)
	Page<Account> findForAutoComplete(@Param("search") String search, Pageable pageable);
}